package com.example.aurorah.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurorah.data.local.entity.NewsEntity
import com.example.aurorah.ui.theme.LocalExtendedColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToVolunteer: () -> Unit,
    onNavigateToRequestAssistance: () -> Unit,
    onNavigateToDonate: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToImpact: () -> Unit
) {
    val newsList by viewModel.newsList.collectAsState()
    val extendedColors = LocalExtendedColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // --- HERO BANNER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Serving Kuils River & Cape Metro",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Aurorah CAN - Community Action Network Serving Kuils River",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Empowering neighborhoods through localized mutual aid, emergency food distribution, youth support, and community solidarity.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateToVolunteer,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VolunteerActivism,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Get Involved", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onNavigateToAbout,
                        shape = RoundedCornerShape(24.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.onPrimary,
                                    MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Learn More", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- IMPACT STAT COUNTERS ---
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Community Impact at a Glance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    number = "250+",
                    label = "Active Volunteers",
                    icon = Icons.Rounded.Group,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    number = "5,200+",
                    label = "People Served",
                    icon = Icons.Rounded.Handshake,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    number = "R350k+",
                    label = "Funds Raised",
                    icon = Icons.Rounded.Favorite,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    number = "100%",
                    label = "Transparency %",
                    icon = Icons.Rounded.Campaign,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- HOW YOU CAN HELP SECTION ---
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "How You Can Help",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Choose a way to make a tangible difference in Kuils River & surrounding areas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            ActionCard(
                title = "1. Become a Volunteer",
                subtitle = "Share your skills, assist in food prep, distribution, youth tutoring, or emergency response.",
                buttonText = "Volunteer Now",
                icon = Icons.Rounded.VolunteerActivism,
                containerColor = extendedColors.mintGreenContainer,
                contentColor = extendedColors.mintGreenText,
                onClick = onNavigateToVolunteer
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActionCard(
                title = "2. Request Assistance",
                subtitle = "Need help with food hampers, medical support, or emergency community aid? Apply safely.",
                buttonText = "Apply for Aid",
                icon = Icons.Rounded.Handshake,
                containerColor = extendedColors.amberContainer,
                contentColor = extendedColors.amberText,
                onClick = onNavigateToRequestAssistance
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActionCard(
                title = "3. Donate & Fund Drives",
                subtitle = "100% transparent funding directly powering food security drives and local family aid.",
                buttonText = "Donate Funds",
                icon = Icons.Rounded.Favorite,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToDonate
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- LIVE NEWS FEED STREAM ---
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Live Community Feed",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Real-time news & drive updates",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    onClick = onNavigateToImpact,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (newsList.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No community news available at this moment.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                newsList.forEach { news ->
                    NewsItemCard(news = news, onClick = onNavigateToImpact)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- ABOUT SUMMARY & FOOTER ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "About Aurorah CAN",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"Empowering local communities starts with listening to their needs and standing together in active mutual support.\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "— Latifah Jacobs, Founder & Executive Director",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Contact: info@aurorahcan.org • +27 21 903 0000\nKuils River, Cape Town, 7580, South Africa\nPOPIA Compliant • Registered NPO",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun StatCard(
    number: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    buttonText: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = buttonText,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun NewsItemCard(
    news: NewsEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (news.urgency == "High") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = news.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (news.urgency == "High") MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = news.publishDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = news.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "By ${news.author}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
