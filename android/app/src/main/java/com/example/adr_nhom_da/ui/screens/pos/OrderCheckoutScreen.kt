package com.example.adr_nhom_da.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.adr_nhom_da.data.model.*
import com.example.adr_nhom_da.data.repository.RestaurantRepository
import com.example.adr_nhom_da.ui.components.CustomerTierBadge
import com.example.adr_nhom_da.ui.components.formatVnd
import com.example.adr_nhom_da.ui.theme.PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCheckoutScreen(
    repository: RestaurantRepository,
    currentUser: User,
    initialTableId: Long? = null
) {
    val tables = remember { repository.getAllTables() }
    val menuItems = remember { repository.getAllMenuItems().filter { it.isAvailable } }
    val customers = remember { repository.getAllCustomers() }

    var selectedTable by remember {
        mutableStateOf(tables.find { it.id == initialTableId } ?: tables.firstOrNull())
    }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var orderItems by remember { mutableStateOf(mutableListOf<OrderItem>()) }
    var paymentMethod by remember { mutableStateOf("Tiền mặt") }
    var cashGivenInput by remember { mutableStateOf("") }

    var showAddItemDialog by remember { mutableStateOf(false) }
    var completedOrderForBill by remember { mutableStateOf<Order?>(null) }

    val subtotal = orderItems.sumOf { it.totalPrice }
    val discountPercent = selectedCustomer?.discountPercent ?: 0.0
    val discountAmount = subtotal * (discountPercent / 100.0)
    val finalTotal = subtotal - discountAmount

    val cashGiven = cashGivenInput.toDoubleOrNull() ?: 0.0
    val changeAmount = if (cashGiven >= finalTotal) cashGiven - finalTotal else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Gọi Món & Thanh Toán", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryRed)
        Spacer(modifier = Modifier.height(12.dp))

        // Table & Customer Selection Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Select Table Dropdown
            var tableExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = tableExpanded,
                onExpandedChange = { tableExpanded = !tableExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedTable?.name ?: "Chọn bàn",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Bàn ăn") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tableExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = tableExpanded,
                    onDismissRequest = { tableExpanded = false }
                ) {
                    tables.forEach { table ->
                        DropdownMenuItem(
                            text = { Text("${table.name} (${table.floor})") },
                            onClick = {
                                selectedTable = table
                                tableExpanded = false
                            }
                        )
                    }
                }
            }

            // Select Customer Dropdown (Tier Discount)
            var customerExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = customerExpanded,
                onExpandedChange = { customerExpanded = !customerExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedCustomer?.name ?: "Khách vãng lai",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Khách hàng") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = customerExpanded,
                    onDismissRequest = { customerExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Khách vãng lai (0%)") },
                        onClick = {
                            selectedCustomer = null
                            customerExpanded = false
                        }
                    )
                    customers.forEach { cust ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(cust.name)
                                    CustomerTierBadge(tier = cust.tier)
                                }
                            },
                            onClick = {
                                selectedCustomer = cust
                                customerExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Order Items List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Danh sách món gọi (${orderItems.size})", fontWeight = FontWeight.Bold)
            Button(
                onClick = { showAddItemDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Thêm Món")
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            items(orderItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.menuItemName, fontWeight = FontWeight.Bold)
                            Text("${formatVnd(item.price)} x ${item.quantity} = ${formatVnd(item.totalPrice)}", fontSize = 12.sp, color = Color.Gray)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                val currentList = orderItems.toMutableList()
                                val index = currentList.indexOf(item)
                                if (index != -1) {
                                    if (item.quantity > 1) {
                                        currentList[index] = item.copy(quantity = item.quantity - 1)
                                    } else {
                                        currentList.removeAt(index)
                                    }
                                    orderItems = currentList
                                }
                            }) {
                                Icon(Icons.Default.Remove, contentDescription = "Trừ")
                            }

                            Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))

                            IconButton(onClick = {
                                val currentList = orderItems.toMutableList()
                                val index = currentList.indexOf(item)
                                if (index != -1) {
                                    currentList[index] = item.copy(quantity = item.quantity + 1)
                                    orderItems = currentList
                                }
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Cộng")
                            }
                        }
                    }
                }
            }
        }

        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        // Billing Breakdown
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tạm tính:")
                Text(formatVnd(subtotal))
            }
            if (discountPercent > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Giảm giá (${selectedCustomer?.tier?.displayName} - ${discountPercent.toInt()}%):", color = PrimaryRed)
                    Text("-${formatVnd(discountAmount)}", color = PrimaryRed)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("TỔNG THANH TOÁN:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(formatVnd(finalTotal), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryRed)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Payment Method Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = paymentMethod == "Tiền mặt",
                    onClick = { paymentMethod = "Tiền mặt" },
                    label = { Text("💵 Tiền mặt") }
                )
                FilterChip(
                    selected = paymentMethod == "VietQR",
                    onClick = { paymentMethod = "VietQR" },
                    label = { Text("📱 VietQR") }
                )
            }

            if (paymentMethod == "Tiền mặt") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = cashGivenInput,
                        onValueChange = { cashGivenInput = it },
                        label = { Text("Tiền khách đưa") },
                        modifier = Modifier.weight(1f)
                    )
                    Text("Tiền thối: ${formatVnd(changeAmount)}", fontWeight = FontWeight.Bold, color = PrimaryRed)
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Text(
                        text = "Mã VietQR Chuyển Khoản: STK 1903888888 - MBBank (Chủ TK: NHA HANG AM THUC)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    selectedTable?.let { table ->
                        if (orderItems.isNotEmpty()) {
                            val order = Order(
                                tableId = table.id,
                                tableName = table.name,
                                customerId = selectedCustomer?.id,
                                customerName = selectedCustomer?.name ?: "Khách vãng lai",
                                customerTierName = selectedCustomer?.tier?.displayName ?: "Bậc Đồng",
                                employeeId = currentUser.id,
                                employeeName = currentUser.fullName,
                                items = orderItems,
                                subtotal = subtotal,
                                discountPercent = discountPercent,
                                paymentMethod = paymentMethod,
                                status = OrderStatus.COMPLETED
                            )
                            val orderId = repository.checkoutOrder(order)
                            completedOrderForBill = order.copy(id = orderId)
                            orderItems = mutableListOf()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                enabled = orderItems.isNotEmpty() && selectedTable != null
            ) {
                Text("THANH TOÁN & IN HÓA ĐƠN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Add Menu Item Modal Dialog
    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Chọn Món Ăn") },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(menuItems) { menu ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                val currentList = orderItems.toMutableList()
                                val existingIndex = currentList.indexOfFirst { it.menuItemId == menu.id }
                                if (existingIndex != -1) {
                                    val existing = currentList[existingIndex]
                                    currentList[existingIndex] = existing.copy(quantity = existing.quantity + 1)
                                } else {
                                    currentList.add(
                                        OrderItem(
                                            menuItemId = menu.id,
                                            menuItemName = menu.name,
                                            price = menu.price,
                                            quantity = 1
                                        )
                                    )
                                }
                                orderItems = currentList
                                showAddItemDialog = false
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(menu.name, fontWeight = FontWeight.Medium)
                                Text(formatVnd(menu.price), color = PrimaryRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddItemDialog = false }) { Text("Đóng") }
            }
        )
    }

    // Receipt Bill Dialog
    completedOrderForBill?.let { billOrder ->
        BillReceiptDialog(
            order = billOrder,
            onDismiss = { completedOrderForBill = null }
        )
    }
}
