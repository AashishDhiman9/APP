package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.model.ViewingNotification
import com.example.data.model.WebApp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    currentUser: UserRole,
    webApps: List<WebApp>,
    selectedCategory: String,
    searchQuery: String,
    latestAlert: ViewingNotification?,
    showAddDialog: Boolean,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onOpenWebApp: (WebApp) -> Unit,
    onDeleteWebApp: (Int) -> Unit,
    onShowAddDialogChanged: (Boolean) -> Unit,
    onAddNewWebApp: (title: String, url: String, description: String, category: String, emoji: String) -> Unit,
    onSwitchUserClick: () -> Unit,
    onDismissAlert: () -> Unit
) {
    val categories = listOf("All", "Memories 💌", "Games 🎮", "Surprise ✨", "Notes 📝", "Music 🎵")
    var appToDelete by remember { mutableStateOf<WebApp?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onShowAddDialogChanged(true) },
                containerColor = PastelPinkPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.testTag("add_webapp_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add WebApp")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Add Web App", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = PastelPinkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Real-Time Access Notification Alert Banner
            AnimatedVisibility(
                visible = latestAlert != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                if (latestAlert != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(6.dp, RoundedCornerShape(20.dp)),
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelRoseAccent.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Live Alert",
                                tint = PastelRoseAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "✨ Real-Time Access Alert!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SoftTextDark
                                )
                                Text(
                                    text = "${latestAlert.accessedBy} opened '${latestAlert.webAppTitle}'",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SoftTextDark
                                )
                            }
                            IconButton(onClick = onDismissAlert) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = SoftTextDark)
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    // High-Contrast Header Banner Card
                    HeaderBannerCard(
                        currentUser = currentUser,
                        webAppsCount = webApps.size,
                        onSwitchUserClick = onSwitchUserClick
                    )
                }

                item {
                    // High Contrast Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = {
                            Text(
                                "Search web apps or keywords...",
                                fontSize = 14.sp,
                                color = SoftTextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = PastelPinkPrimary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChanged("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = SoftTextDark)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(24.dp))
                            .testTag("dashboard_search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SoftTextDark,
                            unfocusedTextColor = SoftTextDark,
                            focusedBorderColor = PastelPinkPrimary,
                            unfocusedBorderColor = PastelCardOutline,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                item {
                    // Category Filter Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategorySelected(category) },
                                label = {
                                    Text(
                                        text = category,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else SoftTextDark
                                    )
                                },
                                shape = RoundedCornerShape(18.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PastelPinkPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = SoftTextDark
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = PastelCardOutline,
                                    selectedBorderColor = PastelPinkPrimary,
                                    borderWidth = 1.5.dp
                                )
                            )
                        }
                    }
                }

                if (webApps.isEmpty()) {
                    item {
                        EmptyStateCard(
                            currentUser = currentUser,
                            onAddClick = { onShowAddDialogChanged(true) }
                        )
                    }
                } else {
                    items(webApps, key = { it.id }) { webApp ->
                        WebAppCardItem(
                            webApp = webApp,
                            currentUser = currentUser,
                            onOpenClick = { onOpenWebApp(webApp) },
                            onDeleteClick = { appToDelete = webApp }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        AddWebAppDialog(
            onDismiss = { onShowAddDialogChanged(false) },
            onConfirm = onAddNewWebApp
        )
    }

    // Delete Confirmation Dialog for any existing web app
    if (appToDelete != null) {
        AlertDialog(
            onDismissRequest = { appToDelete = null },
            title = {
                Text(
                    text = "Delete Web App?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SoftTextDark
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${appToDelete?.title}' (${appToDelete?.emoji})? This action cannot be undone.",
                    fontSize = 14.sp,
                    color = SoftTextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        appToDelete?.let { onDeleteWebApp(it.id) }
                        appToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PastelRoseAccent),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Delete App", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { appToDelete = null },
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PastelCardOutline)
                ) {
                    Text("Cancel", color = SoftTextDark)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun HeaderBannerCard(
    currentUser: UserRole,
    webAppsCount: Int,
    onSwitchUserClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFD81B60), // High contrast deep vibrant pink
                            Color(0xFF8E24AA)  // Rich deep violet
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser.avatarEmoji,
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (currentUser == UserRole.LILLU) "Hi, Lillu! ✨" else "Hi, Admin! 👑",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (currentUser == UserRole.LILLU)
                            "$webAppsCount special web app(s) ready in your hub 🌸"
                        else
                            "$webAppsCount active web app link(s) for Lillu 💌",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                }

                IconButton(
                    onClick = onSwitchUserClick,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.25f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = "Switch Account",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun WebAppCardItem(
    webApp: WebApp,
    currentUser: UserRole,
    onOpenClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .testTag("webapp_item_${webApp.id}"),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelCardOutline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Emoji Avatar Container
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PastelPinkBackground)
                        .border(1.5.dp, PastelCardOutline, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = webApp.emoji, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = webApp.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // Category Tag
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PastelPinkBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, PastelCardOutline),
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text(
                                text = webApp.category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PastelPinkPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = webApp.description,
                        fontSize = 13.sp,
                        color = SoftTextMuted,
                        fontWeight = FontWeight.Normal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = if (webApp.accessCount > 0)
                            "Opened ${webApp.accessCount} time(s)"
                        else
                            "New • Not opened yet",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PastelRoseAccent
                    )
                    if (webApp.lastAccessedTime != null) {
                        val relativeTime = DateUtils.getRelativeTimeSpanString(
                            webApp.lastAccessedTime,
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        )
                        Text(
                            text = "Visited $relativeTime",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SoftTextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Delete Option available for all users on any existing app
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .background(PastelPinkBackground, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Web App",
                            tint = PastelRoseAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Button(
                        onClick = onOpenClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PastelPinkPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("open_webapp_button_${webApp.id}")
                    ) {
                        Text("Open App", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Launch,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    currentUser: UserRole,
    onAddClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelCardOutline)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🌸", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No web apps found",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SoftTextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap the button below to add a custom web app link!",
                fontSize = 13.sp,
                color = SoftTextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = PastelPinkPrimary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Add First Web App", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWebAppDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, url: String, description: String, category: String, emoji: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Memories 💌") }
    var selectedEmoji by remember { mutableStateOf("💖") }

    val emojis = listOf("💖", "🌸", "🎮", "🌟", "🎵", "💌", "🐣", "🍓", "🎀", "🧸")
    val categories = listOf("Memories 💌", "Games 🎮", "Surprise ✨", "Notes 📝", "Music 🎵")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✨ Add Custom Web App", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SoftTextDark)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Web App Title", color = SoftTextMuted) },
                    placeholder = { Text("e.g. Lillu's Memory Lane", color = SoftTextMuted) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftTextDark,
                        unfocusedTextColor = SoftTextDark,
                        focusedBorderColor = PastelPinkPrimary,
                        unfocusedBorderColor = PastelCardOutline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_dialog_title")
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Website URL", color = SoftTextMuted) },
                    placeholder = { Text("e.g. https://mywebapp.vercel.app", color = SoftTextMuted) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftTextDark,
                        unfocusedTextColor = SoftTextDark,
                        focusedBorderColor = PastelPinkPrimary,
                        unfocusedBorderColor = PastelCardOutline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_dialog_url")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = SoftTextMuted) },
                    placeholder = { Text("A short note about this web app...", color = SoftTextMuted) },
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftTextDark,
                        unfocusedTextColor = SoftTextDark,
                        focusedBorderColor = PastelPinkPrimary,
                        unfocusedBorderColor = PastelCardOutline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Pick Category",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftTextDark
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = {
                                Text(
                                    cat,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (category == cat) Color.White else SoftTextDark
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PastelPinkPrimary,
                                containerColor = PastelPinkBackground
                            )
                        )
                    }
                }

                Text(
                    text = "Pick Icon Emoji",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftTextDark
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(emojis) { em ->
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedEmoji == em) PastelPinkPrimary.copy(alpha = 0.2f)
                                    else PastelPinkBackground
                                )
                                .border(
                                    width = if (selectedEmoji == em) 2.dp else 0.dp,
                                    color = PastelPinkPrimary,
                                    shape = CircleShape
                                )
                                .clickable { selectedEmoji = em },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = em, fontSize = 20.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && url.isNotBlank()) {
                        onConfirm(title, url, description, category, selectedEmoji)
                    }
                },
                enabled = title.isNotBlank() && url.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PastelPinkPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_dialog_confirm")
            ) {
                Text("Upload Link ✨", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SoftTextMuted)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}
