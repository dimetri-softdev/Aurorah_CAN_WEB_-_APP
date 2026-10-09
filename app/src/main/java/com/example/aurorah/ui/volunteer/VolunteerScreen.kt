package com.example.aurorah.ui.volunteer

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
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.VolunteerActivism
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
import androidx.compose.material3.TextButton
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
fun VolunteerScreen(
    viewModel: VolunteerViewModel,
    onRegistrationSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var areaExpanded by remember { mutableStateOf(false) }
    var availabilityExpanded by remember { mutableStateOf(false) }

    val areas = listOf(
        "Kuils River", "Eerste River", "Blue Downs", "Mfuleni",
        "Blackheath", "Mitchells Plain", "Khayelitsha", "Gugulethu",
        "Woodstock", "Bellville", "Cape Town Central"
    )

    val availabilityOptions = listOf(
        "Weekends & Evenings", "Weekdays Only", "Flexible On-Call",
        "Full-Time Volunteer", "Emergency Rapid Response"
    )

    // Success Modal Dialog
    uiState.successMessage?.let { successMsg ->
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissSuccessMessage()
                onRegistrationSuccess()
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
                    text = "Application Submitted!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = successMsg,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissSuccessMessage()
                        onRegistrationSuccess()
                    }
                ) {
                    Text("Continue")
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
                imageVector = Icons.Rounded.VolunteerActivism,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Volunteer Registration",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Join Kuils River's active mutual aid network",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Card
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

                // Email Address
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Email Address *") },
                    leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Phone Number (+27 format)
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

                // Location / Area Dropdown
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

                // Skills & Experience
                OutlinedTextField(
                    value = uiState.skills,
                    onValueChange = { viewModel.onSkillsChange(it) },
                    label = { Text("Skills & Experience (e.g. Cooking, Driving, Tutoring)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Availability Dropdown
                ExposedDropdownMenuBox(
                    expanded = availabilityExpanded,
                    onExpandedChange = { availabilityExpanded = !availabilityExpanded }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedAvailability,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Availability *") },
                        leadingIcon = { Icon(Icons.Rounded.Schedule, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = availabilityExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = availabilityExpanded,
                        onDismissRequest = { availabilityExpanded = false }
                    ) {
                        availabilityOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    viewModel.onAvailabilityChange(opt)
                                    availabilityExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Community Experience Text Field
                OutlinedTextField(
                    value = uiState.communityExperience,
                    onValueChange = { viewModel.onCommunityExperienceChange(it) },
                    label = { Text("Previous Community Work / Experience (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                // POPIA Consent Checkbox
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
                        text = "I consent to Aurorah CAN storing my contact details under POPIA for volunteer coordination.",
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
                    onClick = { viewModel.submitRegistration() },
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
                            text = "Submit Volunteer Application",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
