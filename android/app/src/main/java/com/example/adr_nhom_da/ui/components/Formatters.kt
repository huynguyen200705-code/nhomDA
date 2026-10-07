package com.example.adr_nhom_da.ui.components

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatVnd(amount: Double): String {
    val formatter = DecimalFormat("#,### ₫")
    return formatter.format(amount)
}

fun formatDateTime(timestamp: Long): String {
    if (timestamp <= 0) return "N/A"
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
