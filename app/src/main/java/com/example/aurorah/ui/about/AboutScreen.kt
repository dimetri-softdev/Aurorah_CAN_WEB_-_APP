package com.example.aurorah.ui.about

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FaqItemData(
    val id: Int,
    val title: String,
    val summary: String,
    val details: String,
    val icon: ImageVector
)

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // --- HERO SECTION ---
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
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "About Aurorah CAN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Rooted in Community • Driven by Mutual Aid",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Aurorah CAN (Community Action Network) is a grassroots non-profit initiative serving Kuils River and surrounding Cape Town townships through dignified assistance and community action.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- OUR STORY, MISSION, VISION ---
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionTitle(title = "Our Story")
            Text(
                text = "Aurorah CAN was born out of local necessity in Kuils River during times of heightened socio-economic strain. Recognizing that traditional charity often fails to deliver localized, immediate aid, community leaders united to establish a transparent, rapid-response mutual aid network.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    title = "Our Mission",
                    description = "To mobilize local volunteers, funds, and emergency resources to eliminate hunger and support vulnerable families.",
                    icon = Icons.Rounded.Handshake,
                    modifier = Modifier.weight(1f)
                )

                InfoCard(
                    title = "Our Vision",
                    description = "A self-sustaining Kuils River metro where every household has access to food security, dignity, and opportunities.",
                    icon = Icons.Rounded.Star,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- EMBEDDED USER GUIDE & FAQ SYSTEM ---
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SectionTitle(title = "User Guide & FAQ System")
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "6 Guides Available",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tap on any topic below for detailed instructions on using the Aurorah CAN Digital Hub.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            val faqList = remember {
                listOf(
                    FaqItemData(
                        id = 1,
                        title = "1. How to Register & Manage Account",
                        summary = "Sign up as a Volunteer, Beneficiary, or Admin with POPIA consent.",
                        details = "• Navigate to the Login screen and tap 'Register Here'.\n• Select your role: Volunteer, Community Member, Beneficiary, or Admin.\n• Fill in your full name, email address, phone number, and password.\n• Review and accept the POPIA Data Privacy agreement.\n• Your session will automatically authenticate upon registration.",
                        icon = Icons.Rounded.PersonAdd
                    ),
                    FaqItemData(
                        id = 2,
                        title = "2. How to Submit Assistance Requests",
                        summary = "Request food hampers, medical supplies, electricity vouchers, or shelter.",
                        details = "• Tap the 'Request Assistance' tab on the bottom bar.\n• Choose your category: Food Parcels, Medical Care, Electricity Vouchers, or Housing/Shelter.\n• Specify your family size, address, and urgency level (Normal, High, Urgent, or Emergency).\n• Submit your request to trigger our 3-Tier Resource Matching Engine, which automatically pairs your request with local volunteers.",
                        icon = Icons.Rounded.Handshake
                    ),
                    FaqItemData(
                        id = 3,
                        title = "3. How to Make Donations & Support Causes",
                        summary = "Contribute via EFT, SnapScan, or Credit Card for high-impact causes.",
                        details = "• Open the 'Donate' screen from the bottom navigation bar.\n• Choose an earmarked cause (e.g. Hanover Park Kitchen Drive, Winter Food Security).\n• Select or enter your donation amount in South African Rand (ZAR).\n• Choose your preferred payment method (EFT, SnapScan, Credit Card).\n• Toggle 'Make Anonymous' if you prefer to keep your identity confidential.",
                        icon = Icons.Rounded.Payments
                    ),
                    FaqItemData(
                        id = 4,
                        title = "4. How to Download Tax Receipts & Audit Reports",
                        summary = "Generate Section 18A PDF tax receipts and view public audit balance sheets.",
                        details = "• Upon completing a donation, tap 'Download Tax Receipt (PDF)' to generate your official Section 18A tax deduction document.\n• Navigate to the 'Impact & Reports' screen to view completed financial audits.\n• Tap any listed report to generate and save a PDF or CSV format report directly to your device storage.",
                        icon = Icons.Rounded.ReceiptLong
                    ),
                    FaqItemData(
                        id = 5,
                        title = "5. How to Switch App Languages",
                        summary = "Localized support for English, Afrikaans, and isiXhosa.",
                        details = "• Open the 'Settings' tab from the bottom navigation bar.\n• Tap on 'Language Preference'.\n• Select your preferred language: English, Afrikaans, or isiXhosa.\n• The entire application interface will dynamically rebind string resources to your selected language.",
                        icon = Icons.Rounded.Language
                    ),
                    FaqItemData(
                        id = 6,
                        title = "6. How to View & Export Impact Reports",
                        summary = "Access live community metrics and export operational data.",
                        details = "• Go to the 'Impact & Reports' screen.\n• View real-time counters for families fed, active volunteers, and total funds deployed.\n• Filter reports by category (Impact Assessment, Financial Audit, Operations Log).\n• Tap 'Export CSV' or 'Generate PDF' to download full reports for local record-keeping.",
                        icon = Icons.Rounded.Description
                    )
                )
            }

            faqList.forEach { faq ->
                FaqExpandableCard(faqItem = faq)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- CORE VALUES ---
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionTitle(title = "Core Values")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ValueCard(
                    title = "Compassion",
                    desc = "Serving every neighbor with empathy & dignity",
                    icon = Icons.Rounded.Favorite,
                    modifier = Modifier.weight(1f)
                )
                ValueCard(
                    title = "Transparency",
                    desc = "100% open public accounting & audit reports",
                    icon = Icons.Rounded.Shield,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ValueCard(
                    title = "Collaboration",
                    desc = "Uniting civic groups & community hubs",
                    icon = Icons.Rounded.Group,
                    modifier = Modifier.weight(1f)
                )
                ValueCard(
                    title = "Integrity",
                    desc = "POPIA compliance & ethical governance",
                    icon = Icons.Rounded.Security,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- LEADERSHIP SECTION ---
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionTitle(title = "Leadership")

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Latifah Jacobs",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Founder & Executive Director",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Kuils River Community Leader",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "\"Empowering local communities starts with listening to their hearts and acting with purpose. Our network belongs to every volunteer and family we serve.\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- DOCUMENTATION & COMPLIANCE LINKS ---
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionTitle(title = "Legal & Governance")

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DocLinkItem(title = "Privacy Policy & Data Security", icon = Icons.Rounded.Security)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    DocLinkItem(title = "POPIA Compliance Statement", icon = Icons.Rounded.Gavel)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    DocLinkItem(title = "Terms of Service & NPO Charter", icon = Icons.AutoMirrored.Rounded.MenuBook)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- CONTACT DETAILS ---
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionTitle(title = "Contact & Location")

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    ContactRow(icon = Icons.Rounded.LocationOn, text = "Main Hub: Kuils River, Cape Town, 7580, South Africa")
                    Spacer(modifier = Modifier.height(10.dp))
                    ContactRow(icon = Icons.Rounded.Email, text = "info@aurorahcan.org / latifah@aurorahcan.org")
                    Spacer(modifier = Modifier.height(10.dp))
                    ContactRow(icon = Icons.Rounded.Call, text = "+27 21 903 0000 (Helpline available 08:00 - 18:00)")
                }
            }
        }
    }
}

@Composable
fun FaqExpandableCard(faqItem: FaqItemData) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (expanded)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (expanded) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (expanded) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = faqItem.icon,
                            contentDescription = null,
                            tint = if (expanded) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = faqItem.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!expanded) {
                            Text(
                                text = faqItem.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = faqItem.details,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun InfoCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ValueCard(
    title: String,
    desc: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DocLinkItem(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "View",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ContactRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
