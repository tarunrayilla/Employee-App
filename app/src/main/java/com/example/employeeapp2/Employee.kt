package com.example.employeeapp2

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Gender {
    Male,
    Female,
    Other
}

enum class Role {
    Manager,
    Staff,
    Worker
}

@Entity(tableName = "employee")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val role: String,
    val age: Int,
    val gender: Int,
    val photo: String
)