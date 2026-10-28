package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.Language
import com.example.ui.ai.AiChatState
import com.example.ui.ai.ChatMessage

@Composable
fun AiSupportScreen(
    customer: Customer,
    language: Language,
    messages: List<ChatMessage>,
    chatState: AiChatState,
    onSendMessage: (String) -> Unit,
    onAnalyzeHealth: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, chatState) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // AI Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0284C7), Color(0xFF00D2FF))
                        )
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == Language.BANGLA) "এক্স-লিংক AI নেটওয়ার্ক অ্যাসিস্ট্যান্ট" else "X-Link Gemini Network AI",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (language == Language.BANGLA)
                                "Gemini 3.8 Flash • রিয়েল-টাইম ফাইবার হেলথ ও ট্রাবলশুটার"
                            else
                                "Powered by Gemini 3.8 Flash & Firebase AI Logic",
                            fontSize = 11.sp,
                            color = Color(0xFFE0F2FE)
                        )
                    }
                }
            }
        }

        // Quick Prompt Suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SuggestionChip(
                onClick = onAnalyzeHealth,
                label = {
                    Text(
                        text = if (language == Language.BANGLA) "🔬 সিগন্যাল বিশ্লেষণ" else "🔬 Signal Health",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                modifier = Modifier.testTag("ai_chip_analyze_signal")
            )
            SuggestionChip(
                onClick = {
                    val q = if (language == Language.BANGLA)
                        "আমার ইন্টারনেট স্পিড ও ওয়াইফাই কভারেজ অপ্টিমাইজ করার টিপস দিন।"
                    else
                        "Give me tips to optimize my Wi-Fi coverage and latency."
                    onSendMessage(q)
                },
                label = {
                    Text(
                        text = if (language == Language.BANGLA) "📶 স্পিড টিপস" else "📶 Speed Tips",
                        fontSize = 11.sp
                    )
                }
            )
            SuggestionChip(
                onClick = {
                    val q = if (language == Language.BANGLA)
                        "গেমিং ও ৪কে স্ট্রিমিংয়ের জন্য কোন প্যাকেজটি সেরা?"
                    else
                        "Which package is best for low latency esports gaming?"
                    onSendMessage(q)
                },
                label = {
                    Text(
                        text = if (language == Language.BANGLA) "⚡ প্যাকেজ আপগ্রেড" else "⚡ Package Help",
                        fontSize = 11.sp
                    )
                }
            )
        }

        // Error message banner if state is Error
        if (chatState is AiChatState.Error) {
            Surface(
                color = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = chatState.errorMessage,
                        fontSize = 11.sp,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }

        // Messages Feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }

            // Live streaming partial response
            if (chatState is AiChatState.Streaming) {
                item {
                    StreamingBubble(partialText = chatState.partialResponse)
                }
            } else if (chatState is AiChatState.Loading) {
                item {
                    LoadingBubble()
                }
            }
        }

        // Bottom Input Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp, top = 4.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (language == Language.BANGLA) "AI অ্যাসিস্ট্যান্টকে প্রশ্ন করুন..." else "Ask Gemini AI anything...",
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_input_field"),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                val isGenerating = chatState is AiChatState.Loading || chatState is AiChatState.Streaming
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isGenerating) {
                            val query = inputText.trim()
                            inputText = ""
                            onSendMessage(query)
                        }
                    },
                    enabled = inputText.isNotBlank() && !isGenerating,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !isGenerating) MaterialTheme.colorScheme.primary else Color.LightGray)
                        .testTag("ai_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "user"
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.widthIn(max = 320.dp),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0284C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (isUser) 14.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 14.dp
                ),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StreamingBubble(partialText: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.widthIn(max = 320.dp)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0284C7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = partialText,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingBubble() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0284C7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Gemini AI is analyzing...", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
