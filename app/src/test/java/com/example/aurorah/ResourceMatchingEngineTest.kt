package com.example.aurorah

import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.example.aurorah.data.local.entity.DonationEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import com.example.aurorah.util.ResourceMatchingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourceMatchingEngineTest {

    @Test
    fun generateMatches_matchesHighScoringVolunteerForSameAreaAndSkill() {
        val beneficiaries = listOf(
            BeneficiaryEntity(
                id = 1,
                fullName = "Fatima Patel",
                email = "fatima@gmail.com",
                phone = "+27812345678",
                address = "Mitchells Plain",
                area = "Mitchells Plain",
                familySize = 5,
                assistanceNeeded = "Weekly Food Parcel & Rice",
                urgency = "Emergency",
                registrationDate = "2025-05-01",
                status = "Pending"
            )
        )

        val volunteers = listOf(
            VolunteerEntity(
                id = 10,
                fullName = "Amina Adams",
                email = "amina@gmail.com",
                phone = "+27821234567",
                area = "Mitchells Plain",
                skills = "Food Prep & Logistics",
                availability = "Weekends",
                address = "Mitchells Plain",
                status = "Active",
                registrationDate = "2025-01-15"
            ),
            VolunteerEntity(
                id = 20,
                fullName = "Thabo Mbeki",
                email = "thabo@yahoo.com",
                phone = "+27739876543",
                area = "Khayelitsha",
                skills = "Transportation",
                availability = "Weekdays",
                address = "Khayelitsha",
                status = "Active",
                registrationDate = "2025-02-01"
            )
        )

        val donations = listOf(
            DonationEntity(
                id = 100,
                donorName = "CT Community Care",
                email = "ctcc@org.za",
                phone = "0214001000",
                type = "Food Parcels",
                amount = 5000.0,
                cause = "Weekly Food Relief",
                paymentMethod = "EFT",
                date = "2025-05-10"
            )
        )

        val matches = ResourceMatchingEngine.generateMatches(beneficiaries, volunteers, donations)

        assertEquals(1, matches.size)
        val match = matches.first()
        assertNotNull(match.recommendedVolunteer)
        assertEquals(10, match.recommendedVolunteer?.id)
        assertEquals("Amina Adams", match.recommendedVolunteer?.fullName)
        assertTrue(match.matchScore >= 80) // Baseline 30 + Area 35 + Skill 25 + Emergency 10 = 98 max
    }

    @Test
    fun generateMatches_emergencyUrgencyBoostsMatchScore() {
        val emergencyBeneficiary = BeneficiaryEntity(
            id = 1,
            fullName = "Urgent Case",
            email = "uc@gmail.com",
            phone = "0820000000",
            address = "Kuils River",
            area = "Kuils River",
            familySize = 4,
            assistanceNeeded = "Food Parcels",
            urgency = "Emergency",
            registrationDate = "2025-05-01",
            status = "Pending"
        )

        val normalBeneficiary = BeneficiaryEntity(
            id = 2,
            fullName = "Normal Case",
            email = "nc@gmail.com",
            phone = "0820000001",
            address = "Kuils River",
            area = "Kuils River",
            familySize = 2,
            assistanceNeeded = "Food Parcels",
            urgency = "Normal",
            registrationDate = "2025-05-01",
            status = "Pending"
        )

        val volunteers = listOf(
            VolunteerEntity(
                id = 5,
                fullName = "Volunteer A",
                email = "vol@gmail.com",
                phone = "0820000002",
                area = "Kuils River",
                skills = "Food Prep",
                availability = "Flexible",
                address = "Kuils River",
                status = "Active",
                registrationDate = "2025-01-01"
            )
        )

        val matches = ResourceMatchingEngine.generateMatches(
            listOf(emergencyBeneficiary, normalBeneficiary),
            volunteers,
            emptyList()
        )

        assertEquals(2, matches.size)
        val emergencyMatch = matches.first { it.beneficiary.id == 1 }
        val normalMatch = matches.first { it.beneficiary.id == 2 }

        assertTrue("Emergency match score should be higher than normal match score",
            emergencyMatch.matchScore > normalMatch.matchScore
        )
    }

    @Test
    fun generateMatches_returnsRecommendationsSortedByMatchScoreDescending() {
        val beneficiaries = listOf(
            BeneficiaryEntity(1, "Ben High", "b1@test.com", "0811111111", "Area A", "Area A", 3, "Food Parcels", "Emergency", "2025-05-01"),
            BeneficiaryEntity(2, "Ben Low", "b2@test.com", "0822222222", "Area Z", "Area Z", 2, "Other", "Low", "2025-05-01")
        )

        val volunteers = listOf(
            VolunteerEntity(1, "Vol A", "v1@test.com", "0833333333", "Area A", "Food Prep", "Flexible", "Area A", "Active", "2025-01-01")
        )

        val matches = ResourceMatchingEngine.generateMatches(beneficiaries, volunteers, emptyList())

        assertTrue(matches.size >= 2)
        assertTrue(matches[0].matchScore >= matches[1].matchScore)
    }
}
