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
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
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
        val tanggal = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id")))
        val fileName = "laporan_bulanan_${System.currentTimeMillis()}.pdf"

        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        }

        val outputStream: OutputStream = file.outputStream()
        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        document.add(Paragraph("LAPORAN BULANAN KERAMBA").setBold().setFontSize(16f))
        document.add(Paragraph("Tanggal Laporan: $tanggal"))
        document.add(Paragraph("\nRingkasan Data:"))
        document.add(Paragraph("• Total Ikan Hidup: $totalIkan"))
        document.add(Paragraph("• Total Ikan Mati : $totalMati"))
        document.add(Paragraph("\nRincian Per Keramba:"))

        listKeramba.forEachIndexed { index, it ->
            document.add(
                Paragraph("${index + 1}. ${it.nama} (${it.lokasi}) - Hidup: ${it.jumlahIkan}, Mati: ${it.jumlahMati}")
            )
        }

        document.close()
        outputStream.close()

        Toast.makeText(context, "PDF berhasil disimpan ke folder Downloads", Toast.LENGTH_LONG).show()
        file
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal membuat PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        null
    }
}

/**
 * Export Laporan Harian Kematian Ikan ke PDF
 * Menampilkan data kematian per hari dengan breakdown waktu (pagi, siang, sore, malam)
 */
fun exportLaporanHarianPDF(context: Context, viewModel: HomeViewModel): File? {
    return try {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale("id")).format(System.currentTimeMillis())
        val fileName = "LaporanHarian_$timeStamp.pdf"

        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        }

        val laporanHarian = viewModel.getDailyDeathReport()

        if (laporanHarian.isEmpty()) {
            Toast.makeText(context, "Tidak ada data laporan harian untuk diekspor", Toast.LENGTH_SHORT).show()
            return null
        }

        val outputStream: OutputStream = file.outputStream()
        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        // Header
        document.add(Paragraph("LAPORAN HARIAN KEMATIAN IKAN KERAMBA").setBold().setFontSize(16f))
        document.add(Paragraph("Tanggal Cetak: ${dateFormat.format(System.currentTimeMillis())}"))
        document.add(Paragraph("\n"))

        // Summary
        val totalSemuaMati = laporanHarian.sumOf { it.total }
        document.add(Paragraph("Total Kematian Keseluruhan: $totalSemuaMati ekor").setBold())
        document.add(Paragraph("\nRincian Kematian Ikan per Hari:\n"))

        // Detail per hari
        laporanHarian.sortedByDescending { it.tanggal }.forEach { laporan ->
            // Parse tanggal dengan format yang sesuai
            val tanggalFormatted = try {
                LocalDate.parse(laporan.tanggal, DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    .format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id")))
            } catch (e: Exception) {
                laporan.tanggal
            }

            document.add(Paragraph("═══════════════════════════════════════"))
            document.add(Paragraph("Tanggal: $tanggalFormatted").setBold().setFontSize(12f))
            document.add(Paragraph("\nKematian Ikan per Waktu:"))
            document.add(Paragraph("  • Pagi  : ${laporan.pagi} ekor"))
            document.add(Paragraph("  • Siang : ${laporan.siang} ekor"))
            document.add(Paragraph("  • Sore  : ${laporan.sore} ekor"))
            document.add(Paragraph("  • Malam : ${laporan.malam} ekor"))
            document.add(Paragraph("\nTotal Kematian Hari Ini: ${laporan.total} ekor").setBold())
            document.add(Paragraph("\n"))
        }

        document.add(Paragraph("═══════════════════════════════════════"))
        document.add(Paragraph("\nLaporan ini dicetak secara otomatis oleh sistem Simperades"))

        document.close()
        outputStream.close()

        Toast.makeText(context, "Laporan Harian berhasil disimpan ke Downloads!", Toast.LENGTH_LONG).show()
        file

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Gagal membuat Laporan Harian: ${e.message}", Toast.LENGTH_SHORT).show()
        null
    }
}

///**
// * Export Laporan Harian untuk tanggal tertentu saja
// */
//fun exportLaporanHarianByDate(
//    context: Context,
//    viewModel: HomeViewModel,
//    targetDate: String
//): File? {
//    return try {
//        android.util.Log.d("ExportDebug", "=== START EXPORT ===")
//        android.util.Log.d("ExportDebug", "Target date: '$targetDate'")
//
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale("id"))
//            .format(System.currentTimeMillis())
//        val fileName = "LaporanHarian_${targetDate.replace(" ", "_")}_$timeStamp.pdf"
//
//        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
//        } else {
//            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
//        }
//
//        val allLaporanHarian = viewModel.getDailyDeathReport()
//
//        android.util.Log.d("ExportDebug", "Total data available: ${allLaporanHarian?.size ?: 0}")
//
//        if (allLaporanHarian.isNullOrEmpty()) {
//            Toast.makeText(context, "Tidak ada data laporan harian", Toast.LENGTH_SHORT).show()
//            return null
//        }
//
//        // PERBAIKAN: Bandingkan hanya 10 karakter pertama (bukan 11)
//        val laporanHarian = allLaporanHarian.filter { laporan ->
//            val laporanDate = laporan.tanggal.trim()
//            val targetShort = targetDate.trim()
//
//            // Ambil 10 karakter pertama dari masing-masing
//            val laporanFirst10 = if (laporanDate.length >= 10) laporanDate.substring(0, 10) else laporanDate
//            val targetFirst10 = if (targetShort.length >= 10) targetShort.substring(0, 10) else targetShort
//
//            val match = laporanFirst10 == targetFirst10
//
//            android.util.Log.d("ExportDebug", "Laporan: '$laporanFirst10' (len=${laporanFirst10.length})")
//            android.util.Log.d("ExportDebug", "Target : '$targetFirst10' (len=${targetFirst10.length})")
//            android.util.Log.d("ExportDebug", "Match  : $match")
//            android.util.Log.d("ExportDebug", "---")
//
//            match
//        }
//
//        if (laporanHarian.isEmpty()) {
//            Toast.makeText(context, "Tidak ada data untuk tanggal $targetDate", Toast.LENGTH_SHORT).show()
//            return null
//        }
//
//        val laporan = laporanHarian.first()
//        val outputStream: OutputStream = file.outputStream()
//        val writer = PdfWriter(outputStream)
//        val pdf = PdfDocument(writer)
//        val document = Document(pdf)
//
//        document.add(Paragraph("LAPORAN HARIAN KEMATIAN IKAN").setBold().setFontSize(16f))
//        document.add(Paragraph("Tanggal: $targetDate"))
//        document.add(
//            Paragraph(
//                "Dicetak: ${SimpleDateFormat("dd MMMM yyyy", Locale("id"))
//                    .format(System.currentTimeMillis())}"
//            )
//        )
//        document.add(Paragraph("\n"))
//
//        document.add(Paragraph("Kematian Ikan per Waktu:").setBold())
//        document.add(Paragraph("  • Pagi  : ${laporan.pagi} ekor"))
//        document.add(Paragraph("  • Siang : ${laporan.siang} ekor"))
//        document.add(Paragraph("  • Sore  : ${laporan.sore} ekor"))
//        document.add(Paragraph("  • Malam : ${laporan.malam} ekor"))
//        document.add(Paragraph("\n"))
//        document.add(Paragraph("Total Kematian: ${laporan.total} ekor").setBold().setFontSize(14f))
//
//        document.close()
//        outputStream.close()
//
//        Toast.makeText(context, "Laporan tanggal $targetDate berhasil disimpan!", Toast.LENGTH_LONG).show()
//        file
//
//    } catch (e: Exception) {
//        android.util.Log.e("ExportPDF", "Fatal error: ${e.message}", e)
//        e.printStackTrace()
//        Toast.makeText(context, "Gagal membuat laporan: ${e.message}", Toast.LENGTH_SHORT).show()
//        null
//    }
//}
//```
//
//**Perubahan:**
//- Ganti dari `substring(0, 11)` menjadi `substring(0, 10)`
//- Ganti variabel dari `First11` menjadi `First10`
//
//Sekarang harusnya match! Log akan tampil:
//```
//Laporan: '07 Nov 202' (len=10)
//Target : '07 Nov 202' (len=10)
//Match  : true  ✅

