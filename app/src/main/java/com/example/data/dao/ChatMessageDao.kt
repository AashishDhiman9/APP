package com.example.data.dao

import androidx.room.*
import com.example.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE isStickyNote = 1 ORDER BY timestamp DESC LIMIT 1")
    fun getLatestStickyNote(): Flow<ChatMessage?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("UPDATE chat_messages SET isHearted = CASE WHEN isHearted = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleHeart(id: Int)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessage(id: Int)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()
}
