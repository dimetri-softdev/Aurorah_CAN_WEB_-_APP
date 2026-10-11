package com.example.aurorah.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.aurorah.data.local.entity.DonationEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EftBankDetails(
    val bankName: String = "Standard Bank South Africa",
    val accountName: String = "Aurorah Community Action Network NPO",
    val accountNumber: String = "101 9283 7461",
    val accountType: String = "Business Cheque Account",
    val branchCode: String = "051001 (Kuils River Branch)",
    val swiftCode: String = "SBZA ZA JJ",
    val referencePrefix: String = "AUR-DON-"
)

object PaymentGatewayManager {

    val eftDetails = EftBankDetails()

    fun getEftReference(donationId: Int): String {
        return "${eftDetails.referencePrefix}${1000 + donationId}"
    }

    /**
     * Generates an official SARS Section 18A Tax Certificate PDF for donor tax deductions.
     */
    fun generateSection18aReceiptPdf(context: Context, donation: DonationEntity): File? {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val primaryPaint = Paint().apply {
            color = Color.rgb(26, 82, 118)
            isAntiAlias = true
        }

        val headerTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subHeaderPaint = Paint().apply {
            color = Color.WHITE
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.rgb(26, 82, 118)
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val sectionTitlePaint = Paint().apply {
            color = Color.rgb(33, 33, 33)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.rgb(51, 51, 51)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val boldBodyPaint = Paint().apply {
            color = Color.rgb(20, 20, 20)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
            isAntiAlias = true
        }

        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 8f
            isAntiAlias = true
        }

        // Header
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 90f, primaryPaint)
        canvas.drawText("AURORAH COMMUNITY ACTION NETWORK (CAN)", 30f, 38f, headerTextPaint)
        canvas.drawText("Public Benefit Organisation PBO #930076892 • NPO #2024/CAN/001", 30f, 58f, subHeaderPaint)
        canvas.drawText("SECTION 18A DONATION TAX CERTIFICATE", 30f, 74f, subHeaderPaint)

        var yPos = 125f
        val certNo = "SARS-18A-${10000 + donation.id}"
        val todayStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())

        canvas.drawText("TAX CERTIFICATE RECEIPT: $certNo", 30f, yPos, titlePaint)
        yPos += 20f
        canvas.drawText("Issued under Section 18A(1)(a) of the Income Tax Act No. 58 of 1962", 30f, yPos, bodyPaint)
        yPos += 16f
        canvas.drawLine(30f, yPos, pageWidth - 30f, yPos, linePaint)
        yPos += 24f

        // Donor & Transaction Table
        canvas.drawText("1. DONOR DETAILS", 30f, yPos, sectionTitlePaint)
        yPos += 18f
        canvas.drawText("Donor Name:", 30f, yPos, boldBodyPaint)
        canvas.drawText(donation.donorName, 160f, yPos, bodyPaint)
        yPos += 16f
        canvas.drawText("Donor Email:", 30f, yPos, boldBodyPaint)
        canvas.drawText(donation.email, 160f, yPos, bodyPaint)
        yPos += 16f
        canvas.drawText("Donor Phone:", 30f, yPos, boldBodyPaint)
        canvas.drawText(donation.phone, 160f, yPos, bodyPaint)
        yPos += 20f

        canvas.drawLine(30f, yPos, pageWidth - 30f, yPos, linePaint)
        yPos += 24f

        canvas.drawText("2. CONTRIBUTION SUMMARY", 30f, yPos, sectionTitlePaint)
        yPos += 18f
        canvas.drawText("Donation Receipt ID:", 30f, yPos, boldBodyPaint)
        canvas.drawText("#AUR-DON-${donation.id}", 160f, yPos, bodyPaint)
        yPos += 16f
        canvas.drawText("Amount Received:", 30f, yPos, boldBodyPaint)
        canvas.drawText("R${"%.2f".format(donation.amount)} ZAR", 160f, yPos, titlePaint)
        yPos += 20f
        canvas.drawText("Designated Cause:", 30f, yPos, boldBodyPaint)
        canvas.drawText(donation.cause, 160f, yPos, bodyPaint)
        yPos += 16f
        canvas.drawText("Payment Gateway:", 30f, yPos, boldBodyPaint)
        canvas.drawText(donation.paymentMethod, 160f, yPos, bodyPaint)
        yPos += 16f
        canvas.drawText("Transaction Date:", 30f, yPos, boldBodyPaint)
        canvas.drawText(donation.date, 160f, yPos, bodyPaint)
        yPos += 20f

        canvas.drawLine(30f, yPos, pageWidth - 30f, yPos, linePaint)
        yPos += 24f

        canvas.drawText("3. SARS COMPLIANCE DECLARATION", 30f, yPos, sectionTitlePaint)
        yPos += 18f
        val legalText = "This receipt is issued for the purposes of Section 18A of the Income Tax Act 58 of 1962. The donation received will be used exclusively for bona fide Public Benefit Activities in the Kuils River and Cape Metro areas as specified in Part I of the Ninth Schedule."
        val words = legalText.split(" ")
        var lineText = ""
        for (w in words) {
            val test = if (lineText.isEmpty()) w else "$lineText $w"
            if (bodyPaint.measureText(test) > (pageWidth - 60f)) {
                canvas.drawText(lineText, 30f, yPos, bodyPaint)
                yPos += 14f
                lineText = w
            } else {
                lineText = test
            }
        }
        if (lineText.isNotEmpty()) {
            canvas.drawText(lineText, 30f, yPos, bodyPaint)
            yPos += 24f
        }

        // Stamp Box
        val stampPaint = Paint().apply {
            color = Color.rgb(26, 82, 118)
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawRoundRect(30f, yPos, 260f, yPos + 60f, 10f, 10f, stampPaint)
        canvas.drawText("OFFICIAL SARS PBO STAMP", 45f, yPos + 22f, boldBodyPaint)
        canvas.drawText("PBO #930076892 • AUDITED", 45f, yPos + 40f, bodyPaint)

        canvas.drawText("Authorized Financial Officer:", 300f, yPos + 22f, boldBodyPaint)
        canvas.drawText("Latifah Jacobs (Co-Founder)", 300f, yPos + 40f, bodyPaint)
        canvas.drawText("Date: $todayStr", 300f, yPos + 55f, bodyPaint)

        // Footer
        val footerY = pageHeight - 50f
        canvas.drawLine(30f, footerY - 10f, pageWidth - 30f, footerY - 10f, linePaint)
        canvas.drawText("Aurorah CAN NPO • Section 18A Tax Certificate • Keep for SARS Annual Tax Return (IT12/IT14)", 30f, footerY, footerPaint)

        pdfDocument.finishPage(page)

        return try {
            val receiptsDir = File(context.getExternalFilesDir("receipts") ?: context.filesDir, "receipts").apply {
                if (!exists()) mkdirs()
            }
            val fileName = "aurorah_tax_receipt_${donation.id}_${System.currentTimeMillis()}.pdf"
            val file = File(receiptsDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun openTaxReceiptPdf(context: Context, file: File) {
        try {
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(intent, "Open Section 18A Tax Certificate")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Saved Tax Certificate to: ${file.name}", Toast.LENGTH_LONG).show()
        }
    }
}
