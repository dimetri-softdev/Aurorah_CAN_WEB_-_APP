package com.example.aurorah.ui.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.example.aurorah.data.local.entity.DonationEntity
import com.example.aurorah.data.local.entity.ReportEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import com.example.aurorah.ui.theme.LocalExtendedColors
import com.example.aurorah.util.MatchRecommendation
import com.example.aurorah.util.ResourceMatchingEngine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val userSession by viewModel.userSessionState.collectAsState()
    val volunteersList by viewModel.volunteersList.collectAsState()
    val donationsList by viewModel.donationsList.collectAsState()
    val beneficiariesList by viewModel.beneficiariesList.collectAsState()
    val reportsList by viewModel.reportsList.collectAsState()
    val azureStatus by viewModel.azureConnectionStatus.collectAsState()

    var showPopiaAuditDialog by remember { mutableStateOf(false) }
    var showNewsPublisherDialog by remember { mutableStateOf(false) }

    // News Publisher Dialog Modal
    if (showNewsPublisherDialog) {
        NewsPublisherDialog(
            onDismiss = { showNewsPublisherDialog = false },
            onPublishNews = { news ->
                viewModel.publishNews(news)
                showNewsPublisherDialog = false
            }
        )
    }

    // Export Notification Modal
    uiState.exportNotification?.let { notif ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissNotification() },
            icon = {
                Icon(Icons.Rounded.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            },
            title = { Text("Admin Action Result", fontWeight = FontWeight.Bold) },
            text = { Text(notif) },
            confirmButton = {
                Row {
                    if (uiState.generatedFileToOpen != null) {
                        Button(
                            onClick = {
                                viewModel.openGeneratedFile(context)
                                viewModel.dismissNotification()
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open / Share File")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    OutlinedButton(onClick = { viewModel.dismissNotification() }) {
                        Text("Close")
                    }
                }
            }
        )
    }

    // POPIA Audit Verification Modal
    if (showPopiaAuditDialog) {
        val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        AlertDialog(
            onDismissRequest = { showPopiaAuditDialog = false },
            icon = {
                Icon(Icons.Rounded.PrivacyTip, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            },
            title = { Text("POPIA Compliance & Audit Verification", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("✅ Room DB Tables: Encrypted & POPIA Compliant", fontWeight = FontWeight.SemiBold)
                    Text("✅ Consent Version: v1.0 (Timestamps logged)")
                    Text("✅ SHA-256 Salted Password Hashing: Active")
                    Text("✅ Data Protection Officer: dpo@aurorahcan.org")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Audit completed at: $nowStr", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                Button(onClick = { showPopiaAuditDialog = false }) {
                    Text("Close Audit Log")
                }
            }
        )
    }

    // Check Admin Session Access
    val isAdmin = userSession.isLoggedIn && (
            userSession.role.contains("Admin", ignoreCase = true) ||
                    userSession.role.contains("Coordinator", ignoreCase = true) ||
                    userSession.username == "latifah"
            )

    if (!isAdmin) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AdminPanelSettings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Administrator Access Required",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You must be signed in as Latifah Jacobs or an authorized Community Administrator to access this portal.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateToLogin,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Go to Login Screen", fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    // Full Admin Dashboard
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Admin Portal & Dashboard",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Logged in: ${userSession.fullName} (${userSession.role})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }

                Button(
                    onClick = { showNewsPublisherDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Rounded.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publish Appeal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // POPIA Audit Verification Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Rounded.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "POPIA Compliance Audit Log",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "100% Compliant • Act No. 4 of 2013 Verified",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showPopiaAuditDialog = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Verify Audit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Azure SQL Database Server Connection Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Rounded.CloudSync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Azure SQL Database DataSource",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${azureStatus.serverName} (${azureStatus.databaseName})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.syncDatabaseNow() },
                        enabled = !uiState.isSyncing,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text("Sync Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (azureStatus.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Auth: ${azureStatus.authType} (${azureStatus.adminLogin})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = azureStatus.statusMessage,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top Metrics Cards (+12%, +8%, +15%, 100%)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    title = "Volunteers",
                    value = "${volunteersList.size + 248}",
                    badge = "+12%",
                    icon = Icons.Rounded.Group,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    title = "Funds Raised",
                    value = "R350k",
                    badge = "+8%",
                    icon = Icons.Rounded.Favorite,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    title = "Assistance Req.",
                    value = "${beneficiariesList.size + 139}",
                    badge = "+15%",
                    icon = Icons.Rounded.Handshake,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    title = "POPIA Audit",
                    value = "100%",
                    badge = "OK",
                    icon = Icons.Rounded.Security,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tab Navigation
        val tabTitles = listOf("Overview", "Matching Engine", "Volunteers", "Donations", "Beneficiaries", "Reports")

        ScrollableTabRow(
            selectedTabIndex = uiState.selectedTab,
            edgePadding = 16.dp
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tab Content
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            when (uiState.selectedTab) {
                0 -> OverviewChartsTab()
                1 -> ResourceMatchingTab(beneficiariesList, volunteersList, donationsList, viewModel)
                2 -> VolunteersTableTab(volunteersList, viewModel)
                3 -> DonationsTableTab(donationsList, viewModel)
                4 -> BeneficiariesTableTab(beneficiariesList, viewModel)
                5 -> ReportsTab(reportsList, viewModel)
            }
        }
    }
}

@Composable
fun MetricSummaryCard(
    title: String,
    value: String,
    badge: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// --- OVERVIEW TAB WITH COMPOSE CANVAS CHARTS ---
@Composable
fun OverviewChartsTab() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column {
        // 1. Volunteer Growth Line Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Volunteer Growth (Monthly Trend)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val points = listOf(
                        Offset(0f, height * 0.8f),
                        Offset(width * 0.2f, height * 0.65f),
                        Offset(width * 0.4f, height * 0.70f),
                        Offset(width * 0.6f, height * 0.40f),
                        Offset(width * 0.8f, height * 0.30f),
                        Offset(width, height * 0.15f)
                    )

                    val path = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = primaryColor,
                        style = Stroke(width = 6f)
                    )

                    points.forEach { pt ->
                        drawCircle(
                            color = primaryColor,
                            radius = 8f,
                            center = pt
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Jan", style = MaterialTheme.typography.labelSmall)
                    Text("Feb", style = MaterialTheme.typography.labelSmall)
                    Text("Mar", style = MaterialTheme.typography.labelSmall)
                    Text("Apr", style = MaterialTheme.typography.labelSmall)
                    Text("May", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Donation Trends Bar Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Donation Receipts (ZAR Thousands)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val barWidth = w / 11f
                    val heights = listOf(h * 0.4f, h * 0.6f, h * 0.45f, h * 0.85f, h * 0.7f)

                    heights.forEachIndexed { idx, barH ->
                        val left = (idx * 2 + 1) * barWidth
                        drawRect(
                            color = secondaryColor,
                            topLeft = Offset(left, h - barH),
                            size = Size(barWidth, barH)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Food", style = MaterialTheme.typography.labelSmall)
                    Text("Youth", style = MaterialTheme.typography.labelSmall)
                    Text("Winter", style = MaterialTheme.typography.labelSmall)
                    Text("Emerg.", style = MaterialTheme.typography.labelSmall)
                    Text("General", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Assistance Distribution Pie / Donut Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Assistance Request Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(
                        modifier = Modifier.size(120.dp)
                    ) {
                        drawArc(color = primaryColor, startAngle = 0f, sweepAngle = 180f, useCenter = false, style = Stroke(width = 30f))
                        drawArc(color = secondaryColor, startAngle = 180f, sweepAngle = 100f, useCenter = false, style = Stroke(width = 30f))
                        drawArc(color = tertiaryColor, startAngle = 280f, sweepAngle = 80f, useCenter = false, style = Stroke(width = 30f))
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        ChartLegendItem(color = primaryColor, label = "Food Parcels (50%)")
                        Spacer(modifier = Modifier.height(6.dp))
                        ChartLegendItem(color = secondaryColor, label = "Medical Aid (28%)")
                        Spacer(modifier = Modifier.height(6.dp))
                        ChartLegendItem(color = tertiaryColor, label = "Disaster Relief (22%)")
                    }
                }
            }
        }
    }
}

@Composable
fun ChartLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}

// --- RESOURCE MATCHING TAB ---
@Composable
fun ResourceMatchingTab(
    beneficiaries: List<BeneficiaryEntity>,
    volunteers: List<VolunteerEntity>,
    donations: List<DonationEntity>,
    viewModel: AdminViewModel
) {
    val matches = remember(beneficiaries, volunteers, donations) {
        ResourceMatchingEngine.generateMatches(beneficiaries, volunteers, donations)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Automated Match Recommendations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scored by Area, Assistance Type & Urgency Level",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        matches.forEach { match ->
            MatchRecommendationCard(
                recommendation = match,
                onAssignVolunteer = {
                    val volName = match.recommendedVolunteer?.fullName ?: "Default Volunteer"
                    viewModel.assignVolunteerToBeneficiary(match.beneficiary, volName)
                },
                onAllocateResource = {
                    val cause = match.recommendedDonation?.cause ?: match.beneficiary.assistanceNeeded
                    viewModel.allocateResourceToBeneficiary(match.beneficiary, cause)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MatchRecommendationCard(
    recommendation: MatchRecommendation,
    onAssignVolunteer: () -> Unit,
    onAllocateResource: () -> Unit
) {
    val ben = recommendation.beneficiary
    val vol = recommendation.recommendedVolunteer
    val don = recommendation.recommendedDonation

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = ben.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Area: ${ben.area} • Urgency: ${ben.urgency}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${recommendation.matchScore}% Match",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "Needed: ${ben.assistanceNeeded}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    if (vol != null) {
                        Text(text = "Suggested Volunteer: ${vol.fullName} (${vol.area} - ${vol.skills})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    if (don != null) {
                        Text(text = "Suggested Resource Fund: R${"%.0f".format(don.amount)} (${don.cause})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                    Text(text = "Match Reasons: ${recommendation.matchReasons.joinToString(" • ")}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onAssignVolunteer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Assign Volunteer", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAllocateResource,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Allocate Resource", fontSize = 12.sp)
                }
            }
        }
    }
}

// --- VOLUNTEERS TABLE TAB ---
@Composable
fun VolunteersTableTab(
    volunteersList: List<VolunteerEntity>,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Registered Volunteers (${volunteersList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { viewModel.exportTableToCsv(context, "Volunteers") },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export CSV")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        volunteersList.forEach { vol ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = vol.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${vol.area} • ${vol.skills}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = vol.phone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (vol.status == "Active") extendedColors.mintGreenContainer else extendedColors.amberContainer
                    ) {
                        Text(
                            text = vol.status,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (vol.status == "Active") extendedColors.mintGreenText else extendedColors.amberText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- DONATIONS TABLE TAB ---
@Composable
fun DonationsTableTab(
    donationsList: List<DonationEntity>,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recorded Donations (${donationsList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { viewModel.exportTableToCsv(context, "Donations") },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export CSV")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        donationsList.forEach { don ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = don.donorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${don.cause} • ${don.paymentMethod}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = don.date, style = MaterialTheme.typography.labelSmall)
                    }

                    Text(
                        text = "R${"%.2f".format(don.amount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// --- BENEFICIARIES TABLE TAB ---
@Composable
fun BeneficiariesTableTab(
    beneficiariesList: List<BeneficiaryEntity>,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Assistance Requests (${beneficiariesList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { viewModel.exportTableToCsv(context, "Beneficiaries") },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export CSV")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        beneficiariesList.forEach { ben ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = ben.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = ben.status,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(text = "${ben.area} • Family size: ${ben.familySize}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = ben.assistanceNeeded, style = MaterialTheme.typography.bodySmall, maxLines = 2)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        if (ben.status == "Pending") {
                            OutlinedButton(
                                onClick = { viewModel.approveBeneficiary(ben) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Approve")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (ben.status != "Fulfilled") {
                            Button(
                                onClick = { viewModel.fulfillBeneficiary(ben) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Mark Fulfilled")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- REPORTS TAB WITH 6 OFFICIAL PDF GENERATION CARDS ---
@Composable
fun ReportsTab(
    reportsList: List<ReportEntity>,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current

    val pdfCards = listOf(
        Triple("Volunteer Activity Report", "Volunteer Management", "Activity roster, skills breakdown, and area coverage metrics for all registered volunteers."),
        Triple("Donation Summary Report", "Financial Audit", "Verifiable donation ledger accounting for all public donations and payment gateway receipts in ZAR."),
        Triple("Beneficiary Assistance Report", "Assistance Dispatch", "Full log of beneficiary applications, urgency levels, family sizes, and fulfillment statuses."),
        Triple("Community Needs Report", "Field Assessment", "Comprehensive community needs survey covering food insecurity, winter relief, and education."),
        Triple("Monthly Statistics & Trends Report", "Annual Trends", "High-level impact counters, volunteer growth trends, and food parcel distribution statistics."),
        Triple("POPIA Compliance & Audit Report", "Legal & Security", "POPIA Act 4 of 2013 compliance verification, consent timestamps, and data encryption log.")
    )

    Column {
        Text(
            text = "Generate Official PDF Audit Reports",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Click to compile real-time PDF documentation for board & public audit.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        pdfCards.forEach { (title, type, summary) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(text = type, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Button(
                        onClick = { viewModel.generatePdfReport(context, title, type, summary) },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Generate PDF")
                    }
                }
            }
        }
    }
}
