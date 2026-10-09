package com.example.aurorah.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.aurorah.AurorahApplication
import com.example.aurorah.R
import com.example.aurorah.navigation.AurorahRoute
import com.example.aurorah.ui.about.AboutScreen
import com.example.aurorah.ui.admin.AdminDashboardScreen
import com.example.aurorah.ui.admin.AdminViewModel
import com.example.aurorah.ui.assistance.AssistanceViewModel
import com.example.aurorah.ui.assistance.RequestAssistanceScreen
import com.example.aurorah.ui.auth.AuthViewModel
import com.example.aurorah.ui.auth.LoginScreen
import com.example.aurorah.ui.auth.RegisterScreen
import com.example.aurorah.ui.donate.DonateScreen
import com.example.aurorah.ui.donate.DonateViewModel
import com.example.aurorah.ui.home.HomeScreen
import com.example.aurorah.ui.home.HomeViewModel
import com.example.aurorah.ui.impact.ImpactNewsScreen
import com.example.aurorah.ui.impact.ImpactViewModel
import com.example.aurorah.ui.settings.SettingsScreen
import com.example.aurorah.ui.settings.SettingsViewModel
import com.example.aurorah.ui.volunteer.VolunteerScreen
import com.example.aurorah.ui.volunteer.VolunteerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AurorahAppShell() {
    val context = LocalContext.current
    val app = context.applicationContext as AurorahApplication
    val repository = app.repository

    // ViewModels
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
    val volunteerViewModel: VolunteerViewModel = viewModel(factory = VolunteerViewModel.Factory(repository))
    val assistanceViewModel: AssistanceViewModel = viewModel(factory = AssistanceViewModel.Factory(repository))
    val donateViewModel: DonateViewModel = viewModel(factory = DonateViewModel.Factory(repository))
    val impactViewModel: ImpactViewModel = viewModel(factory = ImpactViewModel.Factory(repository))
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(repository))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory(repository))
    val adminViewModel: AdminViewModel = viewModel(factory = AdminViewModel.Factory(repository))

    val userSession by authViewModel.userSessionState.collectAsState()

    // Navigation 3 Backstack
    val backStack = rememberNavBackStack(AurorahRoute.Home)
    val currentRoute = backStack.lastOrNull() ?: AurorahRoute.Home

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = when (currentRoute) {
                                is AurorahRoute.Home -> stringResource(R.string.app_name)
                                is AurorahRoute.About -> stringResource(R.string.nav_about)
                                is AurorahRoute.Volunteer -> stringResource(R.string.nav_volunteer)
                                is AurorahRoute.RequestAssistance -> stringResource(R.string.nav_request)
                                is AurorahRoute.Donate -> stringResource(R.string.nav_donate)
                                is AurorahRoute.ImpactNews -> stringResource(R.string.nav_impact)
                                is AurorahRoute.Login -> stringResource(R.string.nav_login)
                                is AurorahRoute.Register -> stringResource(R.string.nav_register)
                                is AurorahRoute.Settings -> stringResource(R.string.nav_settings)
                                is AurorahRoute.AdminDashboard -> stringResource(R.string.nav_admin_dashboard)
                                else -> stringResource(R.string.app_name)
                            },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    if (backStack.size > 1 && currentRoute !in listOf(AurorahRoute.Home, AurorahRoute.About, AurorahRoute.Volunteer, AurorahRoute.ImpactNews, AurorahRoute.Settings)) {
                        IconButton(onClick = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (currentRoute != AurorahRoute.Donate) {
                        Button(
                            onClick = {
                                if (currentRoute != AurorahRoute.Donate) {
                                    backStack.add(AurorahRoute.Donate)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Icon(Icons.Rounded.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Donate", fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = {
                            if (currentRoute != AurorahRoute.Settings) {
                                backStack.add(AurorahRoute.Settings)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(R.string.nav_settings),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 8.dp
            ) {
                // Home
                NavigationBarItem(
                    selected = currentRoute is AurorahRoute.Home,
                    onClick = {
                        if (currentRoute !is AurorahRoute.Home) {
                            backStack.clear()
                            backStack.add(AurorahRoute.Home)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = stringResource(R.string.nav_home)) },
                    label = { Text(stringResource(R.string.nav_home)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary)
                )

                // About
                NavigationBarItem(
                    selected = currentRoute is AurorahRoute.About,
                    onClick = {
                        if (currentRoute !is AurorahRoute.About) {
                            backStack.clear()
                            backStack.add(AurorahRoute.Home)
                            backStack.add(AurorahRoute.About)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Info, contentDescription = stringResource(R.string.nav_about)) },
                    label = { Text(stringResource(R.string.nav_about)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary)
                )

                // Volunteer / Forms
                NavigationBarItem(
                    selected = currentRoute is AurorahRoute.Volunteer || currentRoute is AurorahRoute.RequestAssistance,
                    onClick = {
                        if (currentRoute !is AurorahRoute.Volunteer) {
                            backStack.clear()
                            backStack.add(AurorahRoute.Home)
                            backStack.add(AurorahRoute.Volunteer)
                        }
                    },
                    icon = { Icon(Icons.Rounded.VolunteerActivism, contentDescription = stringResource(R.string.nav_volunteer)) },
                    label = { Text("Volunteer") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary)
                )

                // Impact & News
                NavigationBarItem(
                    selected = currentRoute is AurorahRoute.ImpactNews,
                    onClick = {
                        if (currentRoute !is AurorahRoute.ImpactNews) {
                            backStack.clear()
                            backStack.add(AurorahRoute.Home)
                            backStack.add(AurorahRoute.ImpactNews)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Assessment, contentDescription = stringResource(R.string.nav_impact)) },
                    label = { Text("Impact") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary)
                )

                // Admin / Profile
                NavigationBarItem(
                    selected = currentRoute is AurorahRoute.AdminDashboard || currentRoute is AurorahRoute.Login || currentRoute is AurorahRoute.Register,
                    onClick = {
                        if (userSession.isLoggedIn && (userSession.role.contains("Admin") || userSession.username == "latifah")) {
                            if (currentRoute !is AurorahRoute.AdminDashboard) {
                                backStack.clear()
                                backStack.add(AurorahRoute.Home)
                                backStack.add(AurorahRoute.AdminDashboard)
                            }
                        } else {
                            if (currentRoute !is AurorahRoute.Login) {
                                backStack.clear()
                                backStack.add(AurorahRoute.Home)
                                backStack.add(AurorahRoute.Login)
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (userSession.isLoggedIn) Icons.Rounded.AdminPanelSettings else Icons.Rounded.Person,
                            contentDescription = "Portal"
                        )
                    },
                    label = { Text(if (userSession.isLoggedIn) "Admin" else "Login") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                entryProvider = { key: NavKey ->
                    when (key) {
                        is AurorahRoute.Home -> NavEntry(key) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToVolunteer = { backStack.add(AurorahRoute.Volunteer) },
                                onNavigateToRequestAssistance = { backStack.add(AurorahRoute.RequestAssistance) },
                                onNavigateToDonate = { backStack.add(AurorahRoute.Donate) },
                                onNavigateToAbout = { backStack.add(AurorahRoute.About) },
                                onNavigateToImpact = { backStack.add(AurorahRoute.ImpactNews) }
                            )
                        }

                        is AurorahRoute.About -> NavEntry(key) {
                            AboutScreen()
                        }

                        is AurorahRoute.Volunteer -> NavEntry(key) {
                            VolunteerScreen(
                                viewModel = volunteerViewModel,
                                onRegistrationSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
                            )
                        }

                        is AurorahRoute.RequestAssistance -> NavEntry(key) {
                            RequestAssistanceScreen(
                                viewModel = assistanceViewModel,
                                onRequestSubmitted = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
                            )
                        }

                        is AurorahRoute.Donate -> NavEntry(key) {
                            DonateScreen(
                                viewModel = donateViewModel,
                                onDonationComplete = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
                            )
                        }

                        is AurorahRoute.ImpactNews -> NavEntry(key) {
                            ImpactNewsScreen(
                                viewModel = impactViewModel
                            )
                        }

                        is AurorahRoute.Settings -> NavEntry(key) {
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                onNavigateToLogin = { backStack.add(AurorahRoute.Login) }
                            )
                        }

                        is AurorahRoute.Login -> NavEntry(key) {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                                onNavigateToRegister = { backStack.add(AurorahRoute.Register) },
                                onLoginSuccess = {
                                    backStack.clear()
                                    backStack.add(AurorahRoute.Home)
                                    backStack.add(AurorahRoute.AdminDashboard)
                                }
                            )
                        }

                        is AurorahRoute.Register -> NavEntry(key) {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onNavigateBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                                onNavigateToLogin = {
                                    if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                                    backStack.add(AurorahRoute.Login)
                                },
                                onRegisterSuccess = {
                                    backStack.clear()
                                    backStack.add(AurorahRoute.Home)
                                }
                            )
                        }

                        is AurorahRoute.AdminDashboard -> NavEntry(key) {
                            AdminDashboardScreen(
                                viewModel = adminViewModel,
                                onNavigateToLogin = { backStack.add(AurorahRoute.Login) }
                            )
                        }

                        else -> NavEntry(key) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToVolunteer = { backStack.add(AurorahRoute.Volunteer) },
                                onNavigateToRequestAssistance = { backStack.add(AurorahRoute.RequestAssistance) },
                                onNavigateToDonate = { backStack.add(AurorahRoute.Donate) },
                                onNavigateToAbout = { backStack.add(AurorahRoute.About) },
                                onNavigateToImpact = { backStack.add(AurorahRoute.ImpactNews) }
                            )
                        }
                    }
                }
            )
        }
    }
}
