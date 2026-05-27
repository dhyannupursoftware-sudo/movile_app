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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.MovieEntity
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CardBg
import com.example.ui.theme.DarkBg
import com.example.ui.theme.GlassBorder
import com.example.ui.viewmodel.CineViewModel

data class CastMember(
    val name: String,
    val character: String,
    val imageUrl: String
)

@Composable
fun DetailsScreen(
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val allMovies by viewModel.allMovies.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val movie = allMovies.find { it.id == uiState.selectedMovieId }

    if (movie == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg),
            contentAlignment = Alignment.Center
        ) {
            Text("No timeline found for this movie coordinate.", color = Color.White)
        }
        return
    }

    // Similar movies: movies in the same category or genre that aren't this movie
    val similarMovies = allMovies.filter {
        it.id != movie.id && (it.category == movie.category || it.genre.split(" • ").firstOrNull() == movie.genre.split(" • ").firstOrNull())
    }

    // Futuristic Cast Info
    val mockCast = remember(movie.id) {
        listOf(
            CastMember(
                "Keanu Mercer",
                "Commander Jax",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80"
            ),
            CastMember(
                "Aria Greenfield",
                "Dr. Celeste",
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80"
            ),
            CastMember(
                "Hologram Voss",
                "Rogue Synthetica",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80"
            ),
            CastMember(
                "Luna Sterling",
                "Serenity-4 AI",
                "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=200&auto=format&fit=crop&q=80"
            )
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("details_screen"),
        containerColor = DarkBg,
        topBar = {
            // Elegant back button bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x33000000))
                        .testTag("details_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                IconButton(
                    onClick = { viewModel.toggleWatchlist(movie.id, movie.isWatchlisted) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x33000000))
                ) {
                    Icon(
                        imageVector = if (movie.isWatchlisted) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Watchlist",
                        tint = if (movie.isWatchlisted) AccentRed else Color.White
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Expanded Backdrop Banner Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(movie.backdropUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Neon Glow gradient shading
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    DarkBg.copy(alpha = 0.5f),
                                    DarkBg
                                )
                            )
                        )
                )

                // Large Overlaid Play Button
                IconButton(
                    onClick = { viewModel.launchVideoPlayer(movie.id) },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AccentRed)
                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        .testTag("details_play_button")
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Stream Trailer",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Movie Details & Info Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Genres / Categories Metadata Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    movie.genre.split(" • ").forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x19FFFFFF))
                                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tag.uppercase(),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title (split highlighting to match theme guidelines)
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
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = lastWord,
                            color = AccentBlue,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                } else {
                    Text(
                        text = movie.title,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ratings & Metadata
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${movie.rating} Rating",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Year: ${movie.releaseYear}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = movie.duration,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Watch / Download Control Button Rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.launchVideoPlayer(movie.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("STREAM NOW", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
                    }

                    // Download Simulator
                    var isDownloaded by remember { mutableStateOf(false) }
                    var isDownloading by remember { mutableStateOf(false) }
                    var downloadProgress by remember { mutableStateOf(0f) }

                    LaunchedEffect(isDownloading) {
                        if (isDownloading) {
                            for (i in 1..10) {
                                kotlinx.coroutines.delay(200)
                                downloadProgress = i / 10f
                            }
                            isDownloading = false
                            isDownloaded = true
                            viewModel.showToast("${movie.title} offline coordinates synched!")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            if (!isDownloaded && !isDownloading) {
                                isDownloading = true
                            } else if (isDownloaded) {
                                isDownloaded = false
                                downloadProgress = 0f
                                viewModel.showToast("Removed offline coordinates link")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        border = BorderStroke(1.dp, if (isDownloaded) AccentBlue else Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier.size(18.dp),
                                color = AccentBlue,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SYNCING ${(downloadProgress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = if (isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                                contentDescription = null,
                                tint = if (isDownloaded) AccentBlue else Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isDownloaded) "SYNC COMPLETED" else "SYNC OFFLINE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Synopsis Paragraph
                Text(
                    text = "SYNOPSIS",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = movie.synopsis,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Episodes Section UI (Simulated seasons list)
                Text(
                    text = "EPISODES",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                EpisodeItem(title = "E1 • Chrono Trigger Coordinates", desc = "Theoretical physics collides with quantum reality when a subatomic explosion splits a dimensional rift over neo-civilization.", duration = "44 mins") { viewModel.launchVideoPlayer(movie.id) }
                EpisodeItem(title = "E2 • The Singularity Matrix", desc = "A team of researchers explores the anomalous boundaries of the glowing visual vortex, finding an ancient signal repeating names of local scientists.", duration = "52 mins") { viewModel.launchVideoPlayer(movie.id) }

                Spacer(modifier = Modifier.height(24.dp))

                // Cast Circle list
                Text(
                    text = "FEATURED CAST",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(mockCast) { actor ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(90.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(actor.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = actor.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = actor.name,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Text(
                                text = actor.character,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 9.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Similar Movies
                if (similarMovies.isNotEmpty()) {
                    Text(
                        text = "YOU MAY ALSO ENJOY",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(similarMovies) { sim ->
                            Box(modifier = Modifier.width(120.dp)) {
                                MovieCard(
                                    movie = sim,
                                    onSelect = { viewModel.selectMovie(sim.id) },
                                    onToggleWatchlist = { viewModel.toggleWatchlist(sim.id, sim.isWatchlisted) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun EpisodeItem(
    title: String,
    desc: String,
    duration: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x0AFFFFFF))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AccentRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = duration,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = desc,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
