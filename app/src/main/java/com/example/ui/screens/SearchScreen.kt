package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkBg
import com.example.ui.viewmodel.CineViewModel

@Composable
fun SearchScreen(
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val allMovies by viewModel.allMovies.collectAsState()

    // Query matched list
    val queryMatches = remember(uiState.searchScope, allMovies) {
        val q = uiState.searchScope.trim()
        if (q.isEmpty()) {
            emptyList()
        } else {
            allMovies.filter {
                it.title.contains(q, ignoreCase = true) ||
                it.genre.contains(q, ignoreCase = true) ||
                it.synopsis.contains(q, ignoreCase = true)
            }
        }
    }

    val suggestionTags = listOf("Chronos", "Cyberpunk", "Anime", "Action", "Rift", "Mecha")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("search_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Live Search Bar Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchScope,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Find films, timelines, genres...", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = AccentRed) },
                    trailingIcon = {
                        if (uiState.searchScope.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.6f))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = AccentRed
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_bar_input"),
                    singleLine = true
                )
            }

            // Trending / SUGGESTIONS Horizontal list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "HOT SUGGESTIONS",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(suggestionTags) { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x11FFFFFF))
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .clickable { viewModel.setSearchQuery(tag) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#$tag",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Results Render State
            if (uiState.searchScope.trim().isEmpty()) {
                // Initial State: Show Category Grids or trending recommendations
                Text(
                    text = "DISCOVER POPULAR",
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
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allMovies, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            onSelect = { viewModel.selectMovie(movie.id) },
                            onToggleWatchlist = { viewModel.toggleWatchlist(movie.id, movie.isWatchlisted) }
                        )
                    }
                }
            } else {
                // Matches found state
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TIMELINE STREAMS FOUND (${queryMatches.size})",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                if (queryMatches.isEmpty()) {
                    EmptyStateView(
                        text = "Vortex scanning returned zero records for \"${uiState.searchScope}\". Please align frequencies.",
                        icon = Icons.Default.SentimentDissatisfied,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(queryMatches, key = { it.id }) { movie ->
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
}
