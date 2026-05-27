package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.viewmodel.CineViewModel

@Composable
fun WatchlistScreen(
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    val watchlist by viewModel.watchlistMovies.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("watchlist_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Elegant Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MY THEATER LIST",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            if (watchlist.isEmpty()) {
                EmptyStateView(
                    text = "Dashboard coordinate empty. Mark quantum timelines with the bookmark icon to populate your private CineVortex theater.",
                    icon = Icons.Default.BookmarkBorder,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(
                    text = "SAVED METADATA RELEASES (${watchlist.size})",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(watchlist, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            onSelect = { viewModel.selectMovie(movie.id) },
                            onToggleWatchlist = { viewModel.toggleWatchlist(movie.id, movie.isWatchlisted) }
                        )
                    }
                }
            }
        }
    }
}
