package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.data.model.Language
import com.example.data.model.UserRole
import com.example.ui.strings.Strings
import com.example.ui.theme.*

@Composable
fun XLinkHeaderBar(
    language: Language,
    currentRole: UserRole,
    unreadNotifCount: Int,
    onToggleLanguage: () -> Unit,
    onRoleSelected: (UserRole) -> Unit,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand logo & title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("brand_logo_row")
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF00D2FF), Color(0xFF0284C7))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "X",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "X-Link",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "LTD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = if (language == Language.BANGLA) "ফাইবার ব্রডব্যান্ড নেটওয়ার্ক" else "ISP Billing & Care",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Controls: Language toggle + Notification Bell
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Switcher
                    FilledTonalButton(
                        onClick = onToggleLanguage,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("language_toggle_button")
                    ) {
                        Text(
                            text = if (language == Language.BANGLA) "বাং | EN" else "EN | বাং",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Notifications icon with badge
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("notification_bell_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifCount > 0) {
                                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                                        Text(text = "$unreadNotifCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Role selector segment (Customer Portal vs Agent Dashboard)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val isCustomer = currentRole == UserRole.CUSTOMER
                // Customer Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCustomer) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onRoleSelected(UserRole.CUSTOMER) }
                        .padding(vertical = 8.dp)
                        .testTag("role_customer_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isCustomer) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.get(language, "role_customer"),
                            fontSize = 13.sp,
                            fontWeight = if (isCustomer) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCustomer) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Agent Portal Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isCustomer) MaterialTheme.colorScheme.secondary else Color.Transparent)
                        .clickable { onRoleSelected(UserRole.AGENT_ADMIN) }
                        .padding(vertical = 8.dp)
                        .testTag("role_agent_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = if (!isCustomer) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.get(language, "role_agent"),
                            fontSize = 13.sp,
                            fontWeight = if (!isCustomer) FontWeight.Bold else FontWeight.Normal,
                            color = if (!isCustomer) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionStatusChip(
    status: ConnectionStatus,
    language: Language,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        ConnectionStatus.ACTIVE -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF1B5E20),
            if (language == Language.BANGLA) "সক্রিয় সংযোগ" else "Active Connection"
        )
        ConnectionStatus.EXPIRED -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFB71C1C),
            if (language == Language.BANGLA) "বকেয়া / মেয়াদোত্তীর্ণ" else "Expired / Due"
        )
        ConnectionStatus.SUSPENDED -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            if (language == Language.BANGLA) "সংযোগ স্থগিত" else "Suspended"
        )
        ConnectionStatus.PENDING -> Triple(
            Color(0xFFEDE7F6),
            Color(0xFF4A148C),
            if (language == Language.BANGLA) "অপেক্ষমান" else "Pending Setup"
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        textColor.copy(alpha = if (status == ConnectionStatus.ACTIVE) alphaPulse else 1f)
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun SignalMetricsWidget(
    opticalDbm: Double,
    pingMs: Int,
    downloadMbps: Double,
    uploadMbps: Double,
    language: Language,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WifiTethering,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == Language.BANGLA) "টেলিকম সিগন্যাল ও ফাইবার স্থিতি" else "Optical Fiber & Signal Telemetry",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (opticalDbm > -25.0 && opticalDbm < -14.0) "Optimal RX" else "Check Fiber",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Optical Power
                MetricTile(
                    title = if (language == Language.BANGLA) "অপটিক্যাল RX" else "Optical Power",
                    value = "$opticalDbm dBm",
                    subtitle = if (language == Language.BANGLA) "স্বাভাবিক মান" else "Good Range",
                    icon = Icons.Default.Cable,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Ping Latency
                MetricTile(
                    title = if (language == Language.BANGLA) "পিং লেটেন্সি" else "Ping Latency",
                    value = "$pingMs ms",
                    subtitle = if (language == Language.BANGLA) "আল্ট্রা লো" else "Ultra Low",
                    icon = Icons.Default.Speed,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Throughput
                MetricTile(
                    title = if (language == Language.BANGLA) "ডাউনলোড" else "Live Down",
                    value = "${downloadMbps.toInt()} Mbps",
                    subtitle = "↑ ${uploadMbps.toInt()} Mbps",
                    icon = Icons.Default.CloudDownload,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricTile(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
