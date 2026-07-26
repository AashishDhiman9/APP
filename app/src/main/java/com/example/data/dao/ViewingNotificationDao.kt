package com.example.data.dao

import androidx.room.*
import com.example.data.model.ViewingNotification
import kotlinx.coroutines.flow.Flow

@Dao
interface ViewingNotificationDao {
    @Query("SELECT * FROM viewing_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<ViewingNotification>>

    @Query("SELECT COUNT(*) FROM viewing_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: ViewingNotification): Long

    @Query("UPDATE viewing_notifications SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead()

    @Query("DELETE FROM viewing_notifications")
    suspend fun clearAllNotifications()
}
