package com.example.adr_nhom_da.ui.screens.pos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.adr_nhom_da.data.model.RestaurantTable
import com.example.adr_nhom_da.data.model.TableStatus
import com.example.adr_nhom_da.data.repository.RestaurantRepository
import com.example.adr_nhom_da.ui.components.TableStatusBadge
import com.example.adr_nhom_da.ui.theme.PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableManagementScreen(
    repository: RestaurantRepository,
    onOpenOrder: (RestaurantTable) -> Unit
) {
    var tables by remember { mutableStateOf(repository.getAllTables()) }
    var selectedFloor by remember { mutableStateOf("Tất cả") }
    val floors = listOf("Tất cả", "Tầng 1", "Tầng 2", "Phòng VIP")

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTableForAction by remember { mutableStateOf<RestaurantTable?>(null) }

    fun refreshData() {
        tables = repository.getAllTables()
    }

    val filteredTables = if (selectedFloor == "Tất cả") tables else tables.filter { it.floor == selectedFloor }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sơ Đồ Bàn Ăn Nhà Hàng", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryRed
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm bàn", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Filter Floor Tabs
            ScrollableTabRow(
                selectedTabIndex = floors.indexOf(selectedFloor),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                floors.forEachIndexed { index, floor ->
                    Tab(
                        selected = selectedFloor == floor,
                        onClick = { selectedFloor = floor },
                        text = { Text(floor, fontWeight = if (selectedFloor == floor) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tables Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTables) { table ->
                    TableCard(
                        table = table,
                        onClick = {
                            if (table.status == TableStatus.TRONG || table.status == TableStatus.DANG_PHUC_VU) {
                                onOpenOrder(table)
                            } else {
                                selectedTableForAction = table
                            }
                        }
                    )
                }
            }
        }
    }

    // Add Table Dialog
    if (showAddDialog) {
        var tableName by remember { mutableStateOf("") }
        var floor by remember { mutableStateOf("Tầng 1") }
        var seats by remember { mutableStateOf("4") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Thêm Bàn Ăn Mới") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tableName,
                        onValueChange = { tableName = it },
                        label = { Text("Tên bàn (VD: Bàn 104)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = floor,
                        onValueChange = { floor = it },
                        label = { Text("Khu vực / Tầng") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = seats,
                        onValueChange = { seats = it },
                        label = { Text("Số ghế") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tableName.isNotBlank()) {
                            repository.addTable(
                                RestaurantTable(
                                    name = tableName,
                                    floor = floor,
                                    seats = seats.toIntOrNull() ?: 4
                                )
                            )
                            refreshData()
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Hủy") }
            }
        )
    }

    // Status Action Dialog for Reserved Table
    selectedTableForAction?.let { table ->
        AlertDialog(
            onDismissRequest = { selectedTableForAction = null },
            title = { Text("Cập nhật trạng thái: ${table.name}") },
            text = { Text("Đổi trạng thái bàn thành 'Đang phục vụ' hoặc 'Trống'?") },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updateTableStatus(table.id, TableStatus.DANG_PHUC_VU)
                        refreshData()
                        selectedTableForAction = null
                        onOpenOrder(table)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Text("Mở Bàn Phục Vụ")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        repository.updateTableStatus(table.id, TableStatus.TRONG)
                        refreshData()
                        selectedTableForAction = null
                    }
                ) {
                    Text("Hủy Đặt / Trả Bàn Trống")
                }
            }
        )
    }
}

@Composable
fun TableCard(table: RestaurantTable, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.TableRestaurant,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = PrimaryRed
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(table.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("${table.floor} • ${table.seats} ghế", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            TableStatusBadge(status = table.status)
        }
    }
}
