package com.example.simperades.ui.home.laporan

import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.example.simperades.ui.home.viewmodel.HomeViewModel
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import java.io.File
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun exportLaporanPDF(context: Context, viewModel: HomeViewModel): File? {
    return try {

        val listKeramba = viewModel.kerambaList.value

        if (listKeramba.isEmpty()) {
            Toast.makeText(context, "Tidak ada data keramba untuk diekspor", Toast.LENGTH_SHORT).show()
            return null
        }

        val totalIkan = listKeramba.sumOf { it.jumlahIkan }
        val totalMati = listKeramba.sumOf { it.jumlahMati }
        val tanggal = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id")))

        val fileName = "Laporan_Bulanan_${System.currentTimeMillis()}.pdf"

        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        }

        val outputStream: OutputStream = file.outputStream()
        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        // ===== HEADER =====
        document.add(
            Paragraph("LAPORAN BULANAN KERAMBA")
                .setBold()
                .setFontSize(18f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        document.add(Paragraph("Tanggal Laporan: $tanggal")
            .setTextAlignment(TextAlignment.CENTER))

        document.add(Paragraph("\n"))

        // ===== RINGKASAN =====
        val ringkasanTable = Table(floatArrayOf(1f, 1f)).useAllAvailableWidth()

        ringkasanTable.addHeaderCell("Total Ikan Hidup")
        ringkasanTable.addHeaderCell("Total Ikan Mati")

        ringkasanTable.addCell(totalIkan.toString())
        ringkasanTable.addCell(totalMati.toString())

        document.add(Paragraph("Ringkasan Data").setBold())
        document.add(ringkasanTable)

        document.add(Paragraph("\n"))

        // ===== TABEL DETAIL =====
        val table = Table(floatArrayOf(1f, 4f, 2f, 2f)).useAllAvailableWidth()

        table.addHeaderCell("No")
        table.addHeaderCell("Nama Keramba")
        table.addHeaderCell("Ikan Hidup")
        table.addHeaderCell("Ikan Mati")

        listKeramba.forEachIndexed { index, keramba ->
            table.addCell((index + 1).toString())
            table.addCell(keramba.nama) // ❌ lokasi dihapus
            table.addCell(keramba.jumlahIkan.toString())
            table.addCell(keramba.jumlahMati.toString())
        }

        document.add(Paragraph("Rincian Per Keramba").setBold())
        document.add(table)

        document.close()
        outputStream.close()

        Toast.makeText(context, "PDF berhasil disimpan ke Downloads", Toast.LENGTH_LONG).show()
        file

    } catch (e: Exception) {
        Toast.makeText(context, "Gagal membuat PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        null
    }
}fun exportLaporanHarianPDF(context: Context, viewModel: HomeViewModel): File? {
    return try {

        val laporanHarian = viewModel.getDailyDeathReport()

        if (laporanHarian.isEmpty()) {
            Toast.makeText(context, "Tidak ada data laporan harian", Toast.LENGTH_SHORT).show()
            return null
        }

        val fileName = "Laporan_Harian_${System.currentTimeMillis()}.pdf"

        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        }

        val outputStream: OutputStream = file.outputStream()
        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        document.add(
            Paragraph("LAPORAN HARIAN KEMATIAN IKAN")
                .setBold()
                .setFontSize(18f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        document.add(Paragraph("\n"))

        val table = Table(floatArrayOf(2f, 1f, 1f, 1f, 1f, 1f))
            .useAllAvailableWidth()

        table.addHeaderCell("Tanggal")
        table.addHeaderCell("Pagi")
        table.addHeaderCell("Siang")
        table.addHeaderCell("Sore")
        table.addHeaderCell("Malam")
        table.addHeaderCell("Total")

        laporanHarian.sortedByDescending { it.tanggal }
            .forEach { laporan ->

                table.addCell(laporan.tanggal)
                table.addCell(laporan.pagi.toString())
                table.addCell(laporan.siang.toString())
                table.addCell(laporan.sore.toString())
                table.addCell(laporan.malam.toString())
                table.addCell(laporan.total.toString())
            }

        document.add(table)

        document.close()
        outputStream.close()

        Toast.makeText(context, "Laporan Harian berhasil disimpan!", Toast.LENGTH_LONG).show()
        file

    } catch (e: Exception) {
        Toast.makeText(context, "Gagal membuat laporan: ${e.message}", Toast.LENGTH_SHORT).show()
        null
    }
}

