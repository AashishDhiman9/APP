package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.screens.*
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPinkPrimary
import com.example.ui.theme.PastelRoseAccent
import com.example.ui.theme.SoftTextDark
import com.example.ui.viewmodel.MyLilluViewModel

@Composable
fun MainScreen(viewModel: MyLilluViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val webApps by viewModel.webApps.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeWebApp by viewModel.activeWebApp.collectAsStateWithLifecycle()
    val showAddDialog by viewModel.showAddDialog.collectAsStateWithLifecycle()
    val latestAlert by viewModel.latestAlert.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val latestStickyNote by viewModel.latestStickyNote.collectAsStateWithLifecycle()

    var currentTab by remember { mutableIntStateOf(0) }
    var isLoggingIn by remember { mutableStateOf(false) }

    if (isLoggingIn) {
        LoginScreen(
            currentRole = currentUser,
            onLoginSuccess = { role ->
                viewModel.switchUser(role)
                isLoggingIn = false
            }
        )
    } else if (activeWebApp != null) {
        WebViewScreen(
            webApp = activeWebApp!!,
            onClose = { viewModel.closeWebApp() }
        )
    } else {
        Scaffold(
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp),
                    color = Color.White
                ) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentTab == 0,
                            onClick = { currentTab = 0 },
                            icon = {
                                Icon(
                                    if (currentTab == 0) Icons.Filled.GridView else Icons.Outlined.GridView,
                                    contentDescription = "Hub"
                                )
                            },
                            label = { Text("Hub", fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PastelPinkPrimary,
                                selectedTextColor = PastelPinkPrimary,
                                indicatorColor = PastelPinkPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_hub")
                        )

                        NavigationBarItem(
                            selected = currentTab == 1,
                            onClick = { currentTab = 1 },
                            icon = {
                                Icon(
                                    if (currentTab == 1) Icons.Filled.Chat else Icons.Outlined.Chat,
                                    contentDescription = "Chat"
                                )
                            },
                            label = { Text("Chat", fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PastelPinkPrimary,
                                selectedTextColor = PastelPinkPrimary,
                                indicatorColor = PastelPinkPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_chat")
                        )

                        NavigationBarItem(
                            selected = currentTab == 2,
                            onClick = { currentTab = 2 },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (unreadCount > 0) {
                                            Badge(
                                                containerColor = PastelRoseAccent,
                                                contentColor = Color.White
                                            ) {
                                                Text(text = unreadCount.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        if (currentTab == 2) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                        contentDescription = "Activity"
                                    )
                                }
                            },
                            label = { Text("Alerts", fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PastelPinkPrimary,
                                selectedTextColor = PastelPinkPrimary,
                                indicatorColor = PastelPinkPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_alerts")
                        )

                        NavigationBarItem(
                            selected = currentTab == 3,
                            onClick = { currentTab = 3 },
                            icon = {
                                Icon(
                                    if (currentTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text("Profile", fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PastelPinkPrimary,
                                selectedTextColor = PastelPinkPrimary,
                                indicatorColor = PastelPinkPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_profile")
                        )
                    }
                }
            },
            containerColor = PastelPinkBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    0 -> DashboardScreen(
                        currentUser = currentUser,
                        webApps = webApps,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        latestAlert = latestAlert,
                        showAddDialog = showAddDialog,
                        onCategorySelected = { viewModel.setSelectedCategory(it) },
                        onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                        onOpenWebApp = { viewModel.openWebApp(it) },
                        onDeleteWebApp = { viewModel.deleteWebApp(it) },
                        onShowAddDialogChanged = { viewModel.setShowAddDialog(it) },
                        onAddNewWebApp = { title, url, desc, cat, emoji ->
                            viewModel.addNewWebApp(title, url, desc, cat, emoji)
                        },
                        onSwitchUserClick = { isLoggingIn = true },
                        onDismissAlert = { viewModel.dismissAlert() }
                    )

                    1 -> ChatScreen(
                        currentUser = currentUser,
                        chatMessages = chatMessages,
                        latestStickyNote = latestStickyNote,
                        onSendMessage = { text, sticker ->
                            viewModel.sendChatMessage(text, sticker)
                        },
                        onPostStickyNote = { viewModel.postStickyNote(it) },
                        onToggleHeart = { viewModel.toggleHeartMessage(it) },
                        onDeleteMessage = { viewModel.deleteMessage(it) },
                        onSwitchUserClick = { isLoggingIn = true }
                    )

                    2 -> NotificationScreen(
                        currentUser = currentUser,
                        notifications = notifications,
                        onMarkAllRead = { viewModel.markNotificationsRead() },
                        onClearAll = { viewModel.clearNotifications() }
                    )

                    3 -> ProfileScreen(
                        currentUser = currentUser,
                        onSwitchUserClick = { isLoggingIn = true }
                    )
                }
            }
        }
    }
}
