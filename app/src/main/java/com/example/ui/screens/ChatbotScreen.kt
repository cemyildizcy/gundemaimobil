@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.ui.ChatMessage
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkSecondary
import kotlinx.coroutines.launch

@Composable
fun ChatbotScreen(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = DarkSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("GündemAI'ye Sor", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Yapay Zekâ Gündem Asistanı", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_chatbot_button")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Kapat")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Chat Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                items(chatHistory) { message ->
                    val isUser = message.sender == "USER"
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 0.dp,
                                bottomEnd = if (isUser) 0.dp else 16.dp
                            ),
                            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .testTag("chat_bubble_${message.sender}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = message.text,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                if (isChatLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = DarkSecondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GündemAI cevap üretiyor...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Quick Recommendations list (tap to send question)
            val recommendations = listOf(
                "Bugün yapay zekâ alanında ne oldu?",
                "Osimhen transferinde son durum ne?",
                "TCMB faiz kararı açıklandı mı?",
                "En önemli gelişmeler ne?"
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recommendations) { rec ->
                    Surface(
                        onClick = {
                            viewModel.sendMessageToAI(rec)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.testTag("rec_query_chip_$rec")
                    ) {
                        Text(
                            text = rec,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Bottom Input Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Asistana bir soru sorun...", fontSize = 13.sp) },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chatbot_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessageToAI(textInput)
                                textInput = ""
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("chatbot_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Gönder",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
