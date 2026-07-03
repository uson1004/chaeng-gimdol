package com.yuseob.chaenggimdol.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    displaySmall = TextStyle(
        fontSize = 30.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Black,
    ),
    headlineSmall = TextStyle(
        fontSize = 24.sp,
        lineHeight = 29.sp,
        fontWeight = FontWeight.ExtraBold,
    ),
    titleMedium = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.ExtraBold,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 23.sp,
        fontWeight = FontWeight.Medium,
    ),
    labelLarge = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Bold,
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.Medium,
    ),
)
