package com.example.employeeapp2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class EmployeeListViewModel(application: Application): AndroidViewModel(application) {
    private val repo: EmployeeListRepository = EmployeeListRepository(application)

    val employees: LiveData<List<Employee>> = repo.getEmployees()

    suspend fun insertEmployees(employees: List<Employee>){
        repo.insertEmployees(employees)
    }

    suspend fun getEmployeeList(): List<Employee>{
        return repo.getEmployeeList()
    }

}