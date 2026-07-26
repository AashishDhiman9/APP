package com.example.data.dao

import androidx.room.*
import com.example.data.model.WebApp
import kotlinx.coroutines.flow.Flow

@Dao
interface WebAppDao {
    @Query("SELECT * FROM web_apps ORDER BY dateAdded DESC")
    fun getAllWebApps(): Flow<List<WebApp>>

    @Query("SELECT * FROM web_apps WHERE id = :id")
    suspend fun getWebAppById(id: Int): WebApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebApp(webApp: WebApp): Long

    @Update
    suspend fun updateWebApp(webApp: WebApp)

    @Query("DELETE FROM web_apps WHERE id = :id")
    suspend fun deleteWebAppById(id: Int)

    @Query("UPDATE web_apps SET accessCount = accessCount + 1, lastAccessedTime = :accessTime WHERE id = :id")
    suspend fun recordAccess(id: Int, accessTime: Long = System.currentTimeMillis())
}
