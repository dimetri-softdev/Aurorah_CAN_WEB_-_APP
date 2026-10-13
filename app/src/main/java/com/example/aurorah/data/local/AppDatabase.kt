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
    version = 3,
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
                // --- 1. SEED ADMINS (10 Records) ---
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
                                lastLogin = "2025-05-20 10:30:00"
                            ),
                            AdminEntity(
                                id = 2,
                                username = "admin",
                                passwordHash = "password",
                                role = "Community Coordinator",
                                fullName = "Community Admin",
                                email = "admin@aurorahcan.org",
                                lastLogin = "2025-05-19 14:15:00"
                            ),
                            AdminEntity(
                                id = 3,
                                username = "ziyaad_m",
                                passwordHash = "admin123",
                                role = "Logistics Manager",
                                fullName = "Ziyaad Mohamed",
                                email = "ziyaad@aurorahcan.org",
                                lastLogin = "2025-05-18 09:00:00"
                            ),
                            AdminEntity(
                                id = 4,
                                username = "chantal_v",
                                passwordHash = "admin123",
                                role = "Finance & Audit Officer",
                                fullName = "Chantal Van Wyk",
                                email = "chantal@aurorahcan.org",
                                lastLogin = "2025-05-17 11:20:00"
                            ),
                            AdminEntity(
                                id = 5,
                                username = "naledi_s",
                                passwordHash = "admin123",
                                role = "Field Operations Lead",
                                fullName = "Naledi Sithole",
                                email = "naledi@aurorahcan.org",
                                lastLogin = "2025-05-16 16:45:00"
                            ),
                            AdminEntity(
                                id = 6,
                                username = "willem_b",
                                passwordHash = "admin123",
                                role = "Volunteers Lead",
                                fullName = "Willem Botha",
                                email = "willem@aurorahcan.org",
                                lastLogin = "2025-05-15 08:30:00"
                            ),
                            AdminEntity(
                                id = 7,
                                username = "devan_g",
                                passwordHash = "admin123",
                                role = "Data Security Officer",
                                fullName = "Devan Govender",
                                email = "devan@aurorahcan.org",
                                lastLogin = "2025-05-14 13:10:00"
                            ),
                            AdminEntity(
                                id = 8,
                                username = "ayanda_k",
                                passwordHash = "admin123",
                                role = "Beneficiary Liaison",
                                fullName = "Ayanda Khumalo",
                                email = "ayanda@aurorahcan.org",
                                lastLogin = "2025-05-13 12:00:00"
                            ),
                            AdminEntity(
                                id = 9,
                                username = "priya_n",
                                passwordHash = "admin123",
                                role = "Outreach Specialist",
                                fullName = "Priya Naidoo",
                                email = "priya@aurorahcan.org",
                                lastLogin = "2025-05-12 15:30:00"
                            ),
                            AdminEntity(
                                id = 10,
                                username = "fatima_a",
                                passwordHash = "admin123",
                                role = "Kitchen Operations Director",
                                fullName = "Fatima Abrahams",
                                email = "fatima.a@aurorahcan.org",
                                lastLogin = "2025-05-11 10:00:00"
                            )
                        )
                    )
                }

                // --- 2. SEED VOLUNTEERS (10 Records) ---
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
                            ),
                            VolunteerEntity(
                                id = 4,
                                fullName = "Jason Hendricks",
                                email = "jason.h@gmail.com",
                                phone = "+27 82 333 4444",
                                area = "Kuils River",
                                skills = "First Aid & Emergency Response",
                                availability = "On Call 24/7",
                                address = "14 Station Rd, Kuils River",
                                status = "Active",
                                registrationDate = "2025-03-15",
                                popiaConsentTimestamp = "2025-03-15 10:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            VolunteerEntity(
                                id = 5,
                                fullName = "Bongiwe Sithole",
                                email = "bongiwe.s@gmail.com",
                                phone = "+27 71 444 5555",
                                area = "Gugulethu",
                                skills = "Community Health & Home Care",
                                availability = "Weekdays & Mornings",
                                address = "78 NY 3, Gugulethu",
                                status = "Active",
                                registrationDate = "2025-03-20",
                                popiaConsentTimestamp = "2025-03-20 12:30:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            VolunteerEntity(
                                id = 6,
                                fullName = "Pieter Van Der Merwe",
                                email = "pieter.vdm@telkomsa.net",
                                phone = "+27 83 666 7777",
                                area = "Bellville",
                                skills = "Food Parcels & Warehouse Packaging",
                                availability = "Saturday Mornings",
                                address = "102 Durban Rd, Bellville",
                                status = "Active",
                                registrationDate = "2025-04-01",
                                popiaConsentTimestamp = "2025-04-01 09:15:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            VolunteerEntity(
                                id = 7,
                                fullName = "Zukiswa Ndamase",
                                email = "zukiswa.n@yahoo.com",
                                phone = "+27 72 888 9999",
                                area = "Langa",
                                skills = "Elderly Support & Counseling",
                                availability = "Afternoons",
                                address = "23 Washington St, Langa",
                                status = "Active",
                                registrationDate = "2025-04-05",
                                popiaConsentTimestamp = "2025-04-05 15:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            VolunteerEntity(
                                id = 8,
                                fullName = "Tariq Patel",
                                email = "tariq.p@mweb.co.za",
                                phone = "+27 84 111 2222",
                                area = "Athlone",
                                skills = "IT & Digital Hub Operations",
                                availability = "Evenings",
                                address = "56 Klipfontein Rd, Athlone",
                                status = "Active",
                                registrationDate = "2025-04-12",
                                popiaConsentTimestamp = "2025-04-12 18:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            VolunteerEntity(
                                id = 9,
                                fullName = "Liezel Swarts",
                                email = "liezel.s@gmail.com",
                                phone = "+27 81 777 8888",
                                area = "Eerste River",
                                skills = "Warm Clothing & Blanket Sorting",
                                availability = "Weekends",
                                address = "34 Forest Drive, Eerste River",
                                status = "Active",
                                registrationDate = "2025-04-18",
                                popiaConsentTimestamp = "2025-04-18 11:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            VolunteerEntity(
                                id = 10,
                                fullName = "Marcus Davids",
                                email = "marcus.d@gmail.com",
                                phone = "+27 73 222 3333",
                                area = "Delft",
                                skills = "Youth Sports & Mentorship",
                                availability = "Sundays",
                                address = "90 Main Rd, Delft",
                                status = "Active",
                                registrationDate = "2025-04-25",
                                popiaConsentTimestamp = "2025-04-25 14:30:00",
                                popiaConsentVersion = "v1.0"
                            )
                        )
                    )
                }

                // --- 3. SEED BENEFICIARIES (10 Records) ---
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
                            ),
                            BeneficiaryEntity(
                                id = 4,
                                fullName = "Nomsa Dlamini",
                                email = "nomsa.d@gmail.com",
                                phone = "+27 71 567 8901",
                                address = "89 Spine Rd, Khayelitsha",
                                area = "Khayelitsha",
                                familySize = 6,
                                assistanceNeeded = "Emergency Food & Water Parcel",
                                urgency = "Urgent",
                                registrationDate = "2025-05-02",
                                status = "Pending",
                                popiaConsentTimestamp = "2025-05-02 09:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            BeneficiaryEntity(
                                id = 5,
                                fullName = "Rashid Abrahams",
                                email = "rashid.a@yahoo.com",
                                phone = "+27 82 678 9012",
                                address = "15 Hanover Park Ave, Hanover Park",
                                area = "Hanover Park",
                                familySize = 4,
                                assistanceNeeded = "Infant Nutrition & Nappies",
                                urgency = "High",
                                registrationDate = "2025-05-03",
                                status = "Approved",
                                popiaConsentTimestamp = "2025-05-03 11:15:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            BeneficiaryEntity(
                                id = 6,
                                fullName = "Chloe Fortuin",
                                email = "chloe.f@gmail.com",
                                phone = "+27 84 789 0123",
                                address = "27 Voorbrug Rd, Delft",
                                area = "Delft",
                                familySize = 5,
                                assistanceNeeded = "School Stationery & Uniforms",
                                urgency = "Medium",
                                registrationDate = "2025-05-05",
                                status = "Pending",
                                popiaConsentTimestamp = "2025-05-05 13:40:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            BeneficiaryEntity(
                                id = 7,
                                fullName = "Vuyokazi Mgijima",
                                email = "vuyo.m@gmail.com",
                                phone = "+27 73 890 1234",
                                address = "54 Lerotholi Ave, Langa",
                                area = "Langa",
                                familySize = 2,
                                assistanceNeeded = "Elderly Care & Home Meal Delivery",
                                urgency = "High",
                                registrationDate = "2025-05-07",
                                status = "Approved",
                                popiaConsentTimestamp = "2025-05-07 14:20:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            BeneficiaryEntity(
                                id = 8,
                                fullName = "David September",
                                email = "david.s@outlook.com",
                                phone = "+27 81 901 2345",
                                address = "10 Stratos Way, Eerste River",
                                area = "Eerste River",
                                familySize = 7,
                                assistanceNeeded = "Family Food Hamper & Paraffin",
                                urgency = "Urgent",
                                registrationDate = "2025-05-09",
                                status = "Pending",
                                popiaConsentTimestamp = "2025-05-09 10:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            BeneficiaryEntity(
                                id = 9,
                                fullName = "Annelize Coetzee",
                                email = "annelize.c@telkomsa.net",
                                phone = "+27 83 012 3456",
                                address = "42 Van Riebeeck St, Kuils River",
                                area = "Kuils River",
                                familySize = 3,
                                assistanceNeeded = "Medical Equipment Support",
                                urgency = "Medium",
                                registrationDate = "2025-05-11",
                                status = "Fulfilled",
                                popiaConsentTimestamp = "2025-05-11 15:30:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            BeneficiaryEntity(
                                id = 10,
                                fullName = "Siyabonga Kulu",
                                email = "siyabonga.k@gmail.com",
                                phone = "+27 72 123 9876",
                                address = "8 Mfuleni Main Rd, Mfuleni",
                                area = "Mfuleni",
                                familySize = 5,
                                assistanceNeeded = "Shelter Repairs & Tarpaulins",
                                urgency = "Emergency",
                                registrationDate = "2025-05-13",
                                status = "Pending",
                                popiaConsentTimestamp = "2025-05-13 16:50:00",
                                popiaConsentVersion = "v1.0"
                            )
                        )
                    )
                }

                // --- 4. SEED DONATIONS (10 Records) ---
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
                                message = "Supporting Aurorah CAN's winter food security program.",
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
                            ),
                            DonationEntity(
                                id = 4,
                                donorName = "Shoprite Checkers Community Care",
                                email = "community@shoprite.co.za",
                                phone = "+27 21 980 4000",
                                type = "In-Kind Food Supplies",
                                amount = 15000.0,
                                cause = "Kuils River Central Pantry",
                                isAnonymous = false,
                                message = "Monthly dry goods supply drive.",
                                paymentMethod = "Corporate Sponsorship",
                                date = "2025-05-10",
                                popiaConsentTimestamp = "2025-05-10 10:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            DonationEntity(
                                id = 5,
                                donorName = "Stellenbosch Wine Valley Trust",
                                email = "trust@sun.ac.za",
                                phone = "+27 21 808 9111",
                                type = "Financial Grant",
                                amount = 50000.0,
                                cause = "Winter Warmth & Shelter Drive",
                                isAnonymous = false,
                                message = "Annual community grant allocation.",
                                paymentMethod = "Bank Transfer",
                                date = "2025-05-08",
                                popiaConsentTimestamp = "2025-05-08 14:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            DonationEntity(
                                id = 6,
                                donorName = "Zainab Ismail",
                                email = "zainab.i@gmail.com",
                                phone = "+27 82 444 3322",
                                type = "Clothing & Blankets",
                                amount = 2500.0,
                                cause = "Cape Flats Youth Relief",
                                isAnonymous = false,
                                message = "Warm clothes and blankets for winter.",
                                paymentMethod = "Debit Card",
                                date = "2025-05-06",
                                popiaConsentTimestamp = "2025-05-06 16:20:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            DonationEntity(
                                id = 7,
                                donorName = "Lion's Club Cape Town",
                                email = "info@lionsct.org.za",
                                phone = "+27 21 685 1234",
                                type = "Medical Supplies",
                                amount = 8000.0,
                                cause = "Chronic Care Support",
                                isAnonymous = false,
                                message = "Supporting elderly medical needs.",
                                paymentMethod = "Cheque",
                                date = "2025-05-04",
                                popiaConsentTimestamp = "2025-05-04 11:30:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            DonationEntity(
                                id = 8,
                                donorName = "John & Brenda Smith",
                                email = "smiths@gmail.com",
                                phone = "+27 83 777 6655",
                                type = "Financial",
                                amount = 3000.0,
                                cause = "General Community Support",
                                isAnonymous = false,
                                message = "With love from the Smith family.",
                                paymentMethod = "SnapScan",
                                date = "2025-05-02",
                                popiaConsentTimestamp = "2025-05-02 13:00:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            DonationEntity(
                                id = 9,
                                donorName = "Rotary Club Bellville",
                                email = "bellville@rotary.org.za",
                                phone = "+27 21 948 5555",
                                type = "Stationery Packs",
                                amount = 4500.0,
                                cause = "School Readiness Campaign",
                                isAnonymous = false,
                                message = "Stationery kits for 100 primary school kids.",
                                paymentMethod = "EFT Direct Transfer",
                                date = "2025-04-28",
                                popiaConsentTimestamp = "2025-04-28 09:30:00",
                                popiaConsentVersion = "v1.0"
                            ),
                            DonationEntity(
                                id = 10,
                                donorName = "Peninsula Beverage Co.",
                                email = "csr@penbev.co.za",
                                phone = "+27 21 930 9000",
                                type = "Hydration Supplies",
                                amount = 12000.0,
                                cause = "Emergency Disaster Relief",
                                isAnonymous = false,
                                message = "Bottled drinking water for flood victims.",
                                paymentMethod = "Corporate Direct",
                                date = "2025-04-25",
                                popiaConsentTimestamp = "2025-04-25 15:15:00",
                                popiaConsentVersion = "v1.0"
                            )
                        )
                    )
                }

                // --- 5. SEED COMMUNITY NEEDS (10 Records) ---
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
                            ),
                            CommunityNeedEntity(
                                id = 4,
                                description = "Warm winter blankets and mattresses for informal settlement fires",
                                assistanceType = "Disaster Relief",
                                priorityLevel = "Critical",
                                status = "Open",
                                area = "Masiphumelele",
                                dateCreated = "2025-05-12",
                                assignedVolunteerId = null
                            ),
                            CommunityNeedEntity(
                                id = 5,
                                description = "First aid kits and mobile health screening clinic supplies",
                                assistanceType = "Healthcare & Wellness",
                                priorityLevel = "High",
                                status = "Assigned",
                                area = "Delft",
                                dateCreated = "2025-05-14",
                                assignedVolunteerId = 4
                            ),
                            CommunityNeedEntity(
                                id = 6,
                                description = "Soup kitchen gas burners and large cooking pots",
                                assistanceType = "Kitchen Infrastructure",
                                priorityLevel = "Medium",
                                status = "In Progress",
                                area = "Kuils River",
                                dateCreated = "2025-05-15",
                                assignedVolunteerId = 1
                            ),
                            CommunityNeedEntity(
                                id = 7,
                                description = "After-school tutoring space tables, chairs, and whiteboards",
                                assistanceType = "Education & Youth",
                                priorityLevel = "Medium",
                                status = "Open",
                                area = "Gugulethu",
                                dateCreated = "2025-05-16",
                                assignedVolunteerId = 3
                            ),
                            CommunityNeedEntity(
                                id = 8,
                                description = "Infant formula, diapers, and baby hygiene hampers",
                                assistanceType = "Infant & Maternal Care",
                                priorityLevel = "High",
                                status = "Open",
                                area = "Mitchells Plain",
                                dateCreated = "2025-05-18",
                                assignedVolunteerId = null
                            ),
                            CommunityNeedEntity(
                                id = 9,
                                description = "Wheelchair ramp construction and home accessibility repairs",
                                assistanceType = "Disability Support",
                                priorityLevel = "Medium",
                                status = "Open",
                                area = "Eerste River",
                                dateCreated = "2025-05-18",
                                assignedVolunteerId = null
                            ),
                            CommunityNeedEntity(
                                id = 10,
                                description = "Community garden seeds, soil fertilizer, and gardening tools",
                                assistanceType = "Sustainable Agriculture",
                                priorityLevel = "Low",
                                status = "Open",
                                area = "Langa",
                                dateCreated = "2025-05-19",
                                assignedVolunteerId = null
                            )
                        )
                    )
                }

                // --- 6. SEED REPORTS (10 Records) ---
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
                            ),
                            ReportEntity(
                                id = 3,
                                title = "Winter Warmth Campaign Operations Log",
                                type = "Operations Summary",
                                generatedDate = "2025-05-01",
                                status = "Completed",
                                exportFormat = "PDF",
                                summary = "Deployment metrics for 1,200 blankets and thermal clothing kits distributed across townships."
                            ),
                            ReportEntity(
                                id = 4,
                                title = "Volunteer Distribution & Hours Tracker",
                                type = "Volunteer Performance",
                                generatedDate = "2025-05-05",
                                status = "Completed",
                                exportFormat = "CSV",
                                summary = "Logged over 850 active volunteer hours across food prep, driving, and community care."
                            ),
                            ReportEntity(
                                id = 5,
                                title = "Beneficiary Demographics & Area Needs Analysis",
                                type = "Demographic Survey",
                                generatedDate = "2025-05-10",
                                status = "Completed",
                                exportFormat = "PDF",
                                summary = "In-depth research identifying critical food security gaps in Kuils River and Hanover Park."
                            ),
                            ReportEntity(
                                id = 6,
                                title = "Kuils River Central Soup Kitchen Efficiency Report",
                                type = "Program Evaluation",
                                generatedDate = "2025-05-12",
                                status = "Completed",
                                exportFormat = "PDF",
                                summary = "Evaluated kitchen meal cost efficiency at R8.50 per warm nutritious meal served."
                            ),
                            ReportEntity(
                                id = 7,
                                title = "POPIA Compliance & Data Protection Audit",
                                type = "Governance & Legal",
                                generatedDate = "2025-05-14",
                                status = "Completed",
                                exportFormat = "PDF",
                                summary = "System-wide review verifying 100% SHA-256 encryption, consent logging, and data anonymization."
                            ),
                            ReportEntity(
                                id = 8,
                                title = "Strategic Resource Allocation 2025-2026",
                                type = "Strategic Planning",
                                generatedDate = "2025-05-16",
                                status = "Pending",
                                exportFormat = "CSV",
                                summary = "Budget forecasting and resource matching recommendations for upcoming fiscal period."
                            ),
                            ReportEntity(
                                id = 9,
                                title = "Emergency Disaster Response - Masiphumelele Relief",
                                type = "Disaster Report",
                                generatedDate = "2025-05-18",
                                status = "Completed",
                                exportFormat = "PDF",
                                summary = "Emergency response log documenting 300 emergency shelters assisted within 24 hours."
                            ),
                            ReportEntity(
                                id = 10,
                                title = "Corporate Sponsorship & Grant Usage Summary",
                                type = "Financial Report",
                                generatedDate = "2025-05-20",
                                status = "Completed",
                                exportFormat = "PDF",
                                summary = "Detailed breakdown of grants received from CTCF, Shoprite, and Stellenbosch Wine Valley Trust."
                            )
                        )
                    )
                }

                // --- 7. SEED NEWS (10 Records) ---
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
                            ),
                            NewsEntity(
                                id = 4,
                                title = "Aurorah CAN Partners with Local Farmers for Sustainable Produce",
                                content = "A new partnership with Stellenbosch agricultural collectives ensures fresh, organic vegetables for our community kitchens every week.",
                                author = "Ziyaad Mohamed",
                                publishDate = "2025-05-05",
                                category = "Partnership",
                                urgency = "Normal"
                            ),
                            NewsEntity(
                                id = 5,
                                title = "Urgent Appeal: Emergency Blankets & Warm Gear Needed for Winter Cold Front",
                                content = "An incoming cold front threatens informal settlements. Aurorah CAN is collecting winter blankets, jackets, and waterproof tarpaulins.",
                                author = "Naledi Sithole",
                                publishDate = "2025-05-02",
                                category = "Urgent Notice",
                                urgency = "High"
                            ),
                            NewsEntity(
                                id = 6,
                                title = "Youth Educational Workshop Empowers 80 Students in Gugulethu",
                                content = "Volunteers hosted a successful weekend tutoring and digital literacy workshop for high school students preparing for exams.",
                                author = "Sarah Jenkins",
                                publishDate = "2025-04-28",
                                category = "Education",
                                urgency = "Normal"
                            ),
                            NewsEntity(
                                id = 7,
                                title = "Aurorah Digital Hub Upgraded with POPIA-Compliant Data Security",
                                content = "Our Android app now features enhanced SHA-256 password hashing, explicit consent management, and automated anonymization.",
                                author = "Devan Govender",
                                publishDate = "2025-04-22",
                                category = "Technology",
                                urgency = "Normal"
                            ),
                            NewsEntity(
                                id = 8,
                                title = "Annual Community Thanksgiving Feast Announced for June 2025",
                                content = "Aurorah CAN invites all volunteers, donors, and beneficiary families to celebrate our collective impact at Kuils River Civic Center.",
                                author = "Latifah Jacobs",
                                publishDate = "2025-04-18",
                                category = "Event",
                                urgency = "Normal"
                            ),
                            NewsEntity(
                                id = 9,
                                title = "Mobile Health Clinic Visits Kuils River & Surrounding Areas",
                                content = "In partnership with local healthcare workers, free blood pressure, diabetes, and health screenings were offered to over 150 residents.",
                                author = "Bongiwe Sithole",
                                publishDate = "2025-04-12",
                                category = "Healthcare",
                                urgency = "High"
                            ),
                            NewsEntity(
                                id = 10,
                                title = "Financial Transparency Report Released: 100% Funds Accounted For",
                                content = "Our Q1 financial audit confirms full accountability across all grants and individual donations, downloadable directly from the app.",
                                author = "Chantal Van Wyk",
                                publishDate = "2025-04-05",
                                category = "Governance",
                                urgency = "Normal"
                            )
                        )
                    )
                }
            }
        }
    }
}
