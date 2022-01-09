package com.example.employeeapp2

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmployeeListDao {
    @Query("select * from employee order by name")
    fun getEmployees(): LiveData<List<Employee>>

    //for csv
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployees(employees: List<Employee>)

    @Query("select * from employee order by name")
    suspend fun getEmployeeList(): List<Employee>

}