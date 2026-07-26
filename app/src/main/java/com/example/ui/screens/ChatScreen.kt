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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.SentimentSatisfied
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.UserRole
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUser: UserRole,
    chatMessages: List<ChatMessage>,
    latestStickyNote: ChatMessage?,
    onSendMessage: (text: String, sticker: String?) -> Unit,
    onPostStickyNote: (text: String) -> Unit,
    onToggleHeart: (Int) -> Unit,
    onDeleteMessage: (Int) -> Unit,
    onSwitchUserClick: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var showStickerPicker by remember { mutableStateOf(false) }
    var showStickyNoteDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val otherUser = if (currentUser == UserRole.ADMIN) UserRole.LILLU else UserRole.ADMIN
    val stickers = listOf("💖", "🌸", "🍓", "🐣", "✨", "🎀", "🧸", "🍭", "💌", "🌟")

    // Auto-scroll to bottom on new message
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(PastelPinkPrimary.copy(alpha = 0.2f), CircleShape)
                            .border(1.5.dp, PastelPinkPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = otherUser.avatarEmoji, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = otherUser.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SoftTextDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF4CAF50), CircleShape)
                            )
                        }
                        Text(
                            text = "Private Chat • Active in MyLillu",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    if (currentUser == UserRole.ADMIN) {
                        IconButton(onClick = { showStickyNoteDialog = true }) {
                            Icon(Icons.Default.Pin, contentDescription = "Pin Note", tint = PastelPinkPrimary)
                        }
                    }

                    IconButton(
                        onClick = onSwitchUserClick,
                        modifier = Modifier
                            .background(PastelSecondaryLavender.copy(alpha = 0.3f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Switch User", tint = SoftTextDark)
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Column {
                    AnimatedVisibility(visible = showStickerPicker) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF8FB))
                                .padding(12.dp)
                        ) {
                            items(stickers) { sticker ->
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.dp, PastelPinkPrimary.copy(alpha = 0.3f), CircleShape)
                                        .clickable {
                                            onSendMessage("", sticker)
                                            showStickerPicker = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = sticker, fontSize = 24.sp)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showStickerPicker = !showStickerPicker }) {
                            Icon(
                                Icons.Default.SentimentSatisfied,
                                contentDescription = "Stickers",
                                tint = PastelPinkPrimary
                            )
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    "Message as ${currentUser.displayName}...",
                                    fontSize = 14.sp,
                                    color = SoftTextMuted
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SoftTextDark,
                                unfocusedTextColor = SoftTextDark,
                                focusedBorderColor = PastelPinkPrimary,
                                unfocusedBorderColor = PastelCardOutline,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FloatingActionButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onSendMessage(inputText, null)
                                    inputText = ""
                                }
                            },
                            containerColor = PastelPinkPrimary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("send_chat_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = PastelPinkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Pinned Sticky Note Banner
            if (latestStickyNote != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    color = PastelPeach,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📌", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sticky Note from ${latestStickyNote.senderName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SoftTextDark
                            )
                            Text(
                                text = latestStickyNote.messageText,
                                fontSize = 13.sp,
                                color = SoftTextDark
                            )
                        }
                    }
                }
            }

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                items(
                    items = chatMessages.filter { !it.isStickyNote },
                    key = { it.id }
                ) { message ->
                    ChatMessageBubble(
                        message = message,
                        isFromCurrentUser = message.senderId == currentUser.name,
                        onToggleHeart = { onToggleHeart(message.id) },
                        onDeleteMessage = { onDeleteMessage(message.id) }
                    )
                }
            }
        }
    }

    if (showStickyNoteDialog) {
        var noteInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showStickyNoteDialog = false },
            title = { Text("📌 Post Pinned Sticky Note", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    placeholder = { Text("e.g. Hope you enjoy these new web apps! 💖") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteInput.isNotBlank()) {
                            onPostStickyNote(noteInput)
                            showStickyNoteDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PastelPinkPrimary)
                ) {
                    Text("Pin Note ✨")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStickyNoteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    isFromCurrentUser: Boolean,
    onToggleHeart: () -> Unit,
    onDeleteMessage: () -> Unit
) {
    val relativeTime = DateUtils.getRelativeTimeSpanString(
        message.timestamp,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    )

    Column(
        horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isFromCurrentUser) 20.dp else 4.dp,
                    bottomEnd = if (isFromCurrentUser) 4.dp else 20.dp
                ),
                color = if (isFromCurrentUser) PastelPinkPrimary else Color.White,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .clickable { onToggleHeart() }
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Column {
                        if (!isFromCurrentUser) {
                            Text(
                                text = message.senderName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PastelPinkPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }

                        if (!message.stickerEmoji.isNull_Empty()) {
                            Text(text = message.stickerEmoji!!, fontSize = 36.sp)
                        }

                        if (message.messageText.isNotBlank()) {
                            Text(
                                text = message.messageText,
                                fontSize = 14.sp,
                                color = if (isFromCurrentUser) Color.White else SoftTextDark
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = relativeTime.toString(),
                                fontSize = 10.sp,
                                color = if (isFromCurrentUser) Color.White.copy(alpha = 0.8f) else Color.Gray
                            )

                            if (message.isHearted) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = "Hearted",
                                    tint = if (isFromCurrentUser) Color.White else PastelRoseAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = onToggleHeart,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (message.isHearted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Heart Reaction",
                    tint = if (message.isHearted) PastelRoseAccent else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun String?.isNull_Empty(): Boolean = this == null || this.isEmpty()
