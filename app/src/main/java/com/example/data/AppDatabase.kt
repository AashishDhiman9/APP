package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ChatMessageDao
import com.example.data.dao.ViewingNotificationDao
import com.example.data.dao.WebAppDao
import com.example.data.model.ChatMessage
import com.example.data.model.ViewingNotification
import com.example.data.model.WebApp

@Database(
    entities = [WebApp::class, ViewingNotification::class, ChatMessage::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun webAppDao(): WebAppDao
    abstract fun viewingNotificationDao(): ViewingNotificationDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mylillu_db"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
