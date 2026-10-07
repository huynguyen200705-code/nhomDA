package com.example.adr_nhom_da.ui.screens.employee

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adr_nhom_da.data.model.ContractType
import com.example.adr_nhom_da.data.model.Employee
import com.example.adr_nhom_da.ui.components.EmployeeTierBadge
import com.example.adr_nhom_da.ui.components.formatVnd
import com.example.adr_nhom_da.ui.theme.PrimaryRed

@Composable
fun PayslipDialog(
    employee: Employee,
    onDismiss: () -> Unit
) {
    val totalSalary = employee.calculateSalary()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("PHIẾU LƯƠNG CHI TIẾT", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryRed)
                Text(employee.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${employee.position} • ${employee.phone}", fontSize = 12.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cấp bậc:")
                    EmployeeTierBadge(tier = employee.tier)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text("Công thức & Chi tiết lương:", fontWeight = FontWeight.Bold)

                if (employee.contractType == ContractType.HOURLY) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Lương giờ (${formatVnd(employee.hourlyRate)}/h):")
                        Text("${employee.workedHours} giờ")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Thành tiền gốc:")
                        Text(formatVnd(employee.hourlyRate * employee.workedHours))
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Lương cứng tháng:")
                        Text(formatVnd(employee.monthlySalary))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ngày công thực tế:")
                        Text("${employee.workedDays} / 26 ngày")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Thành tiền gốc:")
                        Text(formatVnd(employee.monthlySalary * (employee.workedDays / 26.0)))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tiền thưởng (+):", color = PrimaryRed)
                    Text("+${formatVnd(employee.bonus)}", color = PrimaryRed)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kỷ luật/Phạt (-):")
                    Text("-${formatVnd(employee.penalty)}")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TỔNG LƯƠNG THỰC LĨNH:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(formatVnd(totalSalary), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryRed)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
            ) {
                Text("Đóng")
            }
        }
    )
}
