package com.example.aurorah.ui.impact

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurorah.data.local.entity.ReportEntity

@Composable
fun ImpactNewsScreen(
    viewModel: ImpactViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val reportsList by viewModel.reportsList.collectAsState()

    // Default 6 report PDF items if database is empty or needs full list
    val allReports = if (reportsList.isNotEmpty()) reportsList else listOf(
        ReportEntity(1, "Annual Impact Report 2024", "Impact Assessment", "2025-04-01", "Completed", "PDF", "Detailed breakdown of 5,200 beneficiaries served with food, medical aid, and shelter across Cape Metro."),
        ReportEntity(2, "Financial Audit Statement Q3", "Financial Audit", "2025-04-15", "Completed", "PDF", "Verifiable transparent ledger accounting for R350,000 public donations and grant allocations."),
        ReportEntity(3, "Kuils River Community Needs Assessment", "Community Needs", "2025-03-20", "Completed", "PDF", "Comprehensive field survey on household poverty, food insecurity, and youth education in Kuils River."),
        ReportEntity(4, "POPIA Privacy & Data Security Audit", "Compliance", "2025-02-10", "Completed", "PDF", "Independent auditor verification confirming 100% compliance with South African POPIA regulation."),
        ReportEntity(5, "Hanover Park Kitchen Operations Summary", "Operations", "2025-05-01", "Completed", "PDF", "Operational report on 200 daily warm meals distributed to children and seniors in Hanover Park."),
        ReportEntity(6, "Youth Education & Mentorship Evaluation", "Program Review", "2025-01-28", "Completed", "PDF", "Assessment of after-school literacy drives and tutoring hubs established in local libraries.")
    )

    // Report Summary Modal
    uiState.selectedReport?.let { report ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissReportModal() },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.PictureAsPdf,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(text = report.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Column {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${report.type} • ${report.exportFormat}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = report.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Published Date: ${report.generatedDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.triggerReportDownload(report.title)
                        viewModel.dismissReportModal()
                    }
                ) {
                    Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download PDF Document")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissReportModal() }) {
                    Text("Close")
                }
            }
        )
    }

    // Download Notification Modal
    uiState.downloadNotificationMessage?.let { notification ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDownloadMessage() },
            icon = {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            },
            title = { Text("PDF Download Initiated", fontWeight = FontWeight.Bold) },
            text = { Text(notification) },
            confirmButton = {
                Button(onClick = { viewModel.dismissDownloadMessage() }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Assessment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Impact & Audit Reports",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Verifiable transparency, public financial audits, and real community results.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stat Overview Counters
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Key Metrics Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ImpactMetricCard(number = "5,240+", label = "Total Beneficiaries", icon = Icons.Rounded.Group, modifier = Modifier.weight(1f))
                ImpactMetricCard(number = "18", label = "Active Food Drives", icon = Icons.Rounded.Favorite, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ImpactMetricCard(number = "R350,000", label = "Funds Distributed", icon = Icons.AutoMirrored.Rounded.MenuBook, modifier = Modifier.weight(1f))
                ImpactMetricCard(number = "100%", label = "POPIA Audit Rating", icon = Icons.Rounded.Shield, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Download/View Report Cards (6 PDF Generation Cards)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Official PDF Reports & Financial Statements",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Public documents available for donor verification and community audit.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            allReports.forEach { report ->
                ReportCardItem(
                    report = report,
                    onViewSummary = { viewModel.selectReport(report) },
                    onDownloadPdf = { viewModel.triggerReportDownload(report.title) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Testimonials & Stories Section
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Community Voices & Testimonials",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            TestimonialCard(
                quote = "During the winter surge, Aurorah CAN brought food hampers directly to our doorstep in Kuils River. They treat everyone with dignity.",
                author = "Fatima P., Local Beneficiary"
            )

            Spacer(modifier = Modifier.height(10.dp))

            TestimonialCard(
                quote = "Volunteering with Aurorah CAN restored my faith in community action. Every donated Rand is accounted for and delivered to those in need.",
                author = "Thabo M., Community Volunteer"
            )
        }
    }
}

@Composable
fun ImpactMetricCard(
    number: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = number, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ReportCardItem(
    report: ReportEntity,
    onViewSummary: () -> Unit,
    onDownloadPdf: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.PictureAsPdf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = report.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${report.type} • ${report.generatedDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = report.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onViewSummary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("View Summary")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onDownloadPdf,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download PDF")
                }
            }
        }
    }
}

@Composable
fun TestimonialCard(
    quote: String,
    author: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Rounded.FormatQuote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = quote,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "— $author",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
