package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.MovieDao
import com.example.data.local.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val username: String,
    val email: String,
    val subscriptionPlan: String, // "Basic", "Premium", "Family", "None"
    val isLoggedIn: Boolean,
    val isDarkTheme: Boolean = true
)

class MovieRepository(
    private val movieDao: MovieDao,
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cinevortex_prefs", Context.MODE_PRIVATE)

    val allMovies: Flow<List<MovieEntity>> = movieDao.getAllMovies()
    val watchlist: Flow<List<MovieEntity>> = movieDao.getWatchlist()
    val continueWatching: Flow<List<MovieEntity>> = movieDao.getContinueWatching()

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private fun loadProfile(): UserProfile {
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val username = prefs.getString("username", "Guest User") ?: "Guest User"
        val email = prefs.getString("email", "guest@cinevortex.com") ?: "guest@cinevortex.com"
        val plan = prefs.getString("subscription_plan", "None") ?: "None"
        val isDark = prefs.getBoolean("is_dark_theme", true)
        return UserProfile(username, email, plan, isLoggedIn, isDark)
    }

    suspend fun toggleWatchlist(movieId: String, currentStatus: Boolean) {
        movieDao.updateWatchlistStatus(movieId, !currentStatus)
    }

    suspend fun saveWatchProgress(movieId: String, progress: Float) {
        movieDao.updateWatchProgress(movieId, progress, System.currentTimeMillis())
    }

    suspend fun getMovieById(movieId: String): MovieEntity? {
        return movieDao.getMovieById(movieId)
    }

    fun login(username: String, email: String) {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("username", username)
            .putString("email", email)
            .putString("subscription_plan", "Premium") // Defaults to premium to enjoy cinematic luxury!
            .apply()
        _userProfile.value = loadProfile()
    }

    fun logout() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .putString("username", "Guest User")
            .putString("email", "guest@cinevortex.com")
            .putString("subscription_plan", "None")
            .apply()
        _userProfile.value = loadProfile()
    }

    fun setSubscription(plan: String) {
        prefs.edit().putString("subscription_plan", plan).apply()
        _userProfile.value = loadProfile()
    }

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean("is_dark_theme", isDark).apply()
        _userProfile.value = loadProfile()
    }
}
