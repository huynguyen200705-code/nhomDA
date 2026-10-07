package com.example.adr_nhom_da.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryRed,
    secondary = SecondaryGold,
    background = BackgroundLight,
    surface = SurfaceLight
)

@Composable
fun AdrNhomDaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
