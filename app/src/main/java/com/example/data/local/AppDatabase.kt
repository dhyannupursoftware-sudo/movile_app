package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cinevortex_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.movieDao())
                }
            }
        }

        suspend fun populateDatabase(movieDao: MovieDao) {
            // Pre-populate with premium futuristic movies that align with "CineVortex" (Enter the Future of Entertainment)
            val mockMovies = listOf(
                MovieEntity(
                    id = "mv1",
                    title = "Chronos Rift",
                    backdropUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=800&auto=format&fit=crop&q=80",
                    posterUrl = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=500&auto=format&fit=crop&q=80",
                    rating = 8.9,
                    genre = "Sci-Fi • Thriller • Action",
                    duration = "2h 15m",
                    releaseYear = 2026,
                    synopsis = "In the year 2099, a team of temporal physicists accidentally splinters the space-time continuum, opening an uncontrollable vortex over Neo-Tokyo. As history begins to bleed into the present, a rogue pilot is sent on a high-stakes mission into the timeline's fractured core.",
                    category = "Trending",
                    isWatchlisted = false,
                    watchProgress = 0.65f, // Pre-populated continue watching
                    lastWatchedTime = System.currentTimeMillis() - 600000
                ),
                MovieEntity(
                    id = "mv2",
                    title = "Aether Drift",
                    backdropUrl = "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?w=800&auto=format&fit=crop&q=80",
                    posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&auto=format&fit=crop&q=80",
                    rating = 8.4,
                    genre = "Cyberpunk • Mystery",
                    duration = "2h 05m",
                    releaseYear = 2025,
                    synopsis = "Beneath the neon glow of the Cloud Spires of Sector 9, an illegal memory-broker discovers a sentient neural network that claims to hold the final coordinates to Earth's lost colony ship.",
                    category = "Trending",
                    isWatchlisted = true, // Pre-populated in watchlist
                    watchProgress = 0f,
                    lastWatchedTime = 0L
                ),
                MovieEntity(
                    id = "mv3",
                    title = "Neon Genesis DX",
                    backdropUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?w=800&auto=format&fit=crop&q=80",
                    posterUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500&auto=format&fit=crop&q=80",
                    rating = 9.2,
                    genre = "Anime • Mecha • Cyberpunk",
                    duration = "1h 50m",
                    releaseYear = 2026,
                    synopsis = "As mechanical leviathans descend from deep atmospheric dust, Humanity's final resort is a squadron of bio-organic mecha piloted by cybernetically-enhanced youth.",
                    category = "Anime Collection",
                    isWatchlisted = false,
                    watchProgress = 0.40f, // Pre-populated continue watching
                    lastWatchedTime = System.currentTimeMillis() - 1200000
                ),
                MovieEntity(
                    id = "mv4",
                    title = "Starlight Synthetica",
                    backdropUrl = "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=800&auto=format&fit=crop&q=80",
                    posterUrl = "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?w=500&auto=format&fit=crop&q=80",
                    rating = 8.6,
                    genre = "Sci-Fi • Space Opera",
                    duration = "2h 30m",
                    releaseYear = 2026,
                    synopsis = "A galactic diplomat, equipped with synthetic emotions, is tasked with mediating a trade dispute between asteroid-mining syndicates and a highly advanced AI planetary consciousness.",
                    category = "Popular Shows",
                    isWatchlisted = false,
                    watchProgress = 0f,
                    lastWatchedTime = 0L
                ),
                MovieEntity(
                    id = "mv5",
                    title = "Vortex Horizon",
                    backdropUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&auto=format&fit=crop&q=80",
                    posterUrl = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=500&auto=format&fit=crop&q=80",
                    rating = 9.5,
                    genre = "Sci-Fi • Space Thriller",
                    duration = "2h 45m",
                    releaseYear = 2026,
                    synopsis = "At the edge of a spinning supermassive black hole, an investigative team intercepts an ancient signal that mimics the voices of their dead loved ones.",
                    category = "Top Rated",
                    isWatchlisted = true,
                    watchProgress = 0f,
                    lastWatchedTime = 0L
                ),
                MovieEntity(
                    id = "mv6",
                    title = "Cyberpunk Eclipse",
                    backdropUrl = "https://images.unsplash.com/photo-1515260268569-9271009adfdb?w=800&auto=format&fit=crop&q=80",
                    posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=500&auto=format&fit=crop&q=80",
                    rating = 8.0,
                    genre = "Action • Cyberpunk • Noir",
                    duration = "2h 10m",
                    releaseYear = 2024,
                    synopsis = "When a cybernetic hitman refuses a contract targeting a rebellious digital archivist, the megacorporation unleashes a wave of military-grade cyborgs to clean up the streets.",
                    category = "Action Movies",
                    isWatchlisted = false,
                    watchProgress = 0f,
                    lastWatchedTime = 0L
                ),
                MovieEntity(
                    id = "mv7",
                    title = "Acolytes of Iron",
                    backdropUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
                    posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500&auto=format&fit=crop&q=80",
                    rating = 8.2,
                    genre = "Action • Mecha",
                    duration = "1h 55m",
                    releaseYear = 2025,
                    synopsis = "In the rusted ruins of a forgotten star-forge, an underground order of mech-gladiators battles the autonomous defense grid to liberate their subterranean city.",
                    category = "Action Movies",
                    isWatchlisted = false,
                    watchProgress = 0f,
                    lastWatchedTime = 0L
                ),
                MovieEntity(
                    id = "mv8",
                    title = "Synth Samurai",
                    backdropUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&auto=format&fit=crop&q=80",
                    posterUrl = "https://images.unsplash.com/photo-1578301978693-85fa9c0320b9?w=500&auto=format&fit=crop&q=80",
                    rating = 9.0,
                    genre = "Anime • Hack & Slash",
                    duration = "1h 45m",
                    releaseYear = 2026,
                    synopsis = "A swordfighter wielding a blade that vibrates at absolute zero wanders a lawless silicon wasteland, delivering vengeance to corrupt corporate executives.",
                    category = "Anime Collection",
                    isWatchlisted = false,
                    watchProgress = 0f,
                    lastWatchedTime = 0L
                )
            )
            movieDao.insertMovies(mockMovies)
        }
    }
}
