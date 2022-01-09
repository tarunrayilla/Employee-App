package com.example.employeeapp2

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData

class EmployeeShowRepository(context: Application) {
    private val employeeShowDao: EmployeeShowDao = EmployeeDatabase.getDatabase(context).employeeShowDao()

    fun getEmployee(id: Long): LiveData<Employee> {
        return employeeShowDao.getEmployee(id)
    }
}