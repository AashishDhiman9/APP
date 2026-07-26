package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Repository
import com.example.data.model.ChatMessage
import com.example.data.model.UserRole
import com.example.data.model.ViewingNotification
import com.example.data.model.WebApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyLilluViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = Repository(
        db.webAppDao(),
        db.viewingNotificationDao(),
        db.chatMessageDao()
    )

    // Current active user
    private val _currentUser = MutableStateFlow(UserRole.LILLU)
    val currentUser: StateFlow<UserRole> = _currentUser.asStateFlow()

    // Filter category
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Currently opened WebApp for WebView screen
    private val _activeWebApp = MutableStateFlow<WebApp?>(null)
    val activeWebApp: StateFlow<WebApp?> = _activeWebApp.asStateFlow()

    // Admin Add WebApp Dialog state
    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    // Real-time toast alert state for Admin when a new access occurs
    private val _latestAlert = MutableStateFlow<ViewingNotification?>(null)
    val latestAlert: StateFlow<ViewingNotification?> = _latestAlert.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializePrepopulatedDataIfNeeded()
        }

        // Observe new notifications to show live top alert banner for Admin
        viewModelScope.launch {
            repository.allNotifications.collect { list ->
                if (list.isNotEmpty() && _currentUser.value == UserRole.ADMIN) {
                    val newest = list.first()
                    // If less than 10 seconds old
                    if (System.currentTimeMillis() - newest.timestamp < 10000) {
                        _latestAlert.value = newest
                    }
                }
            }
        }
    }

    val webApps: StateFlow<List<WebApp>> = combine(
        repository.allWebApps,
        _selectedCategory,
        _searchQuery
    ) { apps, category, query ->
        apps.filter { app ->
            val matchesCategory = (category == "All" || app.category.contains(category, ignoreCase = true))
            val matchesQuery = query.isBlank() ||
                    app.title.contains(query, ignoreCase = true) ||
                    app.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val notifications: StateFlow<List<ViewingNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val chatMessages: StateFlow<List<ChatMessage>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestStickyNote: StateFlow<ChatMessage?> = repository.latestStickyNote
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun switchUser(role: UserRole) {
        _currentUser.value = role
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openWebApp(webApp: WebApp) {
        _activeWebApp.value = webApp
        viewModelScope.launch {
            repository.recordWebAppAccess(webApp, _currentUser.value.displayName)
        }
    }

    fun closeWebApp() {
        _activeWebApp.value = null
    }

    fun setShowAddDialog(show: Boolean) {
        _showAddDialog.value = show
    }

    fun addNewWebApp(title: String, url: String, description: String, category: String, emoji: String) {
        viewModelScope.launch {
            repository.addWebApp(title, url, description, category, emoji)
            _showAddDialog.value = false
        }
    }

    fun deleteWebApp(id: Int) {
        viewModelScope.launch {
            repository.deleteWebApp(id)
        }
    }

    fun sendChatMessage(text: String, stickerEmoji: String? = null) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.sendChatMessage(
                senderId = user.name,
                senderName = user.displayName,
                text = text,
                sticker = stickerEmoji
            )
        }
    }

    fun postStickyNote(noteText: String) {
        viewModelScope.launch {
            repository.postStickyNote(_currentUser.value.displayName, noteText)
        }
    }

    fun toggleHeartMessage(id: Int) {
        viewModelScope.launch {
            repository.toggleHeartMessage(id)
        }
    }

    fun deleteMessage(id: Int) {
        viewModelScope.launch {
            repository.deleteChatMessage(id)
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead()
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }

    fun dismissAlert() {
        _latestAlert.value = null
    }
}
