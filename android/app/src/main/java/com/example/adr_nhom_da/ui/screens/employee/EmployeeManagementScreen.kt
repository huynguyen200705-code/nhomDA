package com.example.adr_nhom_da.ui.screens.employee

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.adr_nhom_da.data.model.ContractType
import com.example.adr_nhom_da.data.model.Employee
import com.example.adr_nhom_da.data.model.EmployeeTier
import com.example.adr_nhom_da.data.repository.RestaurantRepository
import com.example.adr_nhom_da.ui.components.ConfirmDeleteDialog
import com.example.adr_nhom_da.ui.components.EmployeeTierBadge
import com.example.adr_nhom_da.ui.components.formatVnd
import com.example.adr_nhom_da.ui.theme.PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManagementScreen(
    repository: RestaurantRepository
) {
    var employees by remember { mutableStateOf(repository.getAllEmployees()) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var employeeToEdit by remember { mutableStateOf<Employee?>(null) }
    var employeeToDelete by remember { mutableStateOf<Employee?>(null) }
    var selectedEmployeeForPayslip by remember { mutableStateOf<Employee?>(null) }

    fun refreshData() {
        employees = repository.getAllEmployees()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản Lý Nhân Sự & Tính Lương", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    employeeToEdit = null
                    showAddEditDialog = true
                },
                containerColor = PrimaryRed
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm nhân viên", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(employees) { emp ->
                EmployeeCard(
                    employee = emp,
                    onEdit = {
                        employeeToEdit = emp
                        showAddEditDialog = true
                    },
                    onDelete = {
                        employeeToDelete = emp
                    },
                    onViewPayslip = {
                        selectedEmployeeForPayslip = emp
                    }
                )
            }
        }
    }

    // Add / Edit Employee Modal
    if (showAddEditDialog) {
        var name by remember { mutableStateOf(employeeToEdit?.name ?: "") }
        var phone by remember { mutableStateOf(employeeToEdit?.phone ?: "") }
        var position by remember { mutableStateOf(employeeToEdit?.position ?: "Phục vụ") }
        var selectedTier by remember { mutableStateOf(employeeToEdit?.tier ?: EmployeeTier.CHINH_THUC) }
        var selectedContract by remember { mutableStateOf(employeeToEdit?.contractType ?: ContractType.MONTHLY) }

        var hourlyRateText by remember { mutableStateOf(employeeToEdit?.hourlyRate?.toInt()?.toString() ?: "25000") }
        var monthlySalaryText by remember { mutableStateOf(employeeToEdit?.monthlySalary?.toInt()?.toString() ?: "8000000") }
        var workedHoursText by remember { mutableStateOf(employeeToEdit?.workedHours?.toInt()?.toString() ?: "0") }
        var workedDaysText by remember { mutableStateOf(employeeToEdit?.workedDays?.toInt()?.toString() ?: "0") }
        var bonusText by remember { mutableStateOf(employeeToEdit?.bonus?.toInt()?.toString() ?: "0") }
        var penaltyText by remember { mutableStateOf(employeeToEdit?.penalty?.toInt()?.toString() ?: "0") }

        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = { Text(if (employeeToEdit == null) "Thêm Nhân Viên Mới" else "Cập Nhật Nhân Viên") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = position,
                        onValueChange = { position = it },
                        label = { Text("Vị trí (VD: Phục vụ, Thu ngân, Bếp)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Loại hợp đồng:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedContract == ContractType.HOURLY,
                            onClick = { selectedContract = ContractType.HOURLY },
                            label = { Text("Theo Giờ") }
                        )
                        FilterChip(
                            selected = selectedContract == ContractType.MONTHLY,
                            onClick = { selectedContract = ContractType.MONTHLY },
                            label = { Text("Theo Tháng") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedContract == ContractType.HOURLY) {
                        OutlinedTextField(
                            value = hourlyRateText,
                            onValueChange = { hourlyRateText = it },
                            label = { Text("Lương/giờ (VNĐ)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = workedHoursText,
                            onValueChange = { workedHoursText = it },
                            label = { Text("Số giờ làm việc") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = monthlySalaryText,
                            onValueChange = { monthlySalaryText = it },
                            label = { Text("Lương cứng/tháng (VNĐ)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = workedDaysText,
                            onValueChange = { workedDaysText = it },
                            label = { Text("Số ngày công (/26 ngày)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bonusText,
                            onValueChange = { bonusText = it },
                            label = { Text("Thưởng") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = penaltyText,
                            onValueChange = { penaltyText = it },
                            label = { Text("Phạt") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val newEmp = Employee(
                                id = employeeToEdit?.id ?: 0,
                                name = name,
                                phone = phone,
                                position = position,
                                tier = selectedTier,
                                contractType = selectedContract,
                                hourlyRate = hourlyRateText.toDoubleOrNull() ?: 25000.0,
                                monthlySalary = monthlySalaryText.toDoubleOrNull() ?: 8000000.0,
                                workedHours = workedHoursText.toDoubleOrNull() ?: 0.0,
                                workedDays = workedDaysText.toDoubleOrNull() ?: 0.0,
                                bonus = bonusText.toDoubleOrNull() ?: 0.0,
                                penalty = penaltyText.toDoubleOrNull() ?: 0.0
                            )
                            if (employeeToEdit == null) {
                                repository.addEmployee(newEmp)
                            } else {
                                repository.updateEmployee(newEmp)
                            }
                            refreshData()
                            showAddEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEditDialog = false }) { Text("Hủy") }
            }
        )
    }

    // Confirm Delete Dialog
    employeeToDelete?.let { emp ->
        ConfirmDeleteDialog(
            message = "Bạn có muốn xóa nhân viên '${emp.name}'?",
            onConfirm = {
                repository.deleteEmployee(emp.id)
                refreshData()
                employeeToDelete = null
            },
            onDismiss = { employeeToDelete = null }
        )
    }

    // Payslip Dialog
    selectedEmployeeForPayslip?.let { emp ->
        PayslipDialog(
            employee = emp,
            onDismiss = { selectedEmployeeForPayslip = null }
        )
    }
}

@Composable
fun EmployeeCard(
    employee: Employee,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewPayslip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(employee.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        EmployeeTierBadge(tier = employee.tier)
                    }
                    Text("${employee.position} • SĐT: ${employee.phone}", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "Hợp đồng: ${employee.contractType.displayName} • Lương dự kiến: ${formatVnd(employee.calculateSalary())}",
                        fontWeight = FontWeight.Medium,
                        color = PrimaryRed,
                        fontSize = 13.sp
                    )
                }

                Row {
                    IconButton(onClick = onViewPayslip) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = "Phiếu lương", tint = PrimaryRed)
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color.Gray)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = PrimaryRed)
                    }
                }
            }
        }
    }
}
