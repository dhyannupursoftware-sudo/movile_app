package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: String,
    val title: String,
    val backdropUrl: String,
    val posterUrl: String,
    val rating: Double,
    val genre: String,
    val duration: String,
    val releaseYear: Int,
    val synopsis: String,
    val isWatchlisted: Boolean = false,
    val watchProgress: Float = 0f, // 0.0 to 1.0
    val lastWatchedTime: Long = 0L, // timestamp
    val category: String // e.g. "Trending", "Popular", "Anime", "Action"
)
