package com.example.data

import android.content.Context
import android.util.Log
import com.example.data.dao.ChatMessageDao
import com.example.data.dao.ViewingNotificationDao
import com.example.data.dao.WebAppDao
import com.example.data.model.ChatMessage
import com.example.data.model.ViewingNotification
import com.example.data.model.WebApp
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.math.abs

class Repository(
    context: Context,
    private val webAppDao: WebAppDao,
    private val viewingNotificationDao: ViewingNotificationDao,
    private val chatMessageDao: ChatMessageDao
) {
    private val firestore: FirebaseFirestore? = runCatching {
        FirebaseApp.initializeApp(context.applicationContext)
        FirebaseFirestore.getInstance()
    }.onFailure {
        Log.w(TAG, "Firebase is not configured; using local Room storage only.", it)
    }.getOrNull()

    private val webAppsCollection = firestore?.collection("web_apps")
    private val notificationsCollection = firestore?.collection("viewing_notifications")
    private val chatCollection = firestore?.collection("chat_messages")

    val allWebApps: Flow<List<WebApp>> = webAppsCollection?.snapshots(
        orderBy = "dateAdded",
        direction = Query.Direction.DESCENDING
    ) { it.toObject(WebApp::class.java) } ?: webAppDao.getAllWebApps()

    val allNotifications: Flow<List<ViewingNotification>> = notificationsCollection?.snapshots(
        orderBy = "timestamp",
        direction = Query.Direction.DESCENDING
    ) { it.toObject(ViewingNotification::class.java) } ?: viewingNotificationDao.getAllNotifications()

    val unreadNotificationsCount: Flow<Int> = if (notificationsCollection != null) {
        allNotifications.map { notifications -> notifications.count { !it.isRead } }
    } else {
        viewingNotificationDao.getUnreadCount()
    }

    val allChatMessages: Flow<List<ChatMessage>> = chatCollection?.snapshots(
        orderBy = "timestamp",
        direction = Query.Direction.ASCENDING
    ) { it.toObject(ChatMessage::class.java) } ?: chatMessageDao.getAllMessages()

    val latestStickyNote: Flow<ChatMessage?> = if (chatCollection != null) {
        allChatMessages.map { messages ->
            messages.filter { it.isStickyNote }.maxByOrNull { it.timestamp }
        }
    } else {
        chatMessageDao.getLatestStickyNote()
    }

    suspend fun initializePrepopulatedDataIfNeeded() {
        val currentApps = allWebApps.first()
        if (currentApps.isEmpty()) {
            val defaultApps = listOf(
                WebApp(
                    id = 101,
                    title = "Lillu's Memory Lane 💌",
                    url = "https://example.com/lillu-memories",
                    description = "A photo & memory timeline website built just for you.",
                    category = "Memories 💌",
                    emoji = "💖"
                ),
                WebApp(
                    id = 102,
                    title = "Daily Mood Booster 🌸",
                    url = "https://example.com/daily-affirmations",
                    description = "Interactive card drawer with daily sweet notes and positivity.",
                    category = "Surprise ✨",
                    emoji = "🌟"
                ),
                WebApp(
                    id = 103,
                    title = "Lillu's Custom Wordle 🎮",
                    url = "https://example.com/lillu-wordle",
                    description = "Custom word puzzle app loaded with inside jokes and favorite words.",
                    category = "Games 🎮",
                    emoji = "🕹️"
                ),
                WebApp(
                    id = 104,
                    title = "Aesthetic Chill Jukebox 🎵",
                    url = "https://example.com/jukebox",
                    description = "Interactive music player web app with cozy lofi tunes.",
                    category = "Music 🎵",
                    emoji = "🎧"
                ),
                WebApp(
                    id = 105,
                    title = "Infinite Compliment Generator 📝",
                    url = "https://example.com/compliments",
                    description = "Click the magic button to reveal cute reasons why you're awesome!",
                    category = "Notes 📝",
                    emoji = "💌"
                )
            )
            defaultApps.forEach { insertWebApp(it) }
        }

        val currentMessages = allChatMessages.first()
        if (currentMessages.isEmpty()) {
            insertMessage(
                ChatMessage(
                    id = 201,
                    senderId = "ADMIN",
                    senderName = "Aashish",
                    messageText = "Hey Lillu! ✨ Welcome to your custom app hub! All the web apps I made for you are saved right here in your dashboard. 💖",
                    stickerEmoji = "🌸"
                )
            )
            insertMessage(
                ChatMessage(
                    id = 202,
                    senderId = "ADMIN",
                    senderName = "Aashish",
                    messageText = "💌 Sticky Note: Tap any link in your dashboard to view the webapp! Whenever you open one, I get a real-time viewing notification on my admin feed! 🥰",
                    isStickyNote = true
                )
            )
        }
    }

    suspend fun addWebApp(title: String, url: String, description: String, category: String, emoji: String) {
        val webApp = WebApp(
            id = newSharedId(),
            title = title,
            url = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url,
            description = description,
            category = category,
            emoji = emoji
        )
        insertWebApp(webApp)
    }

    suspend fun deleteWebApp(id: Int) {
        if (webAppsCollection != null) {
            webAppsCollection.document(id.toString()).delete()
        } else {
            webAppDao.deleteWebAppById(id)
        }
    }

    suspend fun recordWebAppAccess(webApp: WebApp, accessedBy: String) {
        val updatedWebApp = webApp.copy(
            accessCount = webApp.accessCount + 1,
            lastAccessedTime = System.currentTimeMillis()
        )
        if (webAppsCollection != null) {
            webAppsCollection.document(webApp.id.toString()).set(updatedWebApp)
        } else {
            webAppDao.recordAccess(webApp.id)
        }

        if (accessedBy == "Lillu" || accessedBy == "Lillu ✨") {
            insertNotification(
                ViewingNotification(
                    id = newSharedId(),
                    webAppId = webApp.id,
                    webAppTitle = webApp.title,
                    accessedBy = accessedBy,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun markNotificationsRead() {
        if (notificationsCollection != null) {
            allNotifications.first().forEach { notification ->
                if (!notification.isRead) {
                    notificationsCollection.document(notification.id.toString()).set(notification.copy(isRead = true))
                }
            }
        } else {
            viewingNotificationDao.markAllAsRead()
        }
    }

    suspend fun clearNotifications() {
        if (notificationsCollection != null) {
            allNotifications.first().forEach { notification ->
                notificationsCollection.document(notification.id.toString()).delete()
            }
        } else {
            viewingNotificationDao.clearAllNotifications()
        }
    }

    suspend fun sendChatMessage(senderId: String, senderName: String, text: String, sticker: String? = null) {
        if (text.isBlank() && sticker == null) return
        insertMessage(
            ChatMessage(
                id = newSharedId(),
                senderId = senderId,
                senderName = senderName,
                messageText = text.trim(),
                stickerEmoji = sticker
            )
        )
    }

    suspend fun postStickyNote(senderName: String, noteText: String) {
        insertMessage(
            ChatMessage(
                id = newSharedId(),
                senderId = "ADMIN",
                senderName = senderName,
                messageText = noteText.trim(),
                isStickyNote = true
            )
        )
    }

    suspend fun toggleHeartMessage(id: Int) {
        if (chatCollection != null) {
            allChatMessages.first().firstOrNull { it.id == id }?.let { message ->
                chatCollection.document(id.toString()).set(message.copy(isHearted = !message.isHearted))
            }
        } else {
            chatMessageDao.toggleHeart(id)
        }
    }

    suspend fun deleteChatMessage(id: Int) {
        if (chatCollection != null) {
            chatCollection.document(id.toString()).delete()
        } else {
            chatMessageDao.deleteMessage(id)
        }
    }

    private suspend fun insertWebApp(webApp: WebApp) {
        if (webAppsCollection != null) {
            webAppsCollection.document(webApp.id.toString()).set(webApp)
        } else {
            webAppDao.insertWebApp(webApp)
        }
    }

    private suspend fun insertMessage(message: ChatMessage) {
        if (chatCollection != null) {
            chatCollection.document(message.id.toString()).set(message)
        } else {
            chatMessageDao.insertMessage(message)
        }
    }

    private suspend fun insertNotification(notification: ViewingNotification) {
        if (notificationsCollection != null) {
            notificationsCollection.document(notification.id.toString()).set(notification)
        } else {
            viewingNotificationDao.insertNotification(notification)
        }
    }

    private fun newSharedId(): Int = abs((System.currentTimeMillis().toString() + (0..9999).random()).hashCode())

    private fun <T> com.google.firebase.firestore.CollectionReference.snapshots(
        orderBy: String,
        direction: Query.Direction,
        mapper: (com.google.firebase.firestore.DocumentSnapshot) -> T?
    ): Flow<List<T>> = callbackFlow {
        var registration: ListenerRegistration? = null
        registration = this@snapshots
            .orderBy(orderBy, direction)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Firestore listener failed for ${this@snapshots.path}.", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.mapNotNull(mapper).orEmpty())
            }
        awaitClose { registration?.remove() }
    }

    companion object {
        private const val TAG = "Repository"
    }
}
