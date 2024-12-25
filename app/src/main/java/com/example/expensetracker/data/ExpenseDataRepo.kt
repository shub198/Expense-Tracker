package com.example.expensetracker.data

import androidx.lifecycle.LiveData

class ExpenseDataRepo(private val expenseTableDao: ExpenseTableDao) {
    // Insert an expense
    suspend fun insertExpense(expense: ExpenseDataModel): Long {
        return expenseTableDao.insertExpense(expense)
    }

    // Update an expense
    suspend fun updateExpense(expense: ExpenseDataModel) {
        expenseTableDao.updateExpense(expense)
    }

    // Delete an expense
    suspend fun deleteExpense(expense: ExpenseDataModel) {
        expenseTableDao.deleteExpense(expense)
    }

    // Retrieve all expenses
    fun getAllExpenses(): LiveData<List<ExpenseDataModel>> {
        return expenseTableDao.getAllExpenses()
    }

    // Retrieve a single expense by its ID
    fun getExpenseById(expenseId: Int): LiveData<ExpenseDataModel>? {
        return expenseTableDao.getExpenseById(expenseId)
    }

    // Get expenses within a certain date range
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): LiveData<List<ExpenseDataModel>> {
        return expenseTableDao.getExpensesBetweenDates(startDate, endDate)
    }

    // Get total amount spent for a specific category
    fun getTotalAmountForCategory(category: String,type: String): LiveData<Int>? {
        return expenseTableDao.getTotalAmountForCategory(category,type)
    }

    // Get total amount spent for a specific type
    fun getTotalAmountForType(type: String): LiveData<Int>? {
        return expenseTableDao.getTotalAmountForType(type)
    }

    // Get total amount spent
    fun getTotalAmountSpent(): LiveData<Int>? {
        return expenseTableDao.getTotalAmountSpent()
    }
    fun getSumGroupByCategory(type: String): LiveData<List<CategoryAggregate>>{
        return  expenseTableDao.getSumGroupByCategory(type)
    }
    fun getSumGroupByType():LiveData<List<TypeAggregate>>{
        return expenseTableDao.getSumGroupByType()
    }
    fun getCategoryWiseData(category: String,type: String):LiveData<List<ExpenseDataModel>>?{
        return expenseTableDao.getCategoryWiseData(category,type)
    }
}