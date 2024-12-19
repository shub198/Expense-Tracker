package com.example.expensetracker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseDataRepo) : ViewModel() {

    val allExpenses: LiveData<List<ExpenseDataModel>> = repository.getAllExpenses()

    // Insert an expense
    fun insertExpense(expense: ExpenseDataModel) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

    // Update an expense
    fun updateExpense(expense: ExpenseDataModel) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    // Delete an expense
    fun deleteExpense(expense: ExpenseDataModel) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // Get a single expense by ID
    fun getExpenseById(expenseId: Int): LiveData<ExpenseDataModel>? {
        return repository.getExpenseById(expenseId)
    }

    // Get expenses within a date range
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): LiveData<List<ExpenseDataModel>> {
        return repository.getExpensesBetweenDates(startDate, endDate)
    }

    // Get total amount for a category
    fun getTotalAmountForCategory(category: String): LiveData<Int>? {
        return repository.getTotalAmountForCategory(category)
    }

    // Get total amount for a type
    fun getTotalAmountForType(type: String): LiveData<Int>? {
        return repository.getTotalAmountForType(type)
    }

    // Get total amount spent
    fun getTotalAmountSpent(): LiveData<Int>? {
        return repository.getTotalAmountSpent()
    }

    fun getSumGroupByCategory(type: String): LiveData<List<CategoryAggregate>>{
        return  repository.getSumGroupByCategory(type)
    }
    fun getSumGroupByType():LiveData<List<TypeAggregate>>{
        return repository.getSumGroupByType()
    }
}


class ExpenseViewModelFactory(private val repository: ExpenseDataRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}