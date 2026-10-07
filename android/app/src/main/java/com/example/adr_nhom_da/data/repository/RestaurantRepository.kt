package com.example.adr_nhom_da.data.repository

import android.content.ContentValues
import android.content.Context
import com.example.adr_nhom_da.data.local.RestaurantDbHelper
import com.example.adr_nhom_da.data.model.*

class RestaurantRepository(context: Context) {
    private val dbHelper = RestaurantDbHelper(context)

    // --- AUTHENTICATION ---
    fun login(usernameInput: String, passwordInput: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${RestaurantDbHelper.TABLE_USERS} WHERE username=? AND password=?",
            arrayOf(usernameInput, passwordInput)
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")) ?: "",
                role = UserRole.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("role")))
            )
        }
        cursor.close()
        return user
    }

    fun register(user: User): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", user.username)
            put("password", user.password)
            put("full_name", user.fullName)
            put("phone", user.phone)
            put("role", user.role.name)
        }
        val result = db.insert(RestaurantDbHelper.TABLE_USERS, null, values)
        return result != -1L
    }

    // --- CUSTOMERS ---
    fun getAllCustomers(): List<Customer> {
        val list = mutableListOf<Customer>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${RestaurantDbHelper.TABLE_CUSTOMERS}", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Customer(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email")) ?: "",
                        totalSpent = cursor.getDouble(cursor.getColumnIndexOrThrow("total_spent")),
                        loyaltyPoints = cursor.getInt(cursor.getColumnIndexOrThrow("loyalty_points"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun addCustomer(customer: Customer): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("name", customer.name)
            put("phone", customer.phone)
            put("email", customer.email)
            put("total_spent", customer.totalSpent)
            put("loyalty_points", customer.loyaltyPoints)
        }
        return db.insert(RestaurantDbHelper.TABLE_CUSTOMERS, null, cv) != -1L
    }

    fun updateCustomer(customer: Customer): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("name", customer.name)
            put("phone", customer.phone)
            put("email", customer.email)
            put("total_spent", customer.totalSpent)
            put("loyalty_points", customer.loyaltyPoints)
        }
        return db.update(RestaurantDbHelper.TABLE_CUSTOMERS, cv, "id=?", arrayOf(customer.id.toString())) > 0
    }

    fun deleteCustomer(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete(RestaurantDbHelper.TABLE_CUSTOMERS, "id=?", arrayOf(id.toString())) > 0
    }

    // --- EMPLOYEES ---
    fun getAllEmployees(): List<Employee> {
        val list = mutableListOf<Employee>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${RestaurantDbHelper.TABLE_EMPLOYEES}", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Employee(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        position = cursor.getString(cursor.getColumnIndexOrThrow("position")),
                        tier = EmployeeTier.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("tier"))),
                        contractType = ContractType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("contract_type"))),
                        hourlyRate = cursor.getDouble(cursor.getColumnIndexOrThrow("hourly_rate")),
                        monthlySalary = cursor.getDouble(cursor.getColumnIndexOrThrow("monthly_salary")),
                        workedHours = cursor.getDouble(cursor.getColumnIndexOrThrow("worked_hours")),
                        workedDays = cursor.getDouble(cursor.getColumnIndexOrThrow("worked_days")),
                        bonus = cursor.getDouble(cursor.getColumnIndexOrThrow("bonus")),
                        penalty = cursor.getDouble(cursor.getColumnIndexOrThrow("penalty"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun addEmployee(emp: Employee): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("name", emp.name)
            put("phone", emp.phone)
            put("position", emp.position)
            put("tier", emp.tier.name)
            put("contract_type", emp.contractType.name)
            put("hourly_rate", emp.hourlyRate)
            put("monthly_salary", emp.monthlySalary)
            put("worked_hours", emp.workedHours)
            put("worked_days", emp.workedDays)
            put("bonus", emp.bonus)
            put("penalty", emp.penalty)
        }
        return db.insert(RestaurantDbHelper.TABLE_EMPLOYEES, null, cv) != -1L
    }

    fun updateEmployee(emp: Employee): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("name", emp.name)
            put("phone", emp.phone)
            put("position", emp.position)
            put("tier", emp.tier.name)
            put("contract_type", emp.contractType.name)
            put("hourly_rate", emp.hourlyRate)
            put("monthly_salary", emp.monthlySalary)
            put("worked_hours", emp.workedHours)
            put("worked_days", emp.workedDays)
            put("bonus", emp.bonus)
            put("penalty", emp.penalty)
        }
        return db.update(RestaurantDbHelper.TABLE_EMPLOYEES, cv, "id=?", arrayOf(emp.id.toString())) > 0
    }

    fun deleteEmployee(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete(RestaurantDbHelper.TABLE_EMPLOYEES, "id=?", arrayOf(id.toString())) > 0
    }

    // --- MENU ITEMS ---
    fun getAllMenuItems(): List<MenuItem> {
        val list = mutableListOf<MenuItem>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${RestaurantDbHelper.TABLE_MENU_ITEMS}", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    MenuItem(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        price = cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")) ?: "",
                        isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow("is_available")) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun addMenuItem(item: MenuItem): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("name", item.name)
            put("category", item.category)
            put("price", item.price)
            put("description", item.description)
            put("is_available", if (item.isAvailable) 1 else 0)
        }
        return db.insert(RestaurantDbHelper.TABLE_MENU_ITEMS, null, cv) != -1L
    }

    fun updateMenuItem(item: MenuItem): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("name", item.name)
            put("category", item.category)
            put("price", item.price)
            put("description", item.description)
            put("is_available", if (item.isAvailable) 1 else 0)
        }
        return db.update(RestaurantDbHelper.TABLE_MENU_ITEMS, cv, "id=?", arrayOf(item.id.toString())) > 0
    }

    fun deleteMenuItem(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete(RestaurantDbHelper.TABLE_MENU_ITEMS, "id=?", arrayOf(id.toString())) > 0
    }

    // --- TABLES ---
    fun getAllTables(): List<RestaurantTable> {
        val list = mutableListOf<RestaurantTable>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${RestaurantDbHelper.TABLE_TABLES}", null)
        if (cursor.moveToFirst()) {
            do {
                val orderIdVal = cursor.getLong(cursor.getColumnIndexOrThrow("current_order_id"))
                list.add(
                    RestaurantTable(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        floor = cursor.getString(cursor.getColumnIndexOrThrow("floor")),
                        seats = cursor.getInt(cursor.getColumnIndexOrThrow("seats")),
                        status = TableStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("status"))),
                        currentOrderId = if (cursor.isNull(cursor.getColumnIndexOrThrow("current_order_id"))) null else orderIdVal
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun addTable(table: RestaurantTable): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("name", table.name)
            put("floor", table.floor)
            put("seats", table.seats)
            put("status", table.status.name)
        }
        return db.insert(RestaurantDbHelper.TABLE_TABLES, null, cv) != -1L
    }

    fun updateTableStatus(tableId: Long, status: TableStatus): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("status", status.name)
        }
        return db.update(RestaurantDbHelper.TABLE_TABLES, cv, "id=?", arrayOf(tableId.toString())) > 0
    }

    fun deleteTable(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete(RestaurantDbHelper.TABLE_TABLES, "id=?", arrayOf(id.toString())) > 0
    }

    // --- ORDERS & CHECKOUT ---
    fun checkoutOrder(order: Order): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val cvOrder = ContentValues().apply {
                put("table_id", order.tableId)
                put("table_name", order.tableName)
                put("customer_id", order.customerId)
                put("customer_name", order.customerName)
                put("customer_tier_name", order.customerTierName)
                put("employee_id", order.employeeId)
                put("employee_name", order.employeeName)
                put("subtotal", order.subtotal)
                put("discount_percent", order.discountPercent)
                put("payment_method", order.paymentMethod)
                put("status", OrderStatus.COMPLETED.name)
                put("timestamp", order.timestamp)
            }
            val orderId = db.insert(RestaurantDbHelper.TABLE_ORDERS, null, cvOrder)

            for (item in order.items) {
                val cvItem = ContentValues().apply {
                    put("order_id", orderId)
                    put("menu_item_id", item.menuItemId)
                    put("menu_item_name", item.menuItemName)
                    put("price", item.price)
                    put("quantity", item.quantity)
                    put("note", item.note)
                }
                db.insert(RestaurantDbHelper.TABLE_ORDER_ITEMS, null, cvItem)
            }

            // Update Table Status to TRONG
            val cvTable = ContentValues().apply {
                put("status", TableStatus.TRONG.name)
                put("current_order_id", null as Long?)
            }
            db.update(RestaurantDbHelper.TABLE_TABLES, cvTable, "id=?", arrayOf(order.tableId.toString()))

            // Update Customer totalSpent & loyalty points if customer selected
            order.customerId?.let { custId ->
                val custCursor = db.rawQuery("SELECT total_spent, loyalty_points FROM ${RestaurantDbHelper.TABLE_CUSTOMERS} WHERE id=?", arrayOf(custId.toString()))
                if (custCursor.moveToFirst()) {
                    val currentSpent = custCursor.getDouble(0)
                    val currentPoints = custCursor.getInt(1)
                    val newSpent = currentSpent + order.finalTotal
                    val earnedPoints = (order.finalTotal / 100000.0).toInt()
                    val cvCust = ContentValues().apply {
                        put("total_spent", newSpent)
                        put("loyalty_points", currentPoints + earnedPoints)
                    }
                    db.update(RestaurantDbHelper.TABLE_CUSTOMERS, cvCust, "id=?", arrayOf(custId.toString()))
                }
                custCursor.close()
            }

            db.setTransactionSuccessful()
            return orderId
        } finally {
            db.endTransaction()
        }
    }

    fun getAllCompletedOrders(): List<Order> {
        val list = mutableListOf<Order>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${RestaurantDbHelper.TABLE_ORDERS} ORDER BY id DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val orderId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                list.add(
                    Order(
                        id = orderId,
                        tableId = cursor.getLong(cursor.getColumnIndexOrThrow("table_id")),
                        tableName = cursor.getString(cursor.getColumnIndexOrThrow("table_name")),
                        customerId = if (cursor.isNull(cursor.getColumnIndexOrThrow("customer_id"))) null else cursor.getLong(cursor.getColumnIndexOrThrow("customer_id")),
                        customerName = cursor.getString(cursor.getColumnIndexOrThrow("customer_name")) ?: "Khách vãng lai",
                        customerTierName = cursor.getString(cursor.getColumnIndexOrThrow("customer_tier_name")) ?: "Bậc Đồng",
                        employeeId = cursor.getLong(cursor.getColumnIndexOrThrow("employee_id")),
                        employeeName = cursor.getString(cursor.getColumnIndexOrThrow("employee_name")) ?: "Nhân viên",
                        subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")),
                        discountPercent = cursor.getDouble(cursor.getColumnIndexOrThrow("discount_percent")),
                        paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow("payment_method")) ?: "Tiền mặt",
                        status = OrderStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("status"))),
                        timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
