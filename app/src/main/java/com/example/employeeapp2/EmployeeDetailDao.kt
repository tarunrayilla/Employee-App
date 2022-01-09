package com.example.employeeapp2

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EmployeeDetailDao {
    @Query("select * from employee where id = :id")
    fun getEmployee(id: Long): LiveData<Employee>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployee(employee: Employee): Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)
}