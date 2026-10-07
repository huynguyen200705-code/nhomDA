package com.example.adr_nhom_da.data.model

data class MenuItem(
    val id: Long = 0,
    val name: String,
    val category: String, // Khai vị, Món chính, Đồ uống, Tráng miệng
    val price: Double,
    val description: String = "",
    val isAvailable: Boolean = true
)
