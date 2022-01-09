package com.example.employeeapp2

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.lang.Appendable

class EmployeeDetailViewModel(application: Application): AndroidViewModel(application) {
    private val repo: EmployeeDetailRepository = EmployeeDetailRepository(application)

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

    fun saveEmployee(employee: Employee) {
        viewModelScope.launch {
            if(_employeeId.value == 0L) {
                _employeeId.value = repo.insertEmployee(employee)
            } else {
                repo.updateEmployee(employee)
            }
        }
    }

    fun deleteEmployee() {
        viewModelScope.launch {
            employee.value?.let {
                repo.deleteEmployee(it)
            }
        }
    }
}