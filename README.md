# Aurorah CAN Digital Hub — Android Application 🌟

[![Android CI/CD Pipeline](https://github.com/dimetri/aurorah/actions/workflows/android.yml/badge.svg)](https.github.com)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-M3%20Expressive-blue.svg)
![Room Database](https://img.shields.io/badge/Room%20DB-v2.6.1-green.svg)
![Azure SQL](https://img.shields.io/badge/Cloud-Azure%20SQL%20DB-blue.svg)
![POPIA Compliant](https://img.shields.io/badge/Security-POPIA%20%26%20SHA--256-orange.svg)

---

## 📖 1. Application Overview & Vision

**Aurorah CAN (Community Action Network)** is a grassroots non-profit mutual aid network serving Kuils River and surrounding Cape Town townships (Khayelitsha, Mitchells Plain, Gugulethu, Hanover Park, Langa, and Delft).

The **Aurorah CAN Digital Hub** is a modern, full-featured native Android application designed to bridge the gap between community members needing urgent assistance, volunteers eager to offer skills and time, and donors contributing resources. By digitizing community outreach with real-time resource matching, offline synchronization, transparent audit logging, and Section 18A tax receipt generation, Aurorah CAN transforms mutual aid into an empowered, efficient, and accountable digital ecosystem.

### Key Objectives:
- **Dignified Assistance**: Enable community members to request food hampers, electricity vouchers, medical supplies, and emergency shelter in a simple, confidential manner.
- **Intelligent Resource Matching**: Automatically pair beneficiary requests with active volunteers based on area proximity, required skills, and urgency score.
- **Financial & Operations Transparency**: Provide public audit reports, downloadable CSV/PDF impact summaries, and live donation goal tracking.
- **Data Protection & POPIA Compliance**: Enforce strict South African POPIA standards including explicit consent logging, password hashing with salt, and "Right to be Forgotten" anonymization.

---

## 🚀 2. Key Features & Screens

| Screen / Feature | Description | Key Capabilities |
| :--- | :--- | :--- |
| **Home Screen** | Central hub & impact overview | Live donation ticker, urgent community needs feed, latest news alerts, fast action buttons. |
| **Request Assistance** | Beneficiary intake portal | Category selection (Food, Medical, Electricity, Housing), urgency level tagging, location tagger, POPIA consent toggle. |
| **Volunteer Hub** | Volunteer onboarding & management | Area & skill registration, availability settings, assigned task tracking, community callouts. |
| **Donate & Support** | Secure donation portal | Multi-tier contribution (EFT, SnapScan, Credit Card), cause allocation, anonymous donation option, PDF Section 18A tax receipt generation. |
| **Impact & News** | Transparency & media feed | Real-time news publisher, community stories, downloadable PDF impact reports & CSV financial audits. |
| **Admin Dashboard** | Executive management portal | Fast Admin switcher, 3-Tier resource matching engine, beneficiary request review, news publishing dialog, user role assignment. |
| **Settings & POPIA Hub** | Customization & privacy center | Light/Dark/System theme toggle, Multilingual support (English, Afrikaans, isiXhosa), POPIA data request & account anonymization/deletion. |
| **About & FAQ System** | Organizational guide | Interactive expandable accordion FAQ system, leadership overview, legal charters, direct contact helpline. |

---

## 🏗️ 3. System Architecture

Aurorah CAN utilizes a robust **3-Tier Architecture** ensuring high performance, offline resilience, and enterprise cloud synchronization.

```
+-----------------------------------------------------------------------+
|                         PRESENTATION LAYER                            |
|             Jetpack Compose Material 3 Expressive UI                  |
|       Navigation 3 • StateFlow • ViewModels • Dark/Light Theme         |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                          REPOSITORY LAYER                             |
|                         AurorahRepository                             |
|      OfflineSyncManager • SecurityUtils • ResourceMatchingEngine      |
+-----------------------------------------------------------------------+
               /                                         \
              v                                           v
+-----------------------------+           +-----------------------------+
|         LOCAL DATA          |           |         REMOTE DATA         |
|      Room SQLite DB         |           |       Azure SQL DB          |
|    7 Pre-Populated 3NF      |           |     JDBC / Retrofit REST      |
|          Entities           |           |    Cloud Sync Fallback      |
+-----------------------------+           +-----------------------------+
```

### Architectural Highlights:
1. **UI Layer**: Built 100% in **Jetpack Compose** with Material Design 3 (Expressive guidelines), dynamic color schemes, full edge-to-edge layout, and adaptive phone/tablet support.
2. **Local Persistence**: **Room Database** powering 7 relational entities with automatic database callbacks pre-populating rich, realistic seed records.
3. **Cloud & Network Sync**: **Azure SQL Database** integration via direct JDBC / REST APIs with fallback to Room cache when offline.
4. **Security & Data Vault**: `SecurityUtils` providing SHA-256 salted password hashing, UUID session tokens, and POPIA privacy controls.

---

## 🗄️ 4. Database Schema (3NF Entities & Seed Data)

The application database (`aurorah_database`, version 3) is structured in **Third Normal Form (3NF)** with 7 interconnected entities. `AppDatabase.kt` automatically pre-populates at least 10 realistic, detailed South African records for each entity upon database creation.

### Database Entities & Seed Counts:

| Entity Name | Table Name | Key Attributes | Seed Count |
| :--- | :--- | :--- | :--- |
| **AdminEntity** | `admins` | `id`, `username`, `passwordHash`, `role`, `fullName`, `email`, `lastLogin` | **10 Records** |
| **VolunteerEntity** | `volunteers` | `id`, `fullName`, `email`, `phone`, `area`, `skills`, `availability`, `address`, `status`, `registrationDate`, `popiaConsentTimestamp`, `popiaConsentVersion` | **10 Records** |
| **BeneficiaryEntity** | `beneficiaries` | `id`, `fullName`, `email`, `phone`, `address`, `area`, `familySize`, `assistanceNeeded`, `urgency`, `registrationDate`, `status`, `popiaConsentTimestamp`, `popiaConsentVersion` | **10 Records** |
| **DonationEntity** | `donations` | `id`, `donorName`, `email`, `phone`, `type`, `amount`, `cause`, `isAnonymous`, `message`, `paymentMethod`, `date`, `popiaConsentTimestamp`, `popiaConsentVersion` | **10 Records** |
| **CommunityNeedEntity** | `community_needs` | `id`, `description`, `assistanceType`, `priorityLevel`, `status`, `area`, `dateCreated`, `assignedVolunteerId` | **10 Records** |
| **ReportEntity** | `reports` | `id`, `title`, `type`, `generatedDate`, `status`, `exportFormat`, `summary` | **10 Records** |
| **NewsEntity** | `news` | `id`, `title`, `content`, `author`, `publishDate`, `category`, `urgency` | **10 Records** |

---

## 🔒 5. Security & POPIA Compliance

Aurorah CAN strictly complies with South Africa's **Protection of Personal Information Act (POPIA)** and security best practices:

1. **SHA-256 Salted Password Hashing**: Passwords are never stored in plain text. `SecurityUtils.hashPasswordWithSalt()` generates a cryptographically secure 16-byte random salt and computes a SHA-256 digest (`salt:hash`).
2. **Explicit Consent Management**: Every registration, assistance request, and donation logs `popiaConsentTimestamp` and `popiaConsentVersion` (e.g., `v1.0`).
3. **Right to be Forgotten (Anonymization & Deletion)**: Users can invoke full data anonymization (`anonymizeUserData`) which redacts personal identifiable information across all 7 tables to `'Anonymized User'` / `'anonymized@privacy.local'` or perform complete deletion (`deleteUserDataCompletely`).
4. **Session Token Security**: UUID-based session tokens (`aurorah_sess_...`) manage local login state securely via Encrypted Preferences / Datastore.

---

## 🛠️ 6. Setup & Installation Instructions

### Prerequisites:
- **Android Studio**: Ladybug / 2024.2+ or higher.
- **JDK**: Java Development Kit 17 (JDK 17).
- **Android SDK**: API Level 34 / 35 (minSdk = 29, targetSdk = 37).
- **Gradle**: 8.x+ (Wrapper included).

### Quick Start Commands:

```bash
# 1. Clone the repository
git clone https://github.com/dimetri/aurorah.git
cd aurorah

# 2. Grant execution permissions to Gradle wrapper
chmod +x gradlew

# 3. Build the Debug APK
./gradlew assembleDebug

# 4. Run automated unit tests
./gradlew test

# 5. Install Debug build to connected device or emulator
./gradlew installDebug
```

---

## ⚙️ 7. CI/CD Pipeline & Automated Testing

### GitHub Actions Workflow (`.github/workflows/android.yml`):
Continuous Integration automatically runs on every push and pull request to `main` and `dimetri-branch-2` branches:
- **Runner**: `ubuntu-latest`
- **JDK**: Temurin JDK 17 with Gradle caching
- **Tasks Executed**:
  1. `./gradlew assembleDebug` (Builds Debug APK)
  2. `./gradlew test` (Runs full JUnit 4 unit testing suite)

### Automated Unit Test Suite (`app/src/test/java/com/example/aurorah/`):
- `SecurityUtilsTest`: Verifies salt generation, SHA-256 password hashing, salt splitting, and session token format.
- `ResourceMatchingEngineTest`: Verifies geographic matching (35 points), skill/need alignment (25 points), urgency scoring boosts, and match sorting.
- `InputValidationTest`: Verifies email syntax, South African phone number format (+27 / 0xx), required form fields, and donation amounts.
- `AuthViewModelTest`: Verifies user login, registration, fast admin role elevation, and session teardown.

---

## 👥 8. Team Members & Credits

- **Project Creator & Lead Engineer**: Dimetri
- **Organization Lead**: Latifah Jacobs (Founder & Executive Director, Aurorah CAN)
- **Community Stakeholders**: Kuils River Community Action Network, Hanover Park Kitchen Initiative, Cape Town Metro Civic Alliance.
- **Built With**: Kotlin, Jetpack Compose Material 3, Room, Kotlin Coroutines, Retrofit, OkHttp, Azure SQL, and GitHub Actions.

---
*Dignity • Community • Transparency — Aurorah CAN Digital Hub 2025*
