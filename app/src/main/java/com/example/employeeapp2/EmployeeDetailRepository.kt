package com.example.employeeapp2

import android.app.Application
import androidx.lifecycle.LiveData

class EmployeeDetailRepository(context: Application) {
    private val employeeDetailDao: EmployeeDetailDao = EmployeeDatabase.getDatabase(context).movieDetailDao()

    fun getEmployee(id: Long): LiveData<Employee> {
        return employeeDetailDao.getEmployee(id)
    }

    suspend fun insertEmployee(employee: Employee): Long {
        return employeeDetailDao.insertEmployee(employee)
    }

    suspend fun updateEmployee(employee: Employee) {
        return employeeDetailDao.updateEmployee(employee)
    }

    suspend fun deleteEmployee(employee: Employee) {
        return employeeDetailDao.deleteEmployee(employee)
    }
}