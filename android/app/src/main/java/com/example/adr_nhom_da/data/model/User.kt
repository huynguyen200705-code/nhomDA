package com.example.adr_nhom_da.data.model

enum class UserRole {
    ADMIN,
    USER
}

data class User(
    val id: Long = 0,
    val username: String,
    val password: String,
    val fullName: String,
    val phone: String = "",
    val role: UserRole = UserRole.USER
)
