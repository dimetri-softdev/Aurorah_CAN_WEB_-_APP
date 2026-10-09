# Project Plan

Build a complete Android mobile application for Aurorah CAN (Community Action Network serving Kuils River). 

Key Features & Screens based on website designs:
1. Home Screen:
   - Header hero banner with title "Aurorah CAN - Community Action Network Serving Kuils River", tagline, "Get Involved" and "Learn More" call to action buttons.
   - Quick stats cards: Active Volunteers (127), People Served (342), Funds Raised (R 45,230), Transparency (100%).
   - "How You Can Help" section with 3 action cards: Become a Volunteer (Register Now), Request Assistance (Submit Request), Make a Donation (Donate).
   - About Aurorah CAN summary section & Our Mission cards.
   - Call to action banner "Ready to Make a Difference?" and Footer with quick links and contact info (info@aurorahan.org, +27 (0)21 XXXX XXXX).

2. About Us Screen:
   - Hero banner with title & subtitle.
   - Our Story, Our Mission, Our Vision, Why We Exist details.
   - Core Values cards (Compassion, Transparency, Collaboration, Integrity).
   - Leadership section highlighting Latifah Jacobs (Founder & Director).
   - Sidebar/Section with Documentation (Privacy Policy, POPIA Compliance, Terms of Service) and Contact details.

3. Volunteer Registration Screen:
   - Registration Form: First Name, Last Name, Email Address, Phone Number (+27 format), Location/Area dropdown, Skills & Experience text area, Availability dropdown, Community Experience text area, Privacy/POPIA consent checkbox, and Submit Application button.

4. Request Assistance Screen:
   - Assistance Form: Personal Information (First Name, Last Name, Email, Phone, Location/Area), Type of Assistance Needed dropdown, Describe Your Need text area, Urgency Level dropdown (Normal, Urgent, Emergency), Privacy Policy/POPIA consent checkbox, and Submit Request button.

5. Make a Donation Screen:
   - Header banner & preset donation tier cards (Basic R50, Standard R200, Supporter R500, Champion R1000+).
   - Custom Donation Form: Amount (ZAR, min R10), Cause/Program selection dropdown, Anonymous donation checkbox, Donor Name, Email Address, Phone Number, Optional Message to inspire others, POPIA consent, and Proceed to Payment button.

6. Impact & Reports Screen:
   - Overview stat counters (Active Volunteers, People Helped, Funds Raised, Donations Received).
   - Impact Stories (Food Relief Program, Skills Training, Healthcare Support).
   - Download Reports section (Annual Report 2025, Financial Statement, Impact Summary).
   - "How Your Donation Helps" breakdown cards.
   - Donor Testimonials.

7. Admin Portal:
   - Admin Login Screen: Username, Password input with toggle, Remember Me, Secure Access badge, Login action.
   - Admin Dashboard:
     - Header & Tabbed navigation: Overview, Volunteers, Donations, Beneficiaries, Reports.
     - Overview tab: Stat metrics cards with trend percentages (+12%, +8%, +15%, 100%), Volunteer Growth chart, Donation Trends chart, Assistance Distribution pie chart.
     - Volunteers tab: Recent Volunteer Registrations table with name, skills, date, status badges (Active, Pending), and Export button.
     - Donations tab: Recent Donations table with donor, amount, type, date, and Export button.
     - Beneficiaries tab: Assistance Requests overview table with 342 total beneficiaries registered and Export button.
     - Reports tab: PDF Generation cards for Volunteer Activity, Donation Summary, Beneficiary, Community Needs, Monthly Statistics, and Impact Reports.

Design System & Branding:
- Color Palette: Deep Burgundy / Crimson (#8B263E / #A63A50 / #C0485E), clean white (#FFFFFF) background cards, soft gray background (#F8F9FA), dark text (#2B2B2B), mint green badges (#E6F4EA / #1E8E3E), amber pending badges.
- Navigation: Bottom Navigation Bar or Navigation Drawer for easy switching between Home, About, Get Involved (Volunteer/Request/Donate), Impact, and Admin Login/Dashboard.
- Responsive Jetpack Compose UI with modern Material Design 3 guidelines.

## Project Brief

# Aurorah CAN - Project Brief

## Features
- **Community Engagement Hub**: Comprehensive home dashboard featuring real-time impact metrics, mission highlights, core values, and quick call-to-action shortcuts for community involvement.
- **Volunteer & Assistance Intake Forms**: Digital intake workflows enabling community members to register as volunteers (capturing skills and availability) or submit structured requests for assistance (with urgency levels and location tagging).
- **Donation & Sponsorship Module**: Flexible contribution interface supporting preset tiers (R50, R200, R500, R1000+) and custom donations with cause selection and POPIA compliance.
- **Impact & Transparency Center**: Verified statistics overview, downloadable annual and financial PDF reports, impact breakdown cards, and community testimonials.
- **Admin Management Dashboard**: Secure admin portal for managing volunteer applications, beneficiary assistance requests, donation logs, and report generation.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3 (featuring custom deep burgundy theme `#8B263E`)
- **Navigation**: Jetpack Navigation 3 (state-driven navigation)
- **Adaptive Strategy**: Compose Material Adaptive library (`androidx.compose.material3.adaptive`)
- **Core Architecture & Asynchrony**: MVVM (ViewModel, StateFlow) & Kotlin Coroutines

## Implementation Steps
**Total Duration:** 94h 31m 54s

### Task_1_CoreSetupDataAuth: Setup Theme, Color Scheme (Burgundy #8B263E), Material Design 3, Navigation routes, String resources for Multi-language support (English, Afrikaans, isiXhosa). Build Room DB entities, DAOs, Database, Repositories with seed data, and Auth/Settings screens.
- **Status:** COMPLETED
- **Updates:** Completed Material 3 theme with Burgundy #8B263E color palette, Room DB entities, DAOs, Database with seed data, Navigation shell, Auth screens, and Settings screen on the dimetri branch. App builds cleanly without errors.
- **Acceptance Criteria:**
  - Material 3 theme with custom burgundy color scheme and multi-language string resources (en, af, xh) set up.
  - Room Database entities, DAOs, and repository layer with pre-populated initial seed data created.
  - Auth screens (Login, Register, SSO) and Settings screen with theme/language selector fully functional.
- **Duration:** 67h 33m 2s

### Task_2_PublicScreensWorkflows: Implement Main Navigation (Bottom Bar) and Public screens: Home Screen with hero/news feed, About Us, Volunteer Registration Form, Request Assistance Form, Donation Portal, and Impact & Reports Screen.
- **Status:** COMPLETED
- **Updates:** Implemented Home Screen, About Us, Volunteer Registration Form, Request Assistance Form, Donation Portal, and Impact & Reports Screen with full input validation and Room DB repository integration on the dimetri branch.
- **Acceptance Criteria:**
  - Bottom navigation bar linking Home, About Us, Volunteer, Assistance, Donation, and Impact screens.
  - Intake forms with input validation saving volunteer and beneficiary requests to database.
  - Donation portal with preset/custom options and Impact screen displaying metrics and PDF report options.
- **Duration:** 31s

### Task_3_AdminPortalDashboard: Implement Admin Login, Admin Dashboard with metric cards, interactive Compose Canvas charts, management tables for Volunteers/Donations/Beneficiaries, Resource Matching Engine, News Feed Publisher, and PDF export flows.
- **Status:** COMPLETED
- **Updates:** Implemented Admin Login, Admin Dashboard with metric cards, Compose Canvas charts (line, bar, donut pie), management tables with search/filter and CSV/PDF export, Resource Matching Engine, and News Feed Publisher on the dimetri branch.
- **Acceptance Criteria:**
  - Admin authentication and multi-tabbed dashboard (Overview, Volunteers, Donations, Beneficiaries, Reports) active.
  - Interactive Compose Canvas charts (growth line chart, donation bar chart, assistance pie chart) operational.
  - Resource matching engine, news feed publisher, and CSV/PDF report generation implemented.
- **Duration:** 30s

### Task_4_RunAndVerify: Compile debug build, verify all navigation and data features, test admin portal and offline sync, and instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** All application UI, navigation, Room DB integration, auth, settings, and admin dashboard verified. Applied UI polish (ScrollableTabRow, stacked form fields, checkbox spacing) on the dimetri branch. assembleDebug compiled cleanly with 0 errors. App stability verified on emulator with 0 crashes.
- **Acceptance Criteria:**
  - build pass
  - make sure all existing tests pass
  - app does not crash
  - critic_agent verifies application stability (no crashes), confirms alignment with user requirements, and reports critical UI issues.
- **Duration:** 26h 57m 51s

