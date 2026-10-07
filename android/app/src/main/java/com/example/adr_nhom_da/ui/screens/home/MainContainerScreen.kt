package com.example.adr_nhom_da.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.adr_nhom_da.data.model.User
import com.example.adr_nhom_da.data.model.UserRole
import com.example.adr_nhom_da.data.repository.RestaurantRepository
import com.example.adr_nhom_da.ui.screens.analytics.AnalyticsScreen
import com.example.adr_nhom_da.ui.screens.customer.CustomerManagementScreen
import com.example.adr_nhom_da.ui.screens.employee.EmployeeManagementScreen
import com.example.adr_nhom_da.ui.screens.menu.MenuManagementScreen
import com.example.adr_nhom_da.ui.screens.pos.OrderCheckoutScreen
import com.example.adr_nhom_da.ui.screens.pos.TableManagementScreen
import com.example.adr_nhom_da.ui.screens.profile.ProfileScreen
import com.example.adr_nhom_da.ui.theme.PrimaryRed

sealed class NavTab(val title: String, val icon: ImageVector) {
    object Tables : NavTab("Sơ đồ bàn", Icons.Default.TableBar)
    object POS : NavTab("Gọi món", Icons.Default.ShoppingCart)
    object Menu : NavTab("Thực đơn", Icons.Default.RestaurantMenu)
    object Customer : NavTab("Khách hàng", Icons.Default.People)
    object Employee : NavTab("Nhân sự", Icons.Default.Badge)
    object Analytics : NavTab("Thống kê", Icons.Default.Analytics)
    object Profile : NavTab("Cá nhân", Icons.Default.Person)
}

@Composable
fun MainContainerScreen(
    currentUser: User,
    repository: RestaurantRepository,
    onLogout: () -> Unit
) {
    val tabs = remember(currentUser.role) {
        if (currentUser.role == UserRole.ADMIN) {
            listOf(
                NavTab.Tables,
                NavTab.Menu,
                NavTab.Customer,
                NavTab.Employee,
                NavTab.Analytics,
                NavTab.Profile
            )
        } else {
            listOf(
                NavTab.Tables,
                NavTab.POS,
                NavTab.Menu,
                NavTab.Profile
            )
        }
    }

    var selectedTab by remember { mutableStateOf(tabs.first()) }
    var selectedTableIdForOrder by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryRed,
                            selectedTextColor = PrimaryRed,
                            indicatorColor = PrimaryRed.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                NavTab.Tables -> TableManagementScreen(
                    repository = repository,
                    onOpenOrder = { table ->
                        selectedTableIdForOrder = table.id
                        selectedTab = NavTab.POS
                    }
                )
                NavTab.POS -> OrderCheckoutScreen(
                    repository = repository,
                    currentUser = currentUser,
                    initialTableId = selectedTableIdForOrder
                )
                NavTab.Menu -> MenuManagementScreen(
                    repository = repository,
                    isAdmin = currentUser.role == UserRole.ADMIN
                )
                NavTab.Customer -> CustomerManagementScreen(
                    repository = repository
                )
                NavTab.Employee -> EmployeeManagementScreen(
                    repository = repository
                )
                NavTab.Analytics -> AnalyticsScreen(
                    repository = repository
                )
                NavTab.Profile -> ProfileScreen(
                    currentUser = currentUser,
                    repository = repository,
                    onLogout = onLogout
                )
            }
        }
    }
}
