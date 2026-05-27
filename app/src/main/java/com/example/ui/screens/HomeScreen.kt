package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.MovieEntity
import com.example.ui.components.CineLogo
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CardBg
import com.example.ui.theme.DarkBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonBlueGlow
import com.example.ui.theme.NeonRedGlow
import com.example.ui.viewmodel.CineScreen
import com.example.ui.viewmodel.CineViewModel

@Composable
fun HomeScreen(
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val allMovies by viewModel.allMovies.collectAsState()
    val watchlist by viewModel.watchlistMovies.collectAsState()
    val continueWatching by viewModel.continueWatchingMovies.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val scrollState = rememberScrollState()

    // Filtered lists depending on activeGenreFilter
    val filteredMovies = if (uiState.activeGenreFilter == "All") {
        allMovies
    } else {
        allMovies.filter { it.genre.contains(uiState.activeGenreFilter, ignoreCase = true) }
    }

    val categories = listOf("All", "Sci-Fi", "Anime", "Cyberpunk", "Action")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (profile.isDarkTheme) DarkBg else MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        // Sticky content layout
        Column(modifier = Modifier.fillMaxSize()) {
            // Sticky Navbar
            Navbar(viewModel = viewModel)

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // Large Hero Section (Choose mv1 "Chronos Rift" as massive default hero)
                val heroMovie = allMovies.find { it.id == "mv1" }
                if (heroMovie != null) {
                    HeroSection(movie = heroMovie, viewModel = viewModel)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Genres filter tabs
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(categories) { category ->
                        val isSelected = uiState.activeGenreFilter == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) AccentRed else Color(0x14FFFFFF)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AccentRed else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.setGenreFilter(category) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category.uppercase(),
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Conditionally display categories or search filter
                if (uiState.activeGenreFilter != "All") {
                    // Show Filter Results Grid
                    Text(
                        text = "GENRE: ${uiState.activeGenreFilter.uppercase()}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    )

                    if (filteredMovies.isEmpty()) {
                        EmptyStateView(
                            text = "No movies match the security filter ${uiState.activeGenreFilter}. Verify timelines.",
                            icon = Icons.Default.FilterList
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            filteredMovies.chunked(2).forEach { rowMovies ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    rowMovies.forEach { movie ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            MovieCard(
                                                movie = movie,
                                                onSelect = { viewModel.selectMovie(movie.id) },
                                                onToggleWatchlist = { viewModel.toggleWatchlist(movie.id, movie.isWatchlisted) }
                                            )
                                        }
                                    }
                                    if (rowMovies.size < 2) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Standard Home Categories List

                    // 1. CONTINUE WATCHING (Filter Progress > 0)
                    if (continueWatching.isNotEmpty()) {
                        MovieCategoryRow(
                            title = "Continue Watching",
                            movies = continueWatching,
                            viewModel = viewModel,
                            showProgress = true
                        )
                    }

                    // 2. TRENDING MOVIES
                    val trending = allMovies.filter { it.category == "Trending" }
                    if (trending.isNotEmpty()) {
                        MovieCategoryRow(
                            title = "Trending Movies",
                            movies = trending,
                            viewModel = viewModel
                        )
                    }

                    // 3. POPULAR SHOWS
                    val popular = allMovies.filter { it.category == "Popular Shows" }
                    if (popular.isNotEmpty()) {
                        MovieCategoryRow(
                            title = "Popular Shows",
                            movies = popular,
                            viewModel = viewModel
                        )
                    }

                    // 4. RECOMMENDED FOR YOU
                    val recommended = allMovies.shuffled().take(4)
                    if (recommended.isNotEmpty()) {
                        MovieCategoryRow(
                            title = "Recommended For You",
                            movies = recommended,
                            viewModel = viewModel
                        )
                    }

                    // 5. TOP RATED
                    val topRated = allMovies.filter { it.category == "Top Rated" || it.rating >= 8.8 }
                    if (topRated.isNotEmpty()) {
                        MovieCategoryRow(
                            title = "Top Rated",
                            movies = topRated,
                            viewModel = viewModel
                        )
                    }

                    // 6. ACTION MOVIES
                    val action = allMovies.filter { it.category == "Action Movies" || it.genre.contains("Action") }
                    if (action.isNotEmpty()) {
                        MovieCategoryRow(
                            title = "Action Movies",
                            movies = action,
                            viewModel = viewModel
                        )
                    }

                    // 7. ANIME COLLECTION
                    val anime = allMovies.filter { it.category == "Anime Collection" || it.genre.contains("Anime") }
                    if (anime.isNotEmpty()) {
                        MovieCategoryRow(
                            title = "Anime Collection",
                            movies = anime,
                            viewModel = viewModel
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp)) // padding for bottom menu
            }
        }

        // Notification Dropdown Overlay
        if (uiState.showNotificationDropdown) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { viewModel.dismissNotificationDropdown() }
            )
            NotificationDropdownMenu(
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 70.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun Navbar(
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xCD0B0F1A)) // Glass translucent top
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)),
                RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo
        CineLogo(
            logoSize = 34.dp,
            animate = true,
            showText = true,
            modifier = Modifier.clickable { viewModel.navigateTo(CineScreen.HOME) }
        )

        // Utility control icons row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search
            IconButton(
                onClick = { viewModel.navigateTo(CineScreen.SEARCH) },
                modifier = Modifier.testTag("nav_search_button")
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            }

            // Notification Bell with badge
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = { viewModel.toggleNotificationDropdown() },
                    modifier = Modifier.testTag("nav_notification_button")
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = if (uiState.showNotificationDropdown) AccentRed else Color.White
                    )
                }

                if (uiState.showNotificationBadge) {
                    Box(
                        modifier = Modifier
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AccentRed)
                            .border(1.5.dp, DarkBg, CircleShape)
                    )
                }
            }

            // Profile tier Dynamic Badge Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(AccentRed, AccentBlue)))
                    .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    .clickable { viewModel.navigateTo(CineScreen.PROFILE) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.username.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HeroSection(
    movie: MovieEntity,
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .testTag("hero_section")
    ) {
        // High-end backdrop image with subtle top and bottom overlay fading gradients
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(movie.backdropUrl)
                .crossfade(true)
                .build(),
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Backdrop glowing visual overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkBg.copy(alpha = 0.6f),
                            Color.Transparent,
                            DarkBg
                        )
                    )
                )
        )

        // Left blue highlight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            DarkBg.copy(alpha = 0.85f),
                            Color.Transparent
                        ),
                        endX = 400f
                    )
                )
        )

        // Movie Detail Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            // High-end atmospheric badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AccentRed)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "TRENDING",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "${movie.genre}  •  ${movie.duration}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val titleWords = movie.title.split(" ")
            if (titleWords.size >= 2) {
                val firstPart = titleWords.dropLast(1).joinToString(" ")
                val lastWord = titleWords.last()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = firstPart,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = lastWord,
                        color = AccentBlue,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            } else {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    fontFamily = FontFamily.SansSerif
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "★ ${movie.rating} rating  •  In interstellar high fidelity",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = movie.synopsis,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
                modifier = Modifier.widthIn(max = 280.dp),
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hero action buttons - Capsule-shaped and high fidelity overlay
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
            ) {
                // Play Button (White container with DarkBg text, rounded-2xl / 16.dp)
                Button(
                    onClick = { viewModel.launchVideoPlayer(movie.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = DarkBg
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = DarkBg)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Watch Now",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                // Info Details button (Glass/Translucent background, rounded-2xl / 16.dp)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectMovie(movie.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Details",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Watchlist toggler (Glass/Translucent, rounded-2xl / 16.dp)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { viewModel.toggleWatchlist(movie.id, movie.isWatchlisted) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (movie.isWatchlisted) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Watchlist",
                        tint = if (movie.isWatchlisted) AccentRed else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MovieCategoryRow(
    title: String,
    movies: List<MovieEntity>,
    viewModel: CineViewModel,
    showProgress: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Row Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "SEE ALL",
                color = AccentRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.clickable { viewModel.navigateTo(CineScreen.SEARCH) }
            )
        }

        // Horizontal scrolling lists
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(movies, key = { it.id }) { movie ->
                MovieCard(
                    movie = movie,
                    showProgress = showProgress,
                    onSelect = { viewModel.selectMovie(movie.id) },
                    onToggleWatchlist = { viewModel.toggleWatchlist(movie.id, movie.isWatchlisted) }
                )
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: MovieEntity,
    showProgress: Boolean = false,
    onSelect: () -> Unit,
    onToggleWatchlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .width(140.dp)
            .clickable { onSelect() }
            .testTag("movie_card_${movie.id}")
    ) {
        Box(
            modifier = Modifier
                .height(190.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                    ),
                    RoundedCornerShape(12.dp)
                )
        ) {
            // Poster picture
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(movie.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dynamic Gradient Overlay for reading star tag
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Rating Star badge inside Poster
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFD700), modifier = Modifier.size(10.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = movie.rating.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quick Play icon badge center
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    .align(Alignment.Center)
                    .clickable { onSelect() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(18.dp))
            }

            // Watchlist item overlay bottom-right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { onToggleWatchlist() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (movie.isWatchlisted) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = if (movie.isWatchlisted) AccentRed else Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }

            // Continue progress bar wrapper if needed
            if (showProgress && movie.watchProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                        .align(Alignment.BottomCenter)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(movie.watchProgress)
                            .background(AccentRed)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title
        Text(
            text = movie.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Subtitle Genre
        Text(
            text = movie.genre.split(" • ").firstOrNull() ?: movie.genre,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun NotificationDropdownMenu(
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .testTag("notification_dropdown_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Announcements",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "DISMISS",
                    color = AccentRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.clickable { viewModel.dismissNotificationDropdown() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notification item 1
            NotificationItem(
                title = "🚨 TIMELINE SPLINTER",
                desc = "Chronos Rift Episode 3 is now streaming in 4k HDR!",
                time = "10m ago"
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))

            // Notification item 2
            NotificationItem(
                title = "🎁 PREMIUM BENEFITS",
                desc = "Family subscription accounts received a free neural holographic skin avatar limit.",
                time = "2h ago"
            )
        }
    }
}

@Composable
fun NotificationItem(
    title: String,
    desc: String,
    time: String
) {
    Column {
        Text(
            text = title,
            color = AccentRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = desc,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            lineHeight = 14.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = time,
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 9.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun EmptyStateView(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.2f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
