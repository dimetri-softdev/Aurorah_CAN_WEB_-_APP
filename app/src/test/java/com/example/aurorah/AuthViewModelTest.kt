package com.example.aurorah

import com.example.aurorah.data.local.entity.AdminEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import com.example.aurorah.data.preferences.UserSession
import com.example.aurorah.data.repository.AurorahRepository
import com.example.aurorah.ui.auth.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onEmailOrUsernameChange_updatesUiStateAndClearsErrorMessage() {
        val fakeRepo = FakeAurorahRepository()
        val viewModel = AuthViewModel(fakeRepo)

        viewModel.onEmailOrUsernameChange("latifah@aurorahcan.org")
        assertEquals("latifah@aurorahcan.org", viewModel.uiState.value.emailOrUsername)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun onPasswordChange_updatesUiState() {
        val fakeRepo = FakeAurorahRepository()
        val viewModel = AuthViewModel(fakeRepo)

        viewModel.onPasswordChange("myPassword123")
        assertEquals("myPassword123", viewModel.uiState.value.password)
    }

    @Test
    fun onRoleChange_updatesSelectedRole() {
        val fakeRepo = FakeAurorahRepository()
        val viewModel = AuthViewModel(fakeRepo)

        viewModel.onRoleChange("Volunteer")
        assertEquals("Volunteer", viewModel.uiState.value.selectedRole)

        viewModel.onRoleChange("Community Administrator")
        assertEquals("Community Administrator", viewModel.uiState.value.selectedRole)
    }

    @Test
    fun login_withEmptyCredentials_setsErrorMessage() = runTest {
        val fakeRepo = FakeAurorahRepository()
        val viewModel = AuthViewModel(fakeRepo)

        viewModel.login()

        assertEquals("Please enter username/email and password.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun register_withMismatchingPasswords_setsErrorMessage() = runTest {
        val fakeRepo = FakeAurorahRepository()
        val viewModel = AuthViewModel(fakeRepo)

        viewModel.onFullNameChange("Amina Adams")
        viewModel.onRegEmailChange("amina@gmail.com")
        viewModel.onRegPasswordChange("password123")
        viewModel.onRegConfirmPasswordChange("different456")

        viewModel.register()

        assertEquals("Passwords do not match.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun register_withEmptyFields_setsErrorMessage() = runTest {
        val fakeRepo = FakeAurorahRepository()
        val viewModel = AuthViewModel(fakeRepo)

        viewModel.register()

        assertEquals("Please fill in all required fields.", viewModel.uiState.value.errorMessage)
    }

    private class FakeAurorahRepository : AurorahRepository(
        database = null,
        userPreferences = null
    ) {
        private val _sessionFlow = MutableStateFlow(UserSession())
        override val userSessionFlow: StateFlow<UserSession> = _sessionFlow.asStateFlow()

        override suspend fun getAdminByEmailOrUsername(emailOrUsername: String): AdminEntity? {
            return if (emailOrUsername == "latifah" || emailOrUsername == "latifah@aurorahcan.org") {
                AdminEntity(
                    id = 1,
                    username = "latifah",
                    passwordHash = "admin123",
                    role = "Super Admin",
                    fullName = "Latifah Jacobs",
                    email = "latifah@aurorahcan.org",
                    lastLogin = "2025-05-20"
                )
            } else null
        }

        override suspend fun getAdminByUsername(username: String): AdminEntity? {
            return getAdminByEmailOrUsername(username)
        }

        override suspend fun insertVolunteer(volunteer: VolunteerEntity): Long = 1L
        override suspend fun insertAdmin(admin: AdminEntity): Long = 1L

        override suspend fun setUserSession(session: UserSession) {
            _sessionFlow.value = session
        }

        override suspend fun clearUserSession() {
            _sessionFlow.value = UserSession()
        }
    }
}
