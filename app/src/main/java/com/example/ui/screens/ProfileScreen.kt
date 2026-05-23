package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Header Profile Card ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), Color.Transparent),
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Avatar representation
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AI",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Asik Ikbal (Asif)",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.testTag("profile_asif_name")
                )

                Text(
                    text = "Age: 20 • West Bengal, India",
                    fontSize = 14.sp,
                    color = NatSubText,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Asif's Personal AI Companion",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Social Links Interactive Row ---
        Text(
            text = "Connect with Asif",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SocialButton(
                title = "GitHub",
                iconText = "GH",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f).testTag("social_github"),
                onClick = { openUrl("https://github.com/Unfavasik") }
            )
            Spacer(modifier = Modifier.width(6.dp))
            SocialButton(
                title = "LinkedIn",
                iconText = "In",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f).testTag("social_linkedin"),
                onClick = { openUrl("https://in.linkedin.com/in/asik-ikbal-6445a932b") }
            )
            Spacer(modifier = Modifier.width(6.dp))
            SocialButton(
                title = "Twitter / X",
                iconText = "X",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f).testTag("social_twitter"),
                onClick = { openUrl("https://x.com/Unfav_asik") }
            )
            Spacer(modifier = Modifier.width(6.dp))
            SocialButton(
                title = "Instagram",
                iconText = "IG",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f).testTag("social_instagram"),
                onClick = { openUrl("https://www.instagram.com/unfav_asik") }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Core Passion / About Card ---
        AboutCardSection()

        Spacer(modifier = Modifier.height(20.dp))

        // --- Skills and Assist Offerings ---
        CapabilitiesSection()

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SocialButton(
    title: String,
    iconText: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(58.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AboutCardSection() {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("about_card_section"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "💡 Real-time Interests & Passions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            InterestItem(
                emoji = "⚽",
                title = "Football & Sports Communities",
                desc = "Crazy enthusiastic about Football and FIFA games. Actively connects with sports communities."
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

            InterestItem(
                emoji = "🪙",
                title = "Crypto, Blockchain & SportsFi",
                desc = "Explores Web3 trends, crypto markets, decentralized tokens, and fan engagement platforms."
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

            InterestItem(
                emoji = "🎙️",
                title = "Podcasts & Creative Writing",
                desc = "Enjoys content ideation, podcast scripting, copywriting, and exploring tech growth topics."
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

            InterestItem(
                emoji = "🚀",
                title = "Self Development & AI",
                desc = "Curious minded, loves daily learning, building with cool AI tools, and aiming for big dreams."
            )
        }
    }
}

@Composable
fun InterestItem(emoji: String, title: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(text = emoji, fontSize = 20.sp, modifier = Modifier.padding(top = 2.dp, end = 10.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = NatSubText,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun CapabilitiesSection() {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("capabilities_section"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚡ What My AI Assistant Can Help With",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            val services = listOf(
                "📝 Smart Captions & Ideas for Socials",
                "🎙️ Podcast Script Outline & Content Structure",
                "⚽ Global Football Discussions & Tacts",
                "🪙 Blockchain & Crypto Tech Explanations",
                "🤝 Personal self-growth & Habit advice",
                "🇧🇩 Bangla / Banglish Colloquial Discussions"
            )

            services.forEach { service ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = service,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
