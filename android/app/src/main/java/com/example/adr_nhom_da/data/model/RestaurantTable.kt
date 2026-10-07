package com.example.adr_nhom_da.data.model

enum class TableStatus(val displayName: String) {
    TRONG("Trống"),
    DANG_PHUC_VU("Đang phục vụ"),
    DA_DAT("Đã đặt")
}

data class RestaurantTable(
    val id: Long = 0,
    val name: String,
    val floor: String = "Tầng 1", // Tầng 1, Tầng 2, Phòng VIP
    val seats: Int = 4,
    val status: TableStatus = TableStatus.TRONG,
    val currentOrderId: Long? = null
)
