package com.example.aurorah.ui.donate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurorah.util.PaymentGatewayManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(
    viewModel: DonateViewModel,
    onDonationComplete: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var causeExpanded by remember { mutableStateOf(false) }

    val causes = listOf(
        "Emergency Food Relief Fund",
        "Hanover Park Kitchen Drive",
        "Youth After-School Education",
        "Winter Blankets & Shelters",
        "General Mutual Aid Operation"
    )

    val paymentMethods = listOf(
        "Credit / Debit Card",
        "EFT Direct Bank Transfer",
        "SnapScan / Zapper QR"
    )

    // Receipt Modal Dialog
    uiState.receiptData?.let { receipt ->
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissReceipt()
                onDonationComplete()
            },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Tax Receipt & Confirmation",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        text = "Thank you for your generous donation to Aurorah CAN!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Transaction ID: #AUR-DON-${receipt.id}", style = MaterialTheme.typography.labelSmall)
                            Text("Donor: ${receipt.donorName}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text("Amount: R${"%.2f".format(receipt.amount)} ZAR", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                            Text("Cause: ${receipt.cause}", style = MaterialTheme.typography.bodySmall)
                            Text("Method: ${receipt.paymentMethod}", style = MaterialTheme.typography.labelSmall)
                            Text("Date: ${receipt.date}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Section 18A Tax Certificate ready for audit. Thank you for empowering Kuils River!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Row {
                    if (uiState.receiptPdfFile != null) {
                        Button(
                            onClick = {
                                viewModel.openTaxReceiptPdf(context)
                                viewModel.dismissReceipt()
                                onDonationComplete()
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download Section 18A PDF")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    OutlinedButton(
                        onClick = {
                            viewModel.dismissReceipt()
                            onDonationComplete()
                        }
                    ) {
                        Text("Close")
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Donation Portal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "100% transparent mutual aid funding",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preset Tier Cards
        Text(
            text = "Select Donation Tier (ZAR)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TierCard(
                tierLabel = "Basic",
                amountText = "R50",
                desc = "2 meals",
                isSelected = uiState.selectedTierAmount == 50.0,
                onClick = { viewModel.selectPresetTier(50.0) },
                modifier = Modifier.weight(1f)
            )

            TierCard(
                tierLabel = "Standard",
                amountText = "R200",
                desc = "1 Hamper",
                isSelected = uiState.selectedTierAmount == 200.0,
                onClick = { viewModel.selectPresetTier(200.0) },
                modifier = Modifier.weight(1f)
            )

            TierCard(
                tierLabel = "Supporter",
                amountText = "R500",
                desc = "Youth Aid",
                isSelected = uiState.selectedTierAmount == 500.0,
                onClick = { viewModel.selectPresetTier(500.0) },
                modifier = Modifier.weight(1f)
            )

            TierCard(
                tierLabel = "Champion",
                amountText = "R1000+",
                desc = "Kitchen Drive",
                isSelected = uiState.selectedTierAmount == 1000.0,
                onClick = { viewModel.selectPresetTier(1000.0) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Amount
        OutlinedTextField(
            value = uiState.customAmountText,
            onValueChange = { viewModel.onCustomAmountChange(it) },
            label = { Text("Custom Amount (ZAR)") },
            prefix = { Text("R ") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Cause Selector Dropdown
        ExposedDropdownMenuBox(
            expanded = causeExpanded,
            onExpandedChange = { causeExpanded = !causeExpanded }
        ) {
            OutlinedTextField(
                value = uiState.cause,
                onValueChange = {},
                readOnly = true,
                label = { Text("Designated Cause / Fund *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = causeExpanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = causeExpanded,
                onDismissRequest = { causeExpanded = false }
            ) {
                causes.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c) },
                        onClick = {
                            viewModel.onCauseChange(c)
                            causeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Donor Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.isAnonymous,
                        onCheckedChange = { viewModel.onAnonymousChange(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Make this an anonymous donation",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                AnimatedVisibility(visible = !uiState.isAnonymous) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = uiState.donorName,
                            onValueChange = { viewModel.onDonorNameChange(it) },
                            label = { Text("Full Name *") },
                            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.donorEmail,
                            onValueChange = { viewModel.onDonorEmailChange(it) },
                            label = { Text("Email Address (for Tax Receipt) *") },
                            leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.donorPhone,
                            onValueChange = { viewModel.onDonorPhoneChange(it) },
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.message,
                    onValueChange = { viewModel.onMessageChange(it) },
                    label = { Text("Encouragement Message (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Method Selector
                Text(
                    text = "Payment Gateway Method",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                paymentMethods.forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onPaymentMethodChange(method) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.paymentMethod == method,
                            onClick = { viewModel.onPaymentMethodChange(method) }
                        )
                        Text(
                            text = method,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Show Gateway Specific Details
                if (uiState.paymentMethod.contains("EFT", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("EFT Bank Details (Standard Bank):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            Text("Bank: ${PaymentGatewayManager.eftDetails.bankName}", style = MaterialTheme.typography.bodySmall)
                            Text("Account Name: ${PaymentGatewayManager.eftDetails.accountName}", style = MaterialTheme.typography.bodySmall)
                            Text("Account #: ${PaymentGatewayManager.eftDetails.accountNumber}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text("Branch: ${PaymentGatewayManager.eftDetails.branchCode}", style = MaterialTheme.typography.labelSmall)
                            Text("Reference: ${PaymentGatewayManager.eftDetails.referencePrefix}YOURNAME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else if (uiState.paymentMethod.contains("SnapScan", ignoreCase = true) || uiState.paymentMethod.contains("Zapper", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.QrCode2, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("SnapScan & Zapper Instant QR", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                Text("Scan directly with SnapScan or Zapper app. Instant receipt generated upon payment completion.", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // POPIA Consent
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.popiaConsented,
                        onCheckedChange = { viewModel.onPopiaConsentChange(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I consent to POPIA data processing for payment confirmation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }

                AnimatedVisibility(visible = uiState.errorMessage != null) {
                    uiState.errorMessage?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Proceed Button
                Button(
                    onClick = { viewModel.processDonation(context) },
                    enabled = !uiState.isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (uiState.isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Proceed to Secure Payment",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TierCard(
    tierLabel: String,
    amountText: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = tierLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = amountText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
