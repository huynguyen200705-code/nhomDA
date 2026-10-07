package com.example.adr_nhom_da.ui.screens.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.adr_nhom_da.data.model.MenuItem
import com.example.adr_nhom_da.data.repository.RestaurantRepository
import com.example.adr_nhom_da.ui.components.ConfirmDeleteDialog
import com.example.adr_nhom_da.ui.components.formatVnd
import com.example.adr_nhom_da.ui.theme.PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen(
    repository: RestaurantRepository,
    isAdmin: Boolean = true
) {
    var menuList by remember { mutableStateOf(repository.getAllMenuItems()) }
    var selectedCategory by remember { mutableStateOf("Tất cả") }
    val categories = listOf("Tất cả", "Khai vị", "Món chính", "Đồ uống", "Tráng miệng")

    var showAddEditDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<MenuItem?>(null) }
    var itemToDelete by remember { mutableStateOf<MenuItem?>(null) }

    fun refreshData() {
        menuList = repository.getAllMenuItems()
    }

    val filteredList = if (selectedCategory == "Tất cả") menuList else menuList.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản Lý Thực Đơn Nhà Hàng", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = {
                        itemToEdit = null
                        showAddEditDialog = true
                    },
                    containerColor = PrimaryRed
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm món", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { cat ->
                    Tab(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        text = { Text(cat) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Menu Item List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { item ->
                    MenuItemCard(
                        item = item,
                        isAdmin = isAdmin,
                        onToggleAvailable = { isAvailable ->
                            repository.updateMenuItem(item.copy(isAvailable = isAvailable))
                            refreshData()
                        },
                        onEdit = {
                            itemToEdit = item
                            showAddEditDialog = true
                        },
                        onDelete = {
                            itemToDelete = item
                        }
                    )
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
        var category by remember { mutableStateOf(itemToEdit?.category ?: "Món chính") }
        var priceText by remember { mutableStateOf(itemToEdit?.price?.toInt()?.toString() ?: "50000") }
        var desc by remember { mutableStateOf(itemToEdit?.description ?: "") }

        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = { Text(if (itemToEdit == null) "Thêm Món Ăn Mới" else "Cập Nhật Món Ăn") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên món ăn") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Danh mục (Khai vị, Món chính, Đồ uống...)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Đơn giá (VNĐ)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Mô tả / Ghi chú") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val newItem = MenuItem(
                                id = itemToEdit?.id ?: 0,
                                name = name,
                                category = category,
                                price = priceText.toDoubleOrNull() ?: 0.0,
                                description = desc,
                                isAvailable = itemToEdit?.isAvailable ?: true
                            )
                            if (itemToEdit == null) {
                                repository.addMenuItem(newItem)
                            } else {
                                repository.updateMenuItem(newItem)
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
    itemToDelete?.let { item ->
        ConfirmDeleteDialog(
            message = "Bạn có chắc chắn muốn xóa món '${item.name}' khỏi thực đơn?",
            onConfirm = {
                repository.deleteMenuItem(item.id)
                refreshData()
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    isAdmin: Boolean,
    onToggleAvailable: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    AssistChip(
                        onClick = {},
                        label = { Text(item.category, fontSize = 11.sp) }
                    )
                }
                if (item.description.isNotBlank()) {
                    Text(item.description, fontSize = 12.sp, color = Color.Gray)
                }
                Text(formatVnd(item.price), fontWeight = FontWeight.Bold, color = PrimaryRed, fontSize = 15.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Switch available / out-of-stock
                Switch(
                    checked = item.isAvailable,
                    onCheckedChange = onToggleAvailable
                )

                if (isAdmin) {
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
