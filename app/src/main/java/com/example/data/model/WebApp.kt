package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "web_apps")
data class WebApp(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val url: String = "",
    val description: String = "",
    val category: String = "Memories 💌", // e.g. "Memories 💌", "Games 🎮", "Surprise ✨", "Notes 📝", "Music 🎵"
    val emoji: String = "🌸",
    val dateAdded: Long = System.currentTimeMillis(),
    val accessCount: Int = 0,
    val lastAccessedTime: Long? = null,
    val isFavorite: Boolean = false
)
