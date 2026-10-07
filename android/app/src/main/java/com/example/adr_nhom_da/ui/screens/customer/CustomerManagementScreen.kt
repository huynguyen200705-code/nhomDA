package com.example.adr_nhom_da.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.adr_nhom_da.data.model.Customer
import com.example.adr_nhom_da.data.model.CustomerTier
import com.example.adr_nhom_da.data.repository.RestaurantRepository
import com.example.adr_nhom_da.ui.components.ConfirmDeleteDialog
import com.example.adr_nhom_da.ui.components.CustomerTierBadge
import com.example.adr_nhom_da.ui.components.formatVnd
import com.example.adr_nhom_da.ui.theme.PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerManagementScreen(
    repository: RestaurantRepository
) {
    var customers by remember { mutableStateOf(repository.getAllCustomers()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTierFilter by remember { mutableStateOf<CustomerTier?>(null) }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var customerToEdit by remember { mutableStateOf<Customer?>(null) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    fun refreshData() {
        customers = repository.getAllCustomers()
    }

    val filteredList = customers.filter { cust ->
        val matchesSearch = cust.name.contains(searchQuery, ignoreCase = true) || cust.phone.contains(searchQuery)
        val matchesTier = selectedTierFilter == null || cust.tier == selectedTierFilter
        matchesSearch && matchesTier
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản Lý 5 Bậc Khách Hàng", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    customerToEdit = null
                    showAddEditDialog = true
                },
                containerColor = PrimaryRed
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm khách hàng", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Tìm kiếm theo tên hoặc SĐT...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5 Tier Filter Chips
            ScrollableTabRow(
                selectedTabIndex = if (selectedTierFilter == null) 0 else CustomerTier.values().indexOf(selectedTierFilter) + 1,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTierFilter == null,
                    onClick = { selectedTierFilter = null },
                    text = { Text("Tất cả (${customers.size})") }
                )
                CustomerTier.values().forEach { tier ->
                    Tab(
                        selected = selectedTierFilter == tier,
                        onClick = { selectedTierFilter = tier },
                        text = { Text("${tier.displayName} (-${tier.discountPercent.toInt()}%)") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Customer List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { cust ->
                    CustomerCard(
                        customer = cust,
                        onEdit = {
                            customerToEdit = cust
                            showAddEditDialog = true
                        },
                        onDelete = {
                            customerToDelete = cust
                        }
                    )
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        var name by remember { mutableStateOf(customerToEdit?.name ?: "") }
        var phone by remember { mutableStateOf(customerToEdit?.phone ?: "") }
        var email by remember { mutableStateOf(customerToEdit?.email ?: "") }
        var spentText by remember { mutableStateOf(customerToEdit?.totalSpent?.toInt()?.toString() ?: "0") }

        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = { Text(if (customerToEdit == null) "Thêm Khách Hàng Mới" else "Cập Nhật Khách Hàng") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = spentText,
                        onValueChange = { spentText = it },
                        label = { Text("Tổng chi tiêu tích lũy (VNĐ)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            val spentVal = spentText.toDoubleOrNull() ?: 0.0
                            val pointsVal = (spentVal / 100000.0).toInt()
                            if (customerToEdit == null) {
                                repository.addCustomer(
                                    Customer(
                                        name = name,
                                        phone = phone,
                                        email = email,
                                        totalSpent = spentVal,
                                        loyaltyPoints = pointsVal
                                    )
                                )
                            } else {
                                repository.updateCustomer(
                                    customerToEdit!!.copy(
                                        name = name,
                                        phone = phone,
                                        email = email,
                                        totalSpent = spentVal,
                                        loyaltyPoints = pointsVal
                                    )
                                )
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
    customerToDelete?.let { cust ->
        ConfirmDeleteDialog(
            message = "Bạn có chắc chắn muốn xóa khách hàng '${cust.name}' không?",
            onConfirm = {
                repository.deleteCustomer(cust.id)
                refreshData()
                customerToDelete = null
            },
            onDismiss = { customerToDelete = null }
        )
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val nextTier = CustomerTier.getNextTier(customer.tier)
    val progress = if (nextTier != null) {
        (customer.totalSpent / nextTier.minSpend).coerceIn(0.0, 1.0).toFloat()
    } else 1.0f

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
                        Text(customer.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        CustomerTierBadge(tier = customer.tier)
                    }
                    Text("SĐT: ${customer.phone} • Points: ${customer.loyaltyPoints} điểm", fontSize = 12.sp, color = Color.Gray)
                    Text("Tổng chi tiêu: ${formatVnd(customer.totalSpent)}", fontWeight = FontWeight.Medium, color = PrimaryRed)
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color.Gray)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = PrimaryRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tier Progress Bar
            if (nextTier != null) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Thăng hạng ${nextTier.displayName}:", fontSize = 11.sp, color = Color.Gray)
                        Text("${formatVnd(customer.totalSpent)} / ${formatVnd(nextTier.minSpend)}", fontSize = 11.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = PrimaryRed,
                        trackColor = Color.LightGray
                    )
                }
            }
        }
    }
}
