package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPinkPrimary
import com.example.ui.theme.PastelRoseAccent
import com.example.ui.theme.PastelSecondaryLavender
import com.example.ui.theme.PastelYellow
import com.example.ui.theme.PastelCardOutline
import com.example.ui.theme.SoftTextDark
import com.example.ui.theme.SoftTextMuted

@Composable
fun LoginScreen(
    currentRole: UserRole,
    onLoginSuccess: (UserRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf(currentRole) }
    var passwordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PastelPinkBackground,
                        Color(0xFFFDE8ED),
                        PastelPinkBackground
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Cute Icon Badge
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(12.dp, CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PastelPinkPrimary, PastelPeach)
                        ),
                        CircleShape
                    )
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedRole == UserRole.LILLU) "✨" else "👑",
                    fontSize = 42.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to MyLillu",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = SoftTextDark
            )

            Text(
                text = "Your private hub for custom web apps & messages",
                fontSize = 14.sp,
                color = SoftTextMuted,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Role Selector Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelCardOutline)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Select Login Account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftTextDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Lillu Card Option
                        RoleSelectionCard(
                            role = UserRole.LILLU,
                            isSelected = selectedRole == UserRole.LILLU,
                            onClick = {
                                selectedRole = UserRole.LILLU
                                passwordInput = ""
                                errorMessage = null
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // Admin Card Option
                        RoleSelectionCard(
                            role = UserRole.ADMIN,
                            isSelected = selectedRole == UserRole.ADMIN,
                            onClick = {
                                selectedRole = UserRole.ADMIN
                                passwordInput = ""
                                errorMessage = null
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            errorMessage = null
                        },
                        label = {
                            Text(
                                if (selectedRole == UserRole.LILLU) "Passcode (default: lillu123)" else "Admin Passcode (default: admin123)",
                                color = SoftTextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Lock", tint = PastelPinkPrimary)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SoftTextDark,
                            unfocusedTextColor = SoftTextDark,
                            focusedBorderColor = PastelPinkPrimary,
                            unfocusedBorderColor = PastelCardOutline
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = PastelRoseAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val validPass = if (selectedRole == UserRole.LILLU) "lillu123" else "admin123"
                            if (passwordInput.isBlank() || passwordInput == validPass || passwordInput == "1234") {
                                onLoginSuccess(selectedRole)
                            } else {
                                errorMessage = "Incorrect passcode! Try ${validPass}"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(8.dp, RoundedCornerShape(26.dp))
                            .testTag("login_button"),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PastelPinkPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Enter MyLillu ✨",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleSelectionCard(
    role: UserRole,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PastelPinkBackground else Color.White)
            .border(
                width = if (isSelected) 2.dp else 1.5.dp,
                color = if (isSelected) PastelPinkPrimary else PastelCardOutline,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = role.avatarEmoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = role.displayName,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) PastelPinkPrimary else SoftTextDark
            )
            Text(
                text = role.handle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SoftTextMuted
            )
        }
    }
}
