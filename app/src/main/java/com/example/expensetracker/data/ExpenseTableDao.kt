package com.example.expensetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseTableDao {
    // Insert a new expense into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseDataModel): Long

    // Update an existing expense
    @Update
    suspend fun updateExpense(expense: ExpenseDataModel)

    // Delete an expense
    @Delete
    suspend fun deleteExpense(expense: ExpenseDataModel)

    // Retrieve all expenses, ordered by date in descending order
    @Query("SELECT * FROM expense_table ORDER BY date DESC")
    suspend fun getAllExpenses(): List<ExpenseDataModel>

    // Retrieve a single expense by its ID
    @Query("SELECT * FROM expense_table WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Int): ExpenseDataModel?

    // Get expenses within a certain date range (useful for reports)
    @Query("SELECT * FROM expense_table WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getExpensesBetweenDates(startDate: Long, endDate: Long): List<ExpenseDataModel>

    // Get total amount spent for a specific category
    @Query("SELECT SUM(amount) FROM expense_table WHERE category = :category")
    suspend fun getTotalAmountForCategory(category: String): Int?

    @Query("SELECT SUM(amount) FROM expense_table WHERE type = :type")
    suspend fun getTotalAmountForType(type: String): Int?

    // Get total amount spent
    @Query("SELECT SUM(amount) FROM expense_table")
    suspend fun getTotalAmountSpent(): Int?
}