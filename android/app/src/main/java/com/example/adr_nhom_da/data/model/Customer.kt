package com.example.adr_nhom_da.data.model

enum class CustomerTier(val displayName: String, val discountPercent: Double, val minSpend: Double) {
    DONG("Bậc Đồng", 0.0, 0.0),
    BAC("Bậc Bạc", 5.0, 5000000.0),
    VANG("Bậc Vàng", 10.0, 15000000.0),
    BACH_KIM("Bậc Bạch Kim", 15.0, 30000000.0),
    KIM_CUONG("Bậc Kim Cương", 20.0, 50000000.0);

    companion object {
        fun getTierFromTotalSpent(spent: Double): CustomerTier {
            return when {
                spent >= KIM_CUONG.minSpend -> KIM_CUONG
                spent >= BACH_KIM.minSpend -> BACH_KIM
                spent >= VANG.minSpend -> VANG
                spent >= BAC.minSpend -> BAC
                else -> DONG
            }
        }

        fun getNextTier(currentTier: CustomerTier): CustomerTier? {
            return when (currentTier) {
                DONG -> BAC
                BAC -> VANG
                VANG -> BACH_KIM
                BACH_KIM -> KIM_CUONG
                KIM_CUONG -> null
            }
        }
    }
}

data class Customer(
    val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val totalSpent: Double = 0.0,
    val loyaltyPoints: Int = 0
) {
    val tier: CustomerTier
        get() = CustomerTier.getTierFromTotalSpent(totalSpent)

    val discountPercent: Double
        get() = tier.discountPercent
}
