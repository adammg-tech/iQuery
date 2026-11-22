package com.adammg.iquery.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.StarHalf
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adammg.iquery.data.model.AppItem
import com.adammg.iquery.ui.theme.RatingGold

@Composable
fun RatingStarsBar(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 14.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val clampedRating = rating.coerceIn(0.0, 5.0)
        repeat(5) { index ->
            val starValue = index + 1
            val icon = when {
                clampedRating >= starValue - 0.25 -> Icons.Rounded.Star
                clampedRating >= starValue - 0.75 -> Icons.AutoMirrored.Rounded.StarHalf
                else -> Icons.Rounded.StarOutline
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RatingGold,
                modifier = Modifier.size(starSize)
            )
            if (index < 4) {
                Spacer(modifier = Modifier.width(1.dp))
            }
        }
    }
}

@Composable
fun KeyStatsCard(
    appItem: AppItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stat 1: Rating with precise full / half / outlined stars
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${appItem.formattedRating} ★",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                RatingStarsBar(
                    rating = appItem.averageUserRating ?: 0.0,
                    starSize = 12.dp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = appItem.formattedRatingCount,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            StatDivider()

            // Stat 2: Age Rating
            StatColumn(
                title = appItem.contentAdvisoryRating ?: "4+",
                subtitle = "AGE"
            )

            StatDivider()

            // Stat 3: Version
            StatColumn(
                title = appItem.version ?: "1.0",
                subtitle = "VERSION"
            )

            StatDivider()

            // Stat 4: Size
            StatColumn(
                title = appItem.formattedFileSize.ifBlank { "N/A" },
                subtitle = "SIZE"
            )
        }
    }
}

@Composable
private fun StatColumn(
    title: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun StatDivider() {
    HorizontalDivider(
        modifier = Modifier
            .height(28.dp)
            .width(1.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    )
}
