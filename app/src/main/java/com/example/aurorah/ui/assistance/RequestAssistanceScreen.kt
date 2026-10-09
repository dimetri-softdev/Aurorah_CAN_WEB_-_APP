package com.example.aurorah.ui.assistance

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.PriorityHigh
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestAssistanceScreen(
    viewModel: AssistanceViewModel,
    onRequestSubmitted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var areaExpanded by remember { mutableStateOf(false) }
    var assistanceTypeExpanded by remember { mutableStateOf(false) }
    var urgencyExpanded by remember { mutableStateOf(false) }

    val areas = listOf(
        "Kuils River", "Eerste River", "Blue Downs", "Mfuleni",
        "Blackheath", "Mitchells Plain", "Khayelitsha", "Gugulethu",
        "Woodstock", "Bellville", "Cape Town Central"
    )

    val assistanceTypes = listOf(
        "Food Parcels & Hampers",
        "Medical & Chronic Care",
        "Disaster & Emergency Relief",
        "Winter Warmth & Blankets",
        "Youth & Educational Support",
        "Utility & Financial Aid"
    )

    val urgencyLevels = listOf("Normal", "Urgent", "Emergency")

    // Success Modal
    uiState.successMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissSuccessMessage()
                onRequestSubmitted()
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
                    text = "Request Received",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissSuccessMessage()
                        onRequestSubmitted()
                    }
                ) {
                    Text("OK")
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
                imageVector = Icons.Rounded.Handshake,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Request Assistance",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Confidential mutual support for local families in need",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // First Name & Last Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.firstName,
                        onValueChange = { viewModel.onFirstNameChange(it) },
                        label = { Text("First Name *") },
                        leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    OutlinedTextField(
                        value = uiState.lastName,
                        onValueChange = { viewModel.onLastNameChange(it) },
                        label = { Text("Last Name *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Email & Phone
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = uiState.phone,
                    onValueChange = { viewModel.onPhoneChange(it) },
                    label = { Text("Phone Number (+27 format) *") },
                    leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Area Dropdown
                ExposedDropdownMenuBox(
                    expanded = areaExpanded,
                    onExpandedChange = { areaExpanded = !areaExpanded }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedArea,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Location / Area *") },
                        leadingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = areaExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = areaExpanded,
                        onDismissRequest = { areaExpanded = false }
                    ) {
                        areas.forEach { area ->
                            DropdownMenuItem(
                                text = { Text(area) },
                                onClick = {
                                    viewModel.onAreaChange(area)
                                    areaExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Assistance Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = assistanceTypeExpanded,
                    onExpandedChange = { assistanceTypeExpanded = !assistanceTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedAssistanceType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type of Assistance Needed *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assistanceTypeExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = assistanceTypeExpanded,
                        onDismissRequest = { assistanceTypeExpanded = false }
                    ) {
                        assistanceTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    viewModel.onAssistanceTypeChange(type)
                                    assistanceTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Family Size
                OutlinedTextField(
                    value = uiState.familySize,
                    onValueChange = { viewModel.onFamilySizeChange(it) },
                    label = { Text("Family Size") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Urgency Level Dropdown
                ExposedDropdownMenuBox(
                    expanded = urgencyExpanded,
                    onExpandedChange = { urgencyExpanded = !urgencyExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.urgencyLevel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Urgency Level *") },
                        leadingIcon = { Icon(Icons.Rounded.PriorityHigh, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = urgencyExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = urgencyExpanded,
                        onDismissRequest = { urgencyExpanded = false }
                    ) {
                        urgencyLevels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level) },
                                onClick = {
                                    viewModel.onUrgencyChange(level)
                                    urgencyExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Describe Need
                OutlinedTextField(
                    value = uiState.describeNeed,
                    onValueChange = { viewModel.onDescribeNeedChange(it) },
                    label = { Text("Describe Your Need / Situation *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5
                )

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
                        text = "I consent to Aurorah CAN processing this information securely to provide assistance.",
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

                // Submit Button
                Button(
                    onClick = { viewModel.submitRequest() },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Submit Assistance Application",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
