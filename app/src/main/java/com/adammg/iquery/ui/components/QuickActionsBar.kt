package com.adammg.iquery.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adammg.iquery.data.model.AppItem
import com.adammg.iquery.ui.theme.RoyalBlue

@Composable
fun QuickActionsBar(
    appItem: AppItem,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storeUrl = appItem.trackViewUrl ?: "https://apps.apple.com"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Open App Store Button
        Button(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .weight(1.8f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RoyalBlue,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.OpenInNew,
                contentDescription = null,
                modifier = Modifier.width(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "App Store",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }

        // 2. Share Button
        OutlinedButton(
            onClick = {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, appItem.trackName)
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Check out ${appItem.trackName} on the App Store:\n$storeUrl"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share App Link"))
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not share link", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // 3. Copy Link Button
        OutlinedButton(
            onClick = {
                try {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("App Store Link", storeUrl)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to copy link", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = "Copy Link",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
