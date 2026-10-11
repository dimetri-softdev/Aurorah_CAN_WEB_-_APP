package com.example.aurorah.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.aurorah.data.local.entity.BeneficiaryEntity
import com.example.aurorah.data.local.entity.DonationEntity
import com.example.aurorah.data.local.entity.VolunteerEntity
import java.io.File
import java.io.FileWriter

object CsvExportManager {

    private fun getExportsDirectory(context: Context): File {
        return File(context.getExternalFilesDir("exports") ?: context.filesDir, "exports").apply {
            if (!exists()) mkdirs()
        }
    }

    fun exportVolunteersToCsv(context: Context, list: List<VolunteerEntity>): File? {
        return try {
            val file = File(getExportsDirectory(context), "aurorah_volunteers_report.csv")
            val writer = FileWriter(file)

            writer.append("ID,Full Name,Email,Phone,Area,Skills,Availability,Status,Registration Date\n")

            list.forEach { vol ->
                writer.append("${vol.id},")
                    .append("\"${escapeCsv(vol.fullName)}\",")
                    .append("\"${escapeCsv(vol.email)}\",")
                    .append("\"${escapeCsv(vol.phone)}\",")
                    .append("\"${escapeCsv(vol.area)}\",")
                    .append("\"${escapeCsv(vol.skills)}\",")
                    .append("\"${escapeCsv(vol.availability)}\",")
                    .append("\"${escapeCsv(vol.status)}\",")
                    .append("\"${escapeCsv(vol.registrationDate)}\"\n")
            }

            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportDonationsToCsv(context: Context, list: List<DonationEntity>): File? {
        return try {
            val file = File(getExportsDirectory(context), "aurorah_donations_report.csv")
            val writer = FileWriter(file)

            writer.append("ID,Donor Name,Email,Phone,Type,Amount ZAR,Cause,Payment Method,Date,Anonymous\n")

            list.forEach { don ->
                writer.append("${don.id},")
                    .append("\"${escapeCsv(don.donorName)}\",")
                    .append("\"${escapeCsv(don.email)}\",")
                    .append("\"${escapeCsv(don.phone)}\",")
                    .append("\"${escapeCsv(don.type)}\",")
                    .append("${don.amount},")
                    .append("\"${escapeCsv(don.cause)}\",")
                    .append("\"${escapeCsv(don.paymentMethod)}\",")
                    .append("\"${escapeCsv(don.date)}\",")
                    .append("${don.isAnonymous}\n")
            }

            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportBeneficiariesToCsv(context: Context, list: List<BeneficiaryEntity>): File? {
        return try {
            val file = File(getExportsDirectory(context), "aurorah_beneficiaries_report.csv")
            val writer = FileWriter(file)

            writer.append("ID,Full Name,Email,Phone,Area,Family Size,Assistance Needed,Urgency,Status,Registration Date\n")

            list.forEach { ben ->
                writer.append("${ben.id},")
                    .append("\"${escapeCsv(ben.fullName)}\",")
                    .append("\"${escapeCsv(ben.email)}\",")
                    .append("\"${escapeCsv(ben.phone)}\",")
                    .append("\"${escapeCsv(ben.area)}\",")
                    .append("${ben.familySize},")
                    .append("\"${escapeCsv(ben.assistanceNeeded)}\",")
                    .append("\"${escapeCsv(ben.urgency)}\",")
                    .append("\"${escapeCsv(ben.status)}\",")
                    .append("\"${escapeCsv(ben.registrationDate)}\"\n")
            }

            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareCsvFile(context: Context, file: File) {
        try {
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Aurorah CAN Data Export: ${file.name}")
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Export & Share CSV")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Exported CSV saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }

    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"")
    }
}
