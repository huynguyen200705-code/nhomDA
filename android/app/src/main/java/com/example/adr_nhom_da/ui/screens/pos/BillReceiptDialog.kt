package com.example.adr_nhom_da.ui.screens.pos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adr_nhom_da.data.model.Order
import com.example.adr_nhom_da.ui.components.formatDateTime
import com.example.adr_nhom_da.ui.components.formatVnd
import com.example.adr_nhom_da.ui.theme.PrimaryRed

@Composable
fun BillReceiptDialog(
    order: Order,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("NHÀ HÀNG ÂM THỰC", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PrimaryRed)
                Text("HÓA ĐƠN THANH TOÁN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Mã HĐ: #${order.id} • ${formatDateTime(order.timestamp)}", fontSize = 12.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text("Bàn: ${order.tableName}", fontWeight = FontWeight.Bold)
                Text("Khách hàng: ${order.customerName} (${order.customerTierName})")
                Text("Thu ngân: ${order.employeeName}")

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text("Chi tiết đơn hàng:", fontWeight = FontWeight.Bold)
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(order.items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.menuItemName} (x${item.quantity})", modifier = Modifier.weight(1f))
                            Text(formatVnd(item.totalPrice))
                        }
                    }
                }

                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tạm tính:")
                    Text(formatVnd(order.subtotal))
                }
                if (order.discountPercent > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Chiết khấu (${order.customerTierName}):", color = PrimaryRed)
                        Text("-${formatVnd(order.discountAmount)}", color = PrimaryRed)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Thành tiền:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(formatVnd(order.finalTotal), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryRed)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Hình thức thanh toán:")
                    Text(order.paymentMethod, fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
            ) {
                Text("Đóng & Hoàn Tất")
            }
        }
    )
}
