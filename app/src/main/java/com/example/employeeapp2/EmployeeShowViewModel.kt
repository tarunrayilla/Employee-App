package com.example.employeeapp2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class EmployeeShowViewModel(application: Application): AndroidViewModel(application) {
    private val repo: EmployeeShowRepository = EmployeeShowRepository(application)

    private val _employeeId = MutableLiveData<Long>(0)

    val employeeId: LiveData<Long>
        get() = _employeeId

    val employee: LiveData<Employee> = Transformations.switchMap(_employeeId) {
        repo.getEmployee(it)
    }

    fun setEmployeeId(id: Long) {
        if(_employeeId.value != id) {
            _employeeId.value = id
        }
    }



}