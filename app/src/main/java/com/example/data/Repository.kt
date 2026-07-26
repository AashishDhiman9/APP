package com.example.data

import com.example.data.dao.ChatMessageDao
import com.example.data.dao.ViewingNotificationDao
import com.example.data.dao.WebAppDao
import com.example.data.model.ChatMessage
import com.example.data.model.ViewingNotification
import com.example.data.model.WebApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class Repository(
    private val webAppDao: WebAppDao,
    private val viewingNotificationDao: ViewingNotificationDao,
    private val chatMessageDao: ChatMessageDao
) {
    val allWebApps: Flow<List<WebApp>> = webAppDao.getAllWebApps()
    val allNotifications: Flow<List<ViewingNotification>> = viewingNotificationDao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = viewingNotificationDao.getUnreadCount()
    val allChatMessages: Flow<List<ChatMessage>> = chatMessageDao.getAllMessages()
    val latestStickyNote: Flow<ChatMessage?> = chatMessageDao.getLatestStickyNote()

    suspend fun initializePrepopulatedDataIfNeeded() {
        val currentApps = allWebApps.first()
        if (currentApps.isEmpty()) {
            val defaultApps = listOf(
                WebApp(
                    title = "Lillu's Memory Lane 💌",
                    url = "https://example.com/lillu-memories",
                    description = "A photo & memory timeline website built just for you.",
                    category = "Memories 💌",
                    emoji = "💖"
                ),
                WebApp(
                    title = "Daily Mood Booster 🌸",
                    url = "https://example.com/daily-affirmations",
                    description = "Interactive card drawer with daily sweet notes and positivity.",
                    category = "Surprise ✨",
                    emoji = "🌟"
                ),
                WebApp(
                    title = "Lillu's Custom Wordle 🎮",
                    url = "https://example.com/lillu-wordle",
                    description = "Custom word puzzle app loaded with inside jokes and favorite words.",
                    category = "Games 🎮",
                    emoji = "🕹️"
                ),
                WebApp(
                    title = "Aesthetic Chill Jukebox 🎵",
                    url = "https://example.com/jukebox",
                    description = "Interactive music player web app with cozy lofi tunes.",
                    category = "Music 🎵",
                    emoji = "🎧"
                ),
                WebApp(
                    title = "Infinite Compliment Generator 📝",
                    url = "https://example.com/compliments",
                    description = "Click the magic button to reveal cute reasons why you're awesome!",
                    category = "Notes 📝",
                    emoji = "💌"
                )
            )
            defaultApps.forEach { webAppDao.insertWebApp(it) }
        }

        val currentMessages = allChatMessages.first()
        if (currentMessages.isEmpty()) {
            chatMessageDao.insertMessage(
                ChatMessage(
                    senderId = "ADMIN",
                    senderName = "Aashish",
                    messageText = "Hey Lillu! ✨ Welcome to your custom app hub! All the web apps I made for you are saved right here in your dashboard. 💖",
                    stickerEmoji = "🌸"
                )
            )
            chatMessageDao.insertMessage(
                ChatMessage(
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
            title = title,
            url = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url,
            description = description,
            category = category,
            emoji = emoji
        )
        webAppDao.insertWebApp(webApp)
    }

    suspend fun deleteWebApp(id: Int) {
        webAppDao.deleteWebAppById(id)
    }

    suspend fun recordWebAppAccess(webApp: WebApp, accessedBy: String) {
        webAppDao.recordAccess(webApp.id)
        // Whenever Lillu opens a link, generate a ViewingNotification trigger
        if (accessedBy == "Lillu" || accessedBy == "Lillu ✨") {
            viewingNotificationDao.insertNotification(
                ViewingNotification(
                    webAppId = webApp.id,
                    webAppTitle = webApp.title,
                    accessedBy = accessedBy,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun markNotificationsRead() {
        viewingNotificationDao.markAllAsRead()
    }

    suspend fun clearNotifications() {
        viewingNotificationDao.clearAllNotifications()
    }

    suspend fun sendChatMessage(senderId: String, senderName: String, text: String, sticker: String? = null) {
        if (text.isBlank() && sticker == null) return
        chatMessageDao.insertMessage(
            ChatMessage(
                senderId = senderId,
                senderName = senderName,
                messageText = text.trim(),
                stickerEmoji = sticker
            )
        )
    }

    suspend fun postStickyNote(senderName: String, noteText: String) {
        chatMessageDao.insertMessage(
            ChatMessage(
                senderId = "ADMIN",
                senderName = senderName,
                messageText = noteText.trim(),
                isStickyNote = true
            )
        )
    }

    suspend fun toggleHeartMessage(id: Int) {
        chatMessageDao.toggleHeart(id)
    }

    suspend fun deleteChatMessage(id: Int) {
        chatMessageDao.deleteMessage(id)
    }
}
