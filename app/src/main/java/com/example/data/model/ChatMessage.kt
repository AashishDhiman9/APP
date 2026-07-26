package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val senderId: String = "", // "ADMIN" or "LILLU"
    val senderName: String = "",
    val messageText: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isHearted: Boolean = false,
    val stickerEmoji: String? = null,
    val isStickyNote: Boolean = false
)
