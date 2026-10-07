package com.example.adr_nhom_da.data.model

data class OrderItem(
    val menuItemId: Long,
    val menuItemName: String,
    val price: Double,
    val quantity: Int,
    val note: String = ""
) {
    val totalPrice: Double
        get() = price * quantity
}

enum class OrderStatus {
    PENDING,
    COMPLETED
}

data class Order(
    val id: Long = 0,
    val tableId: Long,
    val tableName: String,
    val customerId: Long? = null,
    val customerName: String = "Khách vãng lai",
    val customerTierName: String = "Bậc Đồng",
    val employeeId: Long = 0,
    val employeeName: String = "Nhân viên",
    val items: List<OrderItem> = kotlin.collections.emptyList(),
    val subtotal: Double = 0.0,
    val discountPercent: Double = 0.0,
    val paymentMethod: String = "Tiền mặt", // Tiền mặt, VietQR
    val status: OrderStatus = OrderStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
) {
    val discountAmount: Double
        get() = subtotal * (discountPercent / 100.0)

    val finalTotal: Double
        get() = subtotal - discountAmount
}
