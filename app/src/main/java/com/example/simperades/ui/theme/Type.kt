package com.example.simperades.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import com.example.simperades.R


// Set of Material typography styles to start with

val Poppins = FontFamily(
    // Normal / Regular
    Font(R.font.poppins_regular, FontWeight.Normal),

    // Medium
    Font(R.font.poppins_medium, FontWeight.Medium),

    // Bold
    Font(R.font.poppins_bold, FontWeight.Bold)
    // Tambahkan weight lain sesuai kebutuhan Anda (misalnya: Light, SemiBold)
)


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

)