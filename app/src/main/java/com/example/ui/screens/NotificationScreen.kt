package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.model.ViewingNotification
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    currentUser: UserRole,
    notifications: List<ViewingNotification>,
    onMarkAllRead: () -> Unit,
    onClearAll: () -> Unit
) {
    LaunchedEffect(Unit) {
        onMarkAllRead()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = PastelPinkPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Viewing Activity Feed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = SoftTextDark
                        )
                    }
                },
                actions = {
                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = onClearAll) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear", tint = PastelRoseAccent)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", color = PastelRoseAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = PastelPinkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Informational Header Note Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelCardOutline)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(PastelPinkBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentUser == UserRole.ADMIN)
                                "Live Web App Activity Feed"
                            else
                                "Your Web App Visits",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SoftTextDark
                        )
                        Text(
                            text = if (currentUser == UserRole.ADMIN)
                                "Real-time alerts logged whenever Lillu accesses any web app."
                            else
                                "Every time you open an app link, a visit log is recorded here.",
                            fontSize = 12.sp,
                            color = SoftTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔔", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No access alerts yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = SoftTextDark
                        )
                        Text(
                            text = "Activity logs will appear here when web apps are opened!",
                            fontSize = 13.sp,
                            color = SoftTextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notifications, key = { it.id }) { item ->
                        NotificationItemCard(item = item)
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItemCard(item: ViewingNotification) {
    val relativeTime = DateUtils.getRelativeTimeSpanString(
        item.timestamp,
        System.currentTimeMillis(),
        DateUtils.SECOND_IN_MILLIS
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, PastelCardOutline)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(PastelPinkBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = null,
                    tint = PastelPinkPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.webAppTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SoftTextDark
                    )
                    Text(
                        text = relativeTime.toString(),
                        fontSize = 11.sp,
                        color = SoftTextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "✨ ${item.accessedBy} accessed this web app!",
                    fontSize = 13.sp,
                    color = PastelPinkPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
