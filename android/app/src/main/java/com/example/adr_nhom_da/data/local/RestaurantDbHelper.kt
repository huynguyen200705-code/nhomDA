package com.example.adr_nhom_da.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

import com.example.adr_nhom_da.data.model.*

class RestaurantDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "restaurant_db.db"
        private const val DATABASE_VERSION = 1

        // Table names
        const val TABLE_USERS = "users"
        const val TABLE_CUSTOMERS = "customers"
        const val TABLE_EMPLOYEES = "employees"
        const val TABLE_MENU_ITEMS = "menu_items"
        const val TABLE_TABLES = "restaurant_tables"
        const val TABLE_ORDERS = "orders"
        const val TABLE_ORDER_ITEMS = "order_items"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Create Users
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                full_name TEXT NOT NULL,
                phone TEXT,
                role TEXT NOT NULL
            )
        """.trimIndent())

        // 2. Create Customers
        db.execSQL("""
            CREATE TABLE $TABLE_CUSTOMERS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                phone TEXT NOT NULL,
                email TEXT,
                total_spent REAL DEFAULT 0.0,
                loyalty_points INTEGER DEFAULT 0
            )
        """.trimIndent())

        // 3. Create Employees
        db.execSQL("""
            CREATE TABLE $TABLE_EMPLOYEES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                phone TEXT NOT NULL,
                position TEXT NOT NULL,
                tier TEXT NOT NULL,
                contract_type TEXT NOT NULL,
                hourly_rate REAL DEFAULT 25000.0,
                monthly_salary REAL DEFAULT 8000000.0,
                worked_hours REAL DEFAULT 0.0,
                worked_days REAL DEFAULT 0.0,
                bonus REAL DEFAULT 0.0,
                penalty REAL DEFAULT 0.0
            )
        """.trimIndent())

        // 4. Create Menu Items
        db.execSQL("""
            CREATE TABLE $TABLE_MENU_ITEMS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                price REAL NOT NULL,
                description TEXT,
                is_available INTEGER DEFAULT 1
            )
        """.trimIndent())

        // 5. Create Restaurant Tables
        db.execSQL("""
            CREATE TABLE $TABLE_TABLES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                floor TEXT NOT NULL,
                seats INTEGER DEFAULT 4,
                status TEXT NOT NULL,
                current_order_id INTEGER
            )
        """.trimIndent())

        // 6. Create Orders
        db.execSQL("""
            CREATE TABLE $TABLE_ORDERS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                table_id INTEGER NOT NULL,
                table_name TEXT NOT NULL,
                customer_id INTEGER,
                customer_name TEXT,
                customer_tier_name TEXT,
                employee_id INTEGER,
                employee_name TEXT,
                subtotal REAL DEFAULT 0.0,
                discount_percent REAL DEFAULT 0.0,
                payment_method TEXT DEFAULT 'Tiền mặt',
                status TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
        """.trimIndent())

        // 7. Create Order Items
        db.execSQL("""
            CREATE TABLE $TABLE_ORDER_ITEMS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id INTEGER NOT NULL,
                menu_item_id INTEGER NOT NULL,
                menu_item_name TEXT NOT NULL,
                price REAL NOT NULL,
                quantity INTEGER NOT NULL,
                note TEXT
            )
        """.trimIndent())

        // Seed initial data
        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TABLES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MENU_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EMPLOYEES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOMERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        // Seed Users
        db.execSQL("INSERT INTO $TABLE_USERS (username, password, full_name, phone, role) VALUES ('admin', '123456', 'Quản Trị Viên', '0901234567', 'ADMIN')")
        db.execSQL("INSERT INTO $TABLE_USERS (username, password, full_name, phone, role) VALUES ('staff', '123456', 'Nhân Viên Thu Ngân', '0909876543', 'USER')")

        // Seed Customers (5 Tiers)
        db.execSQL("INSERT INTO $TABLE_CUSTOMERS (name, phone, email, total_spent, loyalty_points) VALUES ('Nguyen Van A', '0911111111', 'a@gmail.com', 2000000.0, 20)")
        db.execSQL("INSERT INTO $TABLE_CUSTOMERS (name, phone, email, total_spent, loyalty_points) VALUES ('Tran Thi B', '0922222222', 'b@gmail.com', 7500000.0, 75)")
        db.execSQL("INSERT INTO $TABLE_CUSTOMERS (name, phone, email, total_spent, loyalty_points) VALUES ('Le Van C', '0933333333', 'c@gmail.com', 18000000.0, 180)")
        db.execSQL("INSERT INTO $TABLE_CUSTOMERS (name, phone, email, total_spent, loyalty_points) VALUES ('Pham Thi D', '0944444444', 'd@gmail.com', 35000000.0, 350)")
        db.execSQL("INSERT INTO $TABLE_CUSTOMERS (name, phone, email, total_spent, loyalty_points) VALUES ('Hoang Van E', '0955555555', 'e@gmail.com', 60000000.0, 600)")

        // Seed Employees (4 Tiers)
        db.execSQL("INSERT INTO $TABLE_EMPLOYEES (name, phone, position, tier, contract_type, hourly_rate, monthly_salary, worked_hours, worked_days, bonus, penalty) VALUES ('Nguyen Van Binh', '098111222', 'Phục vụ', 'THU_VIEC', 'HOURLY', 25000.0, 0.0, 120.0, 0.0, 200000.0, 0.0)")
        db.execSQL("INSERT INTO $TABLE_EMPLOYEES (name, phone, position, tier, contract_type, hourly_rate, monthly_salary, worked_hours, worked_days, bonus, penalty) VALUES ('Tran Thi Hoa', '098333444', 'Thu ngân', 'CHINH_THUC', 'MONTHLY', 0.0, 8500000.0, 0.0, 24.0, 500000.0, 100000.0)")
        db.execSQL("INSERT INTO $TABLE_EMPLOYEES (name, phone, position, tier, contract_type, hourly_rate, monthly_salary, worked_hours, worked_days, bonus, penalty) VALUES ('Le Hoang Nam', '098555666', 'Bếp trưởng', 'QUAN_LY', 'MONTHLY', 0.0, 15000000.0, 0.0, 26.0, 1500000.0, 0.0)")
        db.execSQL("INSERT INTO $TABLE_EMPLOYEES (name, phone, position, tier, contract_type, hourly_rate, monthly_salary, worked_hours, worked_days, bonus, penalty) VALUES ('Pham Minh Duc', '098777888', 'Giám đốc VH', 'GIAM_DOC', 'MONTHLY', 0.0, 30000000.0, 0.0, 26.0, 5000000.0, 0.0)")

        // Seed Menu Items
        db.execSQL("INSERT INTO $TABLE_MENU_ITEMS (name, category, price, description, is_available) VALUES ('Gỏi ngó sen tôm thịt', 'Khai vị', 85000.0, 'Tôm tươi, thịt ba chỉ, ngó sen giòn', 1)")
        db.execSQL("INSERT INTO $TABLE_MENU_ITEMS (name, category, price, description, is_available) VALUES ('Chả giò hải sản', 'Khai vị', 75000.0, 'Giòn rụm châm sốt mayonaise', 1)")
        db.execSQL("INSERT INTO $TABLE_MENU_ITEMS (name, category, price, description, is_available) VALUES ('Lẩu thái hải sản', 'Món chính', 290000.0, 'Đậm đà vị chua cay tôm mực nấm', 1)")
        db.execSQL("INSERT INTO $TABLE_MENU_ITEMS (name, category, price, description, is_available) VALUES ('Bò lúc lắc hạt điều', 'Món chính', 165000.0, 'Thịt bò Úc mềm xào rau củ', 1)")
        db.execSQL("INSERT INTO $TABLE_MENU_ITEMS (name, category, price, description, is_available) VALUES ('Cơm chiên hải sản', 'Món chính', 110000.0, 'Hạt cơm vàng óng tôm mực', 1)")
        db.execSQL("INSERT INTO $TABLE_MENU_ITEMS (name, category, price, description, is_available) VALUES ('Trà đào cam sả', 'Đồ uống', 45000.0, 'Thơm mát thanh nhiệt', 1)")
        db.execSQL("INSERT INTO $TABLE_MENU_ITEMS (name, category, price, description, is_available) VALUES ('Cà phê sữa đá', 'Đồ uống', 35000.0, 'Đậm đà phong cách Việt', 1)")
        db.execSQL("INSERT INTO $TABLE_MENU_ITEMS (name, category, price, description, is_available) VALUES ('Chè khúc bạch', 'Tráng miệng', 40000.0, 'Mát lạnh ngọt thanh', 1)")

        // Seed Tables
        db.execSQL("INSERT INTO $TABLE_TABLES (name, floor, seats, status) VALUES ('Bàn 101', 'Tầng 1', 4, 'TRONG')")
        db.execSQL("INSERT INTO $TABLE_TABLES (name, floor, seats, status) VALUES ('Bàn 102', 'Tầng 1', 4, 'TRONG')")
        db.execSQL("INSERT INTO $TABLE_TABLES (name, floor, seats, status) VALUES ('Bàn 103', 'Tầng 1', 6, 'DA_DAT')")
        db.execSQL("INSERT INTO $TABLE_TABLES (name, floor, seats, status) VALUES ('Bàn 201', 'Tầng 2', 4, 'TRONG')")
        db.execSQL("INSERT INTO $TABLE_TABLES (name, floor, seats, status) VALUES ('Bàn 202', 'Tầng 2', 8, 'DANG_PHUC_VU')")
        db.execSQL("INSERT INTO $TABLE_TABLES (name, floor, seats, status) VALUES ('Phòng VIP 1', 'Phòng VIP', 12, 'TRONG')")
        db.execSQL("INSERT INTO $TABLE_TABLES (name, floor, seats, status) VALUES ('Phòng VIP 2', 'Phòng VIP', 10, 'TRONG')")
    }
}
