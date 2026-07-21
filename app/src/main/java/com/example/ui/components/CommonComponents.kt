package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

@Composable
fun GundemAILogo(
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 32.dp,
    innerSize: androidx.compose.ui.unit.Dp = 12.dp,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleLarge
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Geometric Icon Box
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(8.dp))
                .background(BrandGradient),
            contentAlignment = Alignment.Center
        ) {
            // White rotated square inside
            Box(
                modifier = Modifier
                    .size(innerSize)
                    .clip(RoundedCornerShape(2.dp))
                    .rotate(45f)
                    .background(Color.White)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Gündem",
            style = textStyle,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "AI",
            style = textStyle.copy(
                brush = BrandTextGradient
            ),
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun StatusBadge(status: VerificationStatus) {
    val (text, bgColor, textColor) = when (status) {
        VerificationStatus.VERIFIED -> Triple("Doğrulandı", DarkTertiary.copy(alpha = 0.15f), DarkTertiary)
        VerificationStatus.MULTIPLE_SOURCES -> Triple("Çoklu Kaynak", AlertBlue.copy(alpha = 0.15f), AlertBlue)
        VerificationStatus.DEVELOPING -> Triple("Gelişiyor", PremiumGold.copy(alpha = 0.15f), PremiumGold)
        VerificationStatus.CLAIM -> Triple("İddia", SoftGray.copy(alpha = 0.15f), SoftGray)
        VerificationStatus.OFFICIAL_STATEMENT -> Triple("Resmi Açıklama", DarkSecondary.copy(alpha = 0.15f), DarkSecondary)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ImportanceBadge(level: ImportanceLevel) {
    val (text, color) = when (level) {
        ImportanceLevel.CRITICAL -> Pair("Kritik", DarkError)
        ImportanceLevel.HIGH -> Pair("Yüksek", PremiumGold)
        ImportanceLevel.MEDIUM -> Pair("Orta", AlertBlue)
        ImportanceLevel.LOW -> Pair("Düşük", SoftGray)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PlatformIcon(platform: PlatformType, modifier: Modifier = Modifier) {
    val (icon, color) = when (platform) {
        PlatformType.X -> Pair("X", Color.White)
        PlatformType.THREADS -> Pair("Th", Color(0xFFE2E8F0))
        PlatformType.YOUTUBE -> Pair("YT", Color(0xFFEF4444))
        PlatformType.BLOG -> Pair("Bl", Color(0xFFF59E0B))
        PlatformType.NEWS -> Pair("Hbr", Color(0xFF3B82F6))
        PlatformType.OFFICIAL -> Pair("KAP", Color(0xFF10B981))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        Text(
            text = icon,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SkeletonStoryCard() {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = alpha)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun AIOverlayDisclaimer() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Bu özet ve analiz yapay zekâ asistanımız yardımıyla oluşturulmuştur. Hatalar içerebilir. Karar vermeden önce orijinal kaynakları inceleyin.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun EmptyStateView(
    title: String,
    description: String,
    icon: ImageVector = Icons.Filled.Feed,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun SummaryDialog(
    summaryText: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = DarkSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GündemAI 2 Dakikalık Özet",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                Text(
                    text = summaryText,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                AIOverlayDisclaimer()
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_summary_button")
            ) {
                Text("Anladım")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
