package com.example.adr_nhom_da.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.adr_nhom_da.data.model.CustomerTier
import com.example.adr_nhom_da.data.model.EmployeeTier
import com.example.adr_nhom_da.data.model.TableStatus
import com.example.adr_nhom_da.ui.theme.*

@Composable
fun CustomerTierBadge(tier: CustomerTier) {
    val (bgColor, textColor) = when (tier) {
        CustomerTier.DONG -> TierDong to Color.White
        CustomerTier.BAC -> TierBac to Color.White
        CustomerTier.VANG -> TierVang to Color.Black
        CustomerTier.BACH_KIM -> TierBachKim to Color.White
        CustomerTier.KIM_CUONG -> TierKimCuong to Color.White
    }

    Text(
        text = "${tier.displayName} (-${tier.discountPercent.toInt()}%)",
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun EmployeeTierBadge(tier: EmployeeTier) {
    val bgColor = when (tier) {
        EmployeeTier.THU_VIEC -> Color.Gray
        EmployeeTier.CHINH_THUC -> PrimaryRed
        EmployeeTier.QUAN_LY -> SecondaryDarkGold
        EmployeeTier.GIAM_DOC -> TierKimCuong
    }

    Text(
        text = tier.displayName,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(bgColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun TableStatusBadge(status: TableStatus) {
    val bgColor = when (status) {
        TableStatus.TRONG -> TableStatusTrong
        TableStatus.DANG_PHUC_VU -> TableStatusPhucVu
        TableStatus.DA_DAT -> TableStatusDaDat
    }

    Text(
        text = status.displayName,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(bgColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
