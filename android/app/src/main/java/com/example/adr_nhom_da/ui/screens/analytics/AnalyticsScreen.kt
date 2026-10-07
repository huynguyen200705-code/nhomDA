package com.example.adr_nhom_da.ui.screens.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.adr_nhom_da.data.model.CustomerTier
import com.example.adr_nhom_da.data.repository.RestaurantRepository
import com.example.adr_nhom_da.ui.components.CustomerTierBadge
import com.example.adr_nhom_da.ui.components.formatVnd
import com.example.adr_nhom_da.ui.theme.PrimaryRed
import com.example.adr_nhom_da.ui.theme.SecondaryDarkGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    repository: RestaurantRepository
) {
    val orders = remember { repository.getAllCompletedOrders() }
    val customers = remember { repository.getAllCustomers() }
    val employees = remember { repository.getAllEmployees() }

    val totalRevenue = orders.sumOf { it.finalTotal }
    val totalOrdersCount = orders.size

    val totalSalaryBudget = employees.sumOf { it.calculateSalary() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống Kê Doanh Thu & Báo Cáo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Revenue & Order KPI Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = PrimaryRed.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Tổng Doanh Thu", fontSize = 12.sp, color = PrimaryRed)
                            Text(formatVnd(totalRevenue), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryRed)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = SecondaryDarkGold.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Tổng Đơn Hoàn Tất", fontSize = 12.sp, color = SecondaryDarkGold)
                            Text("$totalOrdersCount đơn", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SecondaryDarkGold)
                        }
                    }
                }
            }

            // Customer Tiers Distribution Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Phân Bổ 5 Bậc Khách Hàng Thành Viên", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        CustomerTier.values().forEach { tier ->
                            val tierCustomers = customers.filter { it.tier == tier }
                            val tierSpend = tierCustomers.sumOf { it.totalSpent }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CustomerTierBadge(tier = tier)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${tierCustomers.size} khách", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text(formatVnd(tierSpend), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Salary Budget Analysis Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cơ Cấu Quỹ Lương Nhân Sự", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Số lượng nhân sự:")
                            Text("${employees.size} người", fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tổng quỹ lương ước tính:")
                            Text(formatVnd(totalSalaryBudget), fontWeight = FontWeight.Bold, color = PrimaryRed)
                        }
                    }
                }
            }
        }
    }
}
