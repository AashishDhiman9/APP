package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "viewing_notifications")
data class ViewingNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val webAppId: Int = 0,
    val webAppTitle: String = "",
    val accessedBy: String = "Lillu",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
