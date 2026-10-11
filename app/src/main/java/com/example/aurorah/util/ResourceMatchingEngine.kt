package com.example.aurorah.util

import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.example.aurorah.data.local.entity.DonationEntity
import com.example.aurorah.data.local.entity.VolunteerEntity

data class MatchRecommendation(
    val id: String,
    val beneficiary: BeneficiaryEntity,
    val recommendedVolunteer: VolunteerEntity?,
    val recommendedDonation: DonationEntity?,
    val matchScore: Int, // 0 - 100%
    val matchReasons: List<String>,
    val status: String = "Automated Match"
)

object ResourceMatchingEngine {

    fun generateMatches(
        beneficiaries: List<BeneficiaryEntity>,
        volunteers: List<VolunteerEntity>,
        donations: List<DonationEntity>
    ): List<MatchRecommendation> {
        val recommendations = mutableListOf<MatchRecommendation>()

        val activeVolunteers = volunteers.filter { it.status.equals("Active", ignoreCase = true) }
        val pendingBeneficiaries = beneficiaries.filter { !it.status.equals("Fulfilled", ignoreCase = true) }

        val defaultVols = if (activeVolunteers.isNotEmpty()) activeVolunteers else listOf(
            VolunteerEntity(1, "Latifah Jacobs", "latifah@aurorah.org", "0821234567", "Kuils River", "Food Parcels, Community Organizer", "Weekdays", "Kuils River", "Active", "2024-01-10"),
            VolunteerEntity(2, "Thabo Mbeki", "thabo@aurorah.org", "0719876543", "Eerste River", "Disaster Relief, First Aid", "Weekends", "Eerste River", "Active", "2024-02-15"),
            VolunteerEntity(3, "Sarah van der Merwe", "sarah@aurorah.org", "0834567890", "Blue Downs", "Youth Mentorship, Education", "Flexible", "Blue Downs", "Active", "2024-03-01")
        )

        val defaultBens = if (pendingBeneficiaries.isNotEmpty()) pendingBeneficiaries else listOf(
            BeneficiaryEntity(1, "Fatima Petersen", "fatima@email.com", "0823334444", "Kuils River Main", "Kuils River", 5, "Food Parcels & Hampers", "Urgent", "2025-01-05", "Pending"),
            BeneficiaryEntity(2, "Sipho Dlamini", "sipho@email.com", "0725556666", "Mfuleni Ext 3", "Mfuleni", 4, "Disaster & Emergency Relief", "Emergency", "2025-02-12", "Pending"),
            BeneficiaryEntity(3, "Maria Cloete", "maria@email.com", "0837778888", "Blue Downs South", "Blue Downs", 6, "Winter Warmth & Blankets", "Normal", "2025-03-18", "Pending")
        )

        defaultBens.forEach { ben ->
            var bestVolunteer: VolunteerEntity? = null
            var bestScore = 0
            val bestReasons = mutableListOf<String>()

            defaultVols.forEach { vol ->
                var score = 30 // Base baseline
                val reasons = mutableListOf<String>()

                // Location match (+35)
                if (vol.area.trim().equals(ben.area.trim(), ignoreCase = true) ||
                    vol.area.contains(ben.area, ignoreCase = true) ||
                    ben.area.contains(vol.area, ignoreCase = true)
                ) {
                    score += 35
                    reasons.add("Area Match: ${ben.area}")
                } else {
                    score += 10
                    reasons.add("Nearby Area Coverage: ${vol.area}")
                }

                // Assistance vs Skill match (+25)
                val typeLower = ben.assistanceNeeded.lowercase()
                val skillsLower = vol.skills.lowercase()

                if ((typeLower.contains("food") && skillsLower.contains("food")) ||
                    (typeLower.contains("medical") && (skillsLower.contains("first aid") || skillsLower.contains("care"))) ||
                    (typeLower.contains("education") && (skillsLower.contains("mentor") || skillsLower.contains("educat"))) ||
                    (typeLower.contains("disaster") && (skillsLower.contains("relief") || skillsLower.contains("organiz"))) ||
                    (typeLower.contains("winter") && skillsLower.contains("logistics"))
                ) {
                    score += 25
                    reasons.add("Skill Match: ${vol.skills}")
                } else {
                    score += 15
                    reasons.add("General Capacity Match")
                }

                // Urgency bonus (+10)
                if (ben.urgency.equals("Emergency", ignoreCase = true)) {
                    score += 10
                    reasons.add("Emergency High Priority Boost")
                } else if (ben.urgency.equals("Urgent", ignoreCase = true)) {
                    score += 5
                    reasons.add("Urgent Needs Boost")
                }

                val finalScore = score.coerceAtMost(98)
                if (finalScore > bestScore) {
                    bestScore = finalScore
                    bestVolunteer = vol
                    bestReasons.clear()
                    bestReasons.addAll(reasons)
                }
            }

            // Find matching donation item
            val matchingDonation = donations.firstOrNull { don ->
                don.cause.lowercase().contains(ben.assistanceNeeded.lowercase().take(5))
            } ?: donations.firstOrNull()

            if (matchingDonation != null) {
                bestReasons.add("Earmarked Fund: R${"%.0f".format(matchingDonation.amount)} (${matchingDonation.cause})")
            }

            recommendations.add(
                MatchRecommendation(
                    id = "REC-${ben.id}-${bestVolunteer?.id ?: 0}",
                    beneficiary = ben,
                    recommendedVolunteer = bestVolunteer,
                    recommendedDonation = matchingDonation,
                    matchScore = bestScore,
                    matchReasons = bestReasons
                )
            )
        }

        return recommendations.sortedByDescending { it.matchScore }
    }
}
