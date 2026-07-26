package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUser: UserRole,
    onSwitchUserClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile & Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SoftTextDark
                    )
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelCardOutline)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(8.dp, CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(PastelPinkPrimary, PastelPeach)
                                ),
                                CircleShape
                            )
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = currentUser.avatarEmoji, fontSize = 42.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentUser.displayName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftTextDark
                    )

                    Text(
                        text = currentUser.handle,
                        fontSize = 14.sp,
                        color = PastelPinkPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onSwitchUserClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PastelPinkPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("switch_account_button")
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Switch Account")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Switch Account / Re-login", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Credentials Info Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelCardOutline)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = PastelPinkPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Account Credentials Info",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SoftTextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "• Lillu Passcode: lillu123\n• Admin Passcode: admin123",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SoftTextMuted,
                        lineHeight = 22.sp
                    )
                }
            }

            // About MyLillu Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelCardOutline)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = PastelRoseAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About MyLillu",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SoftTextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "MyLillu is a cute, lightweight personalized web app hub made specifically for Lillu. All your favorite custom web apps in one organized space with real-time access alerts and private chat!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SoftTextMuted,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
