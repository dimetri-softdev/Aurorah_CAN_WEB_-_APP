package com.example.aurorah.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aurorah.data.local.dao.AdminDao
import com.example.aurorah.data.local.dao.BeneficiaryDao
import com.example.aurorah.data.local.dao.CommunityNeedDao
import com.example.aurorah.data.local.dao.DonationDao
import com.example.aurorah.data.local.dao.NewsDao
import com.example.aurorah.data.local.dao.ReportDao
import com.example.aurorah.data.local.dao.VolunteerDao
import com.example.aurorah.data.local.entity.AdminEntity
import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.example.aurorah.data.local.entity.CommunityNeedEntity
import com.example.aurorah.data.local.entity.DonationEntity
import com.example.aurorah.data.local.entity.NewsEntity
import com.example.aurorah.data.local.entity.ReportEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        VolunteerEntity::class,
        BeneficiaryEntity::class,
        DonationEntity::class,
        CommunityNeedEntity::class,
        AdminEntity::class,
        ReportEntity::class,
        NewsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun volunteerDao(): VolunteerDao
    abstract fun beneficiaryDao(): BeneficiaryDao
    abstract fun donationDao(): DonationDao
    abstract fun communityNeedDao(): CommunityNeedDao
    abstract fun adminDao(): AdminDao
    abstract fun reportDao(): ReportDao
    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aurorah_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(db: AppDatabase) {
                // Seed Admin
                val adminDao = db.adminDao()
                if (adminDao.getCount() == 0) {
                    adminDao.insertAll(
                        listOf(
                            AdminEntity(
                                id = 1,
                                username = "latifah",
                                passwordHash = "admin123",
                                role = "Super Admin",
                                fullName = "Latifah Jacobs",
                                email = "latifah@aurorahcan.org",
                                lastLogin = "2025-05-20 10:30"
                            ),
                            AdminEntity(
                                id = 2,
                                username = "admin",
                                passwordHash = "password",
                                role = "Community Coordinator",
                                fullName = "Community Admin",
                                email = "admin@aurorahcan.org",
                                lastLogin = "2025-05-19 14:15"
                            )
                        )
                    )
                }

                // Seed Volunteers
                val volunteerDao = db.volunteerDao()
                if (volunteerDao.getCount() == 0) {
                    volunteerDao.insertAll(
                        listOf(
                            VolunteerEntity(
                                id = 1,
                                fullName = "Amina Adams",
                                email = "amina.adams@gmail.com",
                                phone = "+27 82 123 4567",
                                area = "Mitchells Plain",
                                skills = "Food Prep & Logistics",
                                availability = "Weekends & Evenings",
                                address = "12 Beacon Valley Rd, Mitchells Plain",
                                status = "Active",
                                registrationDate = "2025-01-15",
                                popiaConsentTimestamp = "2025-01-15 09:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            VolunteerEntity(
                                id = 2,
                                fullName = "Thabo Mbeki",
                                email = "thabo.mbeki@yahoo.com",
                                phone = "+27 73 987 6543",
                                area = "Khayelitsha",
                                skills = "Transportation & Driving",
                                availability = "Weekdays",
                                address = "45 Mew Way, Khayelitsha",
                                status = "Active",
                                registrationDate = "2025-02-01",
                                popiaConsentTimestamp = "2025-02-01 11:30:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            VolunteerEntity(
                                id = 3,
                                fullName = "Sarah Jenkins",
                                email = "sarah.j@outlook.com",
                                phone = "+27 84 555 1234",
                                area = "Woodstock",
                                skills = "Youth Tutoring & Admin",
                                availability = "Flexible",
                                address = "89 Victoria Rd, Woodstock",
                                status = "Active",
                                registrationDate = "2025-03-10",
                                popiaConsentTimestamp = "2025-03-10 14:15:00",
                                popiaConsentVersion = "v1.0"
                            )
                        )
                    )
                }

                // Seed Beneficiaries
                val beneficiaryDao = db.beneficiaryDao()
                if (beneficiaryDao.getCount() == 0) {
                    beneficiaryDao.insertAll(
                        listOf(
                            BeneficiaryEntity(
                                id = 1,
                                fullName = "Fatima Patel",
                                email = "fatima.patel@gmail.com",
                                phone = "+27 81 234 5678",
                                address = "34 Tafelsig St, Mitchells Plain",
                                area = "Mitchells Plain",
                                familySize = 5,
                                assistanceNeeded = "Weekly Food Parcel & Electricity Voucher",
                                urgency = "High",
                                registrationDate = "2025-05-01",
                                status = "Pending",
                                popiaConsentTimestamp = "2025-05-01 08:45:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            BeneficiaryEntity(
                                id = 2,
                                fullName = "Sipho Nhlapo",
                                email = "sipho.n@gmail.com",
                                phone = "+27 72 345 6789",
                                address = "12 NY 11, Gugulethu",
                                area = "Gugulethu",
                                familySize = 4,
                                assistanceNeeded = "Chronic Medical Supply Assistance",
                                urgency = "Urgent",
                                registrationDate = "2025-04-22",
                                status = "Approved",
                                popiaConsentTimestamp = "2025-04-22 10:20:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            BeneficiaryEntity(
                                id = 3,
                                fullName = "Maria Van Zyl",
                                email = "maria.vanzyl@telkomsa.net",
                                phone = "+27 83 456 7890",
                                address = "78 Voortrekker Rd, Bellville",
                                area = "Bellville",
                                familySize = 3,
                                assistanceNeeded = "Winter Blankets & Warm Clothing",
                                urgency = "Medium",
                                registrationDate = "2025-04-10",
                                status = "Fulfilled",
                                popiaConsentTimestamp = "2025-04-10 16:00:00",
                                popiaConsentVersion = "v1.0"
                            )
                        )
                    )
                }

                // Seed Donations
                val donationDao = db.donationDao()
                if (donationDao.getCount() == 0) {
                    donationDao.insertAll(
                        listOf(
                            DonationEntity(
                                id = 1,
                                donorName = "Cape Town Community Foundation",
                                email = "contact@ctcf.org.za",
                                phone = "+27 21 400 1000",
                                type = "Financial Grant",
                                amount = 25000.0,
                                cause = "Emergency Food Relief Fund",
                                isAnonymous = false,
                                message = "Supporting Aurorah CAN\'s winter food security program.",
                                paymentMethod = "EFT Direct Transfer",
                                date = "2025-05-18",
                                popiaConsentTimestamp = "2025-05-18 09:15:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            DonationEntity(
                                id = 2,
                                donorName = "David Miller",
                                email = "david.m@mweb.co.za",
                                phone = "+27 82 999 8877",
                                type = "Food Parcels",
                                amount = 1500.0,
                                cause = "Hanover Park Kitchen Drive",
                                isAnonymous = false,
                                message = "Glad to help local families.",
                                paymentMethod = "Credit Card",
                                date = "2025-05-15",
                                popiaConsentTimestamp = "2025-05-15 12:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            DonationEntity(
                                id = 3,
                                donorName = "Anonymous Friend",
                                email = "anon@aurorahcan.org",
                                phone = "N/A",
                                type = "Financial",
                                amount = 5000.0,
                                cause = "Youth After-School Education",
                                isAnonymous = true,
                                message = "Keep up the noble work!",
                                paymentMethod = "SnapScan",
                                date = "2025-05-12",
                                popiaConsentTimestamp = "2025-05-12 15:45:00",
                                popiaConsentVersion = "v1.0"
                            )
                        )
                    )
                }

                // Seed Community Needs
                val communityNeedDao = db.communityNeedDao()
                if (communityNeedDao.getCount() == 0) {
                    communityNeedDao.insertAll(
                        listOf(
                            CommunityNeedEntity(
                                id = 1,
                                description = "Fresh vegetables, rice & soup mix for 200 families in Hanover Park",
                                assistanceType = "Food Parcels",
                                priorityLevel = "Critical",
                                status = "Open",
                                area = "Hanover Park",
                                dateCreated = "2025-05-19",
                                assignedVolunteerId = null
                            ),
                            CommunityNeedEntity(
                                id = 2,
                                description = "School Uniforms and stationery kits for primary learners",
                                assistanceType = "Educational Support",
                                priorityLevel = "High",
                                status = "Open",
                                area = "Khayelitsha",
                                dateCreated = "2025-05-17",
                                assignedVolunteerId = null
                            ),
                            CommunityNeedEntity(
                                id = 3,
                                description = "Emergency clean water tanks and distribution drive",
                                assistanceType = "Infrastructure & Sanitation",
                                priorityLevel = "High",
                                status = "Assigned",
                                area = "Imizamo Yethu",
                                dateCreated = "2025-05-10",
                                assignedVolunteerId = 2
                            )
                        )
                    )
                }

                // Seed Reports
                val reportDao = db.reportDao()
                if (reportDao.getCount() == 0) {
                    reportDao.insertAll(
                        listOf(
                            ReportEntity(
                                id = 1,
                                title = "Q1 2025 Community Impact & Food Relief Summary",
                                type = "Impact Assessment",
                                generatedDate = "2025-04-01",
                                status = "Completed",
                                exportFormat = "PDF",
                                summary = "Over 3,500 beneficiaries assisted across Cape Town Metro with R120,000 in direct mutual aid."
                            ),
                            ReportEntity(
                                id = 2,
                                title = "2025 Financial Transparency & Donor Audit",
                                type = "Financial Audit",
                                generatedDate = "2025-04-15",
                                status = "Completed",
                                exportFormat = "CSV",
                                summary = "Complete balance sheet detailing grant allocations and public donation receipts."
                            )
                        )
                    )
                }

                // Seed News
                val newsDao = db.newsDao()
                if (newsDao.getCount() == 0) {
                    newsDao.insertAll(
                        listOf(
                            NewsEntity(
                                id = 1,
                                title = "Aurorah CAN Launches Expanded Kitchen Initiative in Hanover Park",
                                content = "Thanks to overwhelming donor support, Aurorah CAN has scaled its daily warm meal distribution to serve 200 additional families per day in Hanover Park.",
                                author = "Latifah Jacobs",
                                publishDate = "2025-05-19",
                                category = "Community Drive",
                                urgency = "High"
                            ),
                            NewsEntity(
                                id = 2,
                                title = "Over 1,200 Food Parcels Handed Out During Winter Warmth Drive",
                                content = "Volunteers rallied across Khayelitsha, Mitchells Plain, and Gugulethu to distribute essential food hampers and winter blankets.",
                                author = "Amina Adams",
                                publishDate = "2025-05-14",
                                category = "Impact Report",
                                urgency = "Normal"
                            ),
                            NewsEntity(
                                id = 3,
                                title = "Volunteer Spotlight: How Local Youth are Leading Mutual Aid Efforts",
                                content = "Meet the passionate young volunteers in Cape Town who dedicate their weekends to building resilient networks of support.",
                                author = "Sarah Jenkins",
                                publishDate = "2025-05-08",
                                category = "Volunteer Story",
                                urgency = "Normal"
                            )
                        )
                    )
                }
            }
        }
    }
}
