package com.example.employeeapp2

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface EmployeeShowDao {
    @Query("select * from employee where id = :id")
    fun getEmployee(id: Long): LiveData<Employee>
}