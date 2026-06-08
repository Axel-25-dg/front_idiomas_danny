package com.ute.guamanidiomas.ui.certificate

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class CertificateViewModel @Inject constructor() : ViewModel() {

    fun downloadCertificate(context: Context, studentName: String, courseName: String, certificateId: String) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        paint.color = android.graphics.Color.WHITE
        canvas.drawRect(0f, 0f, 595f, 842f, paint)

        paint.color = 0xFF1E40AF.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
        canvas.drawRect(40f, 40f, 555f, 802f, paint)

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = true

        paint.textSize = 36f
        canvas.drawText("JUMPUP", 297f, 150f, paint)

        paint.textSize = 24f
        paint.color = android.graphics.Color.DKGRAY
        canvas.drawText("CERTIFICADO DE FINALIZACIÓN", 297f, 220f, paint)

        paint.textSize = 16f
        paint.isFakeBoldText = false
        canvas.drawText("Otorgado a:", 297f, 280f, paint)

        paint.textSize = 32f
        paint.isFakeBoldText = true
        paint.color = 0xFF1E40AF.toInt()
        canvas.drawText(studentName.uppercase(), 297f, 340f, paint)

        paint.textSize = 18f
        paint.color = android.graphics.Color.BLACK
        paint.isFakeBoldText = false
        val lines = "Por haber completado satisfactoriamente el curso de\n$courseName".split("\n")
        var yPos = 420f
        for (line in lines) {
            canvas.drawText(line, 297f, yPos, paint)
            yPos += 30f
        }

        paint.textSize = 12f
        paint.color = android.graphics.Color.GRAY
        canvas.drawText("ID: $certificateId", 297f, 750f, paint)
        
        canvas.drawLine(100f, 650f, 250f, 650f, paint)
        canvas.drawText("Director Académico", 175f, 670f, paint)
        
        canvas.drawLine(345f, 650f, 495f, 650f, paint)
        canvas.drawText("Sello Digital", 420f, 670f, paint)

        pdfDocument.finishPage(page)

        savePdfToStorage(context, pdfDocument, "Certificado_$certificateId.pdf")
        pdfDocument.close()
    }

    private fun savePdfToStorage(context: Context, pdfDocument: PdfDocument, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(directory, fileName)
            android.net.Uri.fromFile(file)
        }

        uri?.let {
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(it)
                outputStream?.let { os ->
                    pdfDocument.writeTo(os)
                    os.close()
                    Toast.makeText(context, "Certificado descargado en Descargas", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al guardar el PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "No se pudo crear el archivo", Toast.LENGTH_SHORT).show()
        }
    }
}