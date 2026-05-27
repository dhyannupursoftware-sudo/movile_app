package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    suspend fun getMovieById(id: String): MovieEntity?

    @Query("SELECT * FROM movies WHERE isWatchlisted = 1")
    fun getWatchlist(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE watchProgress > 0 ORDER BY lastWatchedTime DESC")
    fun getContinueWatching(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("UPDATE movies SET isWatchlisted = :isWatchlisted WHERE id = :id")
    suspend fun updateWatchlistStatus(id: String, isWatchlisted: Boolean)

    @Query("UPDATE movies SET watchProgress = :progress, lastWatchedTime = :timestamp WHERE id = :id")
    suspend fun updateWatchProgress(id: String, progress: Float, timestamp: Long)
}
