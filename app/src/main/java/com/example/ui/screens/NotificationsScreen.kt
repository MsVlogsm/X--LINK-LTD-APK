package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.AppNotification
import com.example.data.model.Language
import com.example.data.model.NotificationType
import com.example.ui.strings.Strings

@Composable
fun NotificationsScreen(
    notifications: List<AppNotification>,
    language: Language,
    onMarkAllRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<NotificationType?>(null) }

    val filteredList = remember(notifications, selectedFilter) {
        if (selectedFilter == null) notifications else notifications.filter { it.type == selectedFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Strings.get(language, "notifications_title"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${notifications.size} Automated Triggers & Alerts",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                TextButton(
                    onClick = onMarkAllRead,
                    modifier = Modifier.testTag("mark_all_read_button")
                ) {
                    Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = Strings.get(language, "mark_all_read"), fontSize = 11.sp)
                }
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == NotificationType.BILL_DUE,
                    onClick = { selectedFilter = NotificationType.BILL_DUE },
                    label = { Text(if (language == Language.BANGLA) "বিল নোটিশ" else "Bill Due", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == NotificationType.PAYMENT_SUCCESS,
                    onClick = { selectedFilter = NotificationType.PAYMENT_SUCCESS },
                    label = { Text(if (language == Language.BANGLA) "পেমেন্ট" else "Payments", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == NotificationType.MAINTENANCE,
                    onClick = { selectedFilter = NotificationType.MAINTENANCE },
                    label = { Text(if (language == Language.BANGLA) "মেইনটেন্যান্স" else "Maintenance", fontSize = 11.sp) }
                )
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = Strings.get(language, "no_notifications"),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
            items(filteredList) { notif ->
                NotificationItemCard(notification = notif, language = language)
            }
        }
    }
}

@Composable
fun NotificationItemCard(
    notification: AppNotification,
    language: Language
) {
    val (icon, iconColor, bgColor) = when (notification.type) {
        NotificationType.BILL_DUE -> Triple(Icons.Default.ReceiptLong, Color(0xFFF97316), Color(0xFFFFF7ED))
        NotificationType.PAYMENT_SUCCESS -> Triple(Icons.Default.CheckCircle, Color(0xFF10B981), Color(0xFFF0FDF4))
        NotificationType.DISCONNECTION_ALERT -> Triple(Icons.Default.Warning, Color(0xFFEF4444), Color(0xFFFEF2F2))
        NotificationType.NEW_CONNECTION -> Triple(Icons.Default.AddLink, Color(0xFF0284C7), Color(0xFFF0F9FF))
        NotificationType.MAINTENANCE -> Triple(Icons.Default.Construction, Color(0xFF8B5CF6), Color(0xFFF5F3FF))
        NotificationType.API_SYNC -> Triple(Icons.Default.Sync, Color(0xFF0284C7), Color(0xFFF0F9FF))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else bgColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == Language.BANGLA) notification.titleBn else notification.titleEn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = notification.timestamp,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (language == Language.BANGLA) notification.messageBn else notification.messageEn,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
