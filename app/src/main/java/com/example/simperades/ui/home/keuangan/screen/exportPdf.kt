package com.example.simperades.ui.home.keuangan.screen

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.simperades.ui.home.keuangan.dao.Keuangan
import com.example.simperades.utils.formatRupiah
import com.example.simperades.utils.formatTanggal
import java.io.OutputStream

fun exportPdf(
    context: Context,
    data: List<Keuangan>,
    totalPemasukan: Double,
    totalPengeluaran: Double,
    saldo: Double
) {

    val pdfDocument = PdfDocument()
    val paint = Paint()
    val titlePaint = Paint()
    val linePaint = Paint()

    val pageInfo = PdfDocument.PageInfo.Builder(842, 1191, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    titlePaint.textSize = 26f
    titlePaint.isFakeBoldText = true

    paint.textSize = 14f

    linePaint.strokeWidth = 2f

    var y = 60

    // ===== HEADER =====
    canvas.drawText("LAPORAN KEUANGAN Simperades", 230f, y.toFloat(), titlePaint)

    y += 40
    canvas.drawLine(50f, y.toFloat(), 792f, y.toFloat(), linePaint)

    y += 30
    canvas.drawText("Ringkasan Keuangan:", 50f, y.toFloat(), paint)

    y += 25
    canvas.drawText("Total Pemasukan : ${formatRupiah(totalPemasukan)}", 60f, y.toFloat(), paint)

    y += 20
    canvas.drawText("Total Pengeluaran : ${formatRupiah(totalPengeluaran)}", 60f, y.toFloat(), paint)

    y += 20
    canvas.drawText("Saldo Akhir : ${formatRupiah(saldo)}", 60f, y.toFloat(), paint)

    y += 40
    canvas.drawLine(50f, y.toFloat(), 792f, y.toFloat(), linePaint)

    y += 30

    // ===== TABLE HEADER =====
    val startX = 50f
    val colTanggal = startX
    val colKategori = 180f
    val colJenis = 350f
    val colJumlah = 550f

    paint.isFakeBoldText = true
    canvas.drawText("Tanggal", colTanggal, y.toFloat(), paint)
    canvas.drawText("Kategori", colKategori, y.toFloat(), paint)
    canvas.drawText("Jenis", colJenis, y.toFloat(), paint)
    canvas.drawText("Jumlah", colJumlah, y.toFloat(), paint)
    paint.isFakeBoldText = false

    y += 15
    canvas.drawLine(50f, y.toFloat(), 792f, y.toFloat(), linePaint)

    y += 25

    // ===== TABLE CONTENT =====
    data.forEach { item ->

        if (y > 1100) return@forEach

        val jenis = if (item.jumlah >= 0) "Pemasukan" else "Pengeluaran"

        canvas.drawText(
            formatTanggal(item.tanggal),
            colTanggal,
            y.toFloat(),
            paint
        )

        canvas.drawText(
            item.kategori,
            colKategori,
            y.toFloat(),
            paint
        )

        canvas.drawText(
            jenis,
            colJenis,
            y.toFloat(),
            paint
        )

        canvas.drawText(
            formatRupiah(item.jumlah),
            colJumlah,
            y.toFloat(),
            paint
        )

        y += 20
    }

    y += 10
    canvas.drawLine(50f, y.toFloat(), 792f, y.toFloat(), linePaint)

    pdfDocument.finishPage(page)

    val fileName = "Laporan_Keuangan_Simperades_${System.currentTimeMillis()}.pdf"

    val resolver = context.contentResolver
    val contentValues = android.content.ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    val uri: Uri? = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

    var savedUri: Uri? = null

    uri?.let {
        savedUri = it
        val outputStream: OutputStream? = resolver.openOutputStream(it)
        outputStream?.use { stream ->
            pdfDocument.writeTo(stream)
        }
    }

    pdfDocument.close()

    if (savedUri != null) {
        Toast.makeText(context, "PDF berhasil disimpan di Download", Toast.LENGTH_LONG).show()
        openPdfWithChooser(context, savedUri!!)
    } else {
        Toast.makeText(context, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show()
    }
}


/**
 * Membuka PDF dengan chooser agar user bisa memilih aplikasi
 */
private fun openPdfWithChooser(context: Context, uri: Uri) {

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(intent, "Buka PDF dengan")

    try {
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Tidak ada aplikasi pembuka PDF", Toast.LENGTH_SHORT).show()
    }
}