package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MovieEntity
import com.example.data.repository.MovieRepository
import com.example.data.repository.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class CineScreen { SPLASH, LOGIN, HOME, DETAIL, SEARCH, WATCHLIST, PROFILE, SUBSCRIPTION }

data class CineUiState(
    val currentScreen: CineScreen = CineScreen.SPLASH,
    val selectedMovieId: String? = null,
    val searchScope: String = "",
    val activeGenreFilter: String = "All",
    val activeVideoPlayingId: String? = null, // ID of movie currently playing full-screen
    val videoPlayingProgress: Float = 0f, // percentage
    val videoIsPaused: Boolean = false,
    val showNotificationBadge: Boolean = true,
    val userNotificationMessage: String? = null,
    val authUsernameInput: String = "",
    val authEmailInput: String = "",
    val showNotificationDropdown: Boolean = false
)

class CineViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = MovieRepository(database.movieDao(), application)

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val watchlistMovies: StateFlow<List<MovieEntity>> = repository.watchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val continueWatchingMovies: StateFlow<List<MovieEntity>> = repository.continueWatching
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allMovies: StateFlow<List<MovieEntity>> = repository.allMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CineUiState())
    val uiState: StateFlow<CineUiState> = _uiState.asStateFlow()

    // Backstack list to allow clean back navigation
    private val navigationHistory = mutableListOf<CineScreen>()

    init {
        // Automatically check login state to route
        viewModelScope.launch {
            kotlinx.coroutines.delay(2200) // Beautiful splash dwell time
            if (userProfile.value.isLoggedIn) {
                navigateTo(CineScreen.HOME)
            } else {
                navigateTo(CineScreen.LOGIN)
            }
        }
    }

    fun navigateTo(screen: CineScreen) {
        if (_uiState.value.currentScreen != screen) {
            // Keep track of backstack (only for primary screens, avoid loops)
            val current = _uiState.value.currentScreen
            if (current != CineScreen.SPLASH && current != CineScreen.LOGIN) {
                navigationHistory.add(current)
            }
            _uiState.value = _uiState.value.copy(currentScreen = screen)
        }
    }

    fun navigateBack() {
        if (navigationHistory.isNotEmpty()) {
            val prev = navigationHistory.removeAt(navigationHistory.size - 1)
            _uiState.value = _uiState.value.copy(
                currentScreen = prev,
                selectedMovieId = if (prev == CineScreen.DETAIL) _uiState.value.selectedMovieId else null
            )
        } else {
            // Default back behavior
            _uiState.value = _uiState.value.copy(currentScreen = CineScreen.HOME)
        }
    }

    fun selectMovie(movieId: String) {
        _uiState.value = _uiState.value.copy(selectedMovieId = movieId)
        navigateTo(CineScreen.DETAIL)
    }

    fun toggleWatchlist(movieId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleWatchlist(movieId, currentStatus)
            showToast(if (!currentStatus) "Added to Watchlist!" else "Removed from Watchlist")
        }
    }

    fun submitLogin() {
        val username = _uiState.value.authUsernameInput.trim()
        val email = _uiState.value.authEmailInput.trim()

        if (username.isEmpty() || email.isEmpty()) {
            showToast("Please enter a username and email.")
            return
        }

        repository.login(username, email)
        showToast("Welcome back, $username!")
        navigateTo(CineScreen.HOME)
    }

    fun signout() {
        repository.logout()
        _uiState.value = _uiState.value.copy(
            authUsernameInput = "",
            authEmailInput = ""
        )
        showToast("Logged out successfully")
        navigateTo(CineScreen.LOGIN)
    }

    fun selectSubscription(plan: String) {
        repository.setSubscription(plan)
        showToast("Successfully subscribed to $plan Plan! enjoy 4K Ultra HDR")
        navigateTo(CineScreen.PROFILE)
    }

    fun toggleAppTheme() {
        val next = !userProfile.value.isDarkTheme
        repository.setDarkTheme(next)
        showToast(if (next) "Cinematic Dark Mode applied" else "Steel Light Mode applied")
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchScope = query)
    }

    fun setGenreFilter(genre: String) {
        _uiState.value = _uiState.value.copy(activeGenreFilter = genre)
    }

    fun updateAuthUsername(value: String) {
        _uiState.value = _uiState.value.copy(authUsernameInput = value)
    }

    fun updateAuthEmail(value: String) {
        _uiState.value = _uiState.value.copy(authEmailInput = value)
    }

    fun updateProfileName(username: String) {
        repository.login(username, userProfile.value.email)
        showToast("Neural profile synched!")
    }

    fun toggleNotificationDropdown() {
        _uiState.value = _uiState.value.copy(
            showNotificationDropdown = !_uiState.value.showNotificationDropdown,
            showNotificationBadge = false
        )
    }

    fun dismissNotificationDropdown() {
        _uiState.value = _uiState.value.copy(showNotificationDropdown = false)
    }

    // Video Player simulations
    fun launchVideoPlayer(movieId: String) {
        _uiState.value = _uiState.value.copy(
            activeVideoPlayingId = movieId,
            videoPlayingProgress = 0f,
            videoIsPaused = false
        )
        showToast("Streaming starting in 4K HDR...")
    }

    fun setVideoProgress(progress: Float) {
        _uiState.value = _uiState.value.copy(videoPlayingProgress = progress.coerceIn(0f, 1f))
    }

    fun toggleVideoPause() {
        _uiState.value = _uiState.value.copy(videoIsPaused = !_uiState.value.videoIsPaused)
    }

    fun closeVideoPlayer() {
        val playingId = _uiState.value.activeVideoPlayingId
        val progress = _uiState.value.videoPlayingProgress
        if (playingId != null && progress > 0f) {
            viewModelScope.launch {
                repository.saveWatchProgress(playingId, progress)
            }
        }
        _uiState.value = _uiState.value.copy(activeVideoPlayingId = null)
        showToast("Stopped Stream")
    }

    fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(userNotificationMessage = message)
    }

    fun dismissToast() {
        _uiState.value = _uiState.value.copy(userNotificationMessage = null)
    }
}
