package com.example.expensetracker.data

import androidx.lifecycle.LiveData
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
     fun getAllExpenses(): LiveData<List<ExpenseDataModel>>

    // Retrieve a single expense by its ID
    @Query("SELECT * FROM expense_table WHERE id = :expenseId")
     fun getExpenseById(expenseId: Int): LiveData<ExpenseDataModel>?

    // Get expenses within a certain date range (useful for reports)
    @Query("SELECT * FROM expense_table WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
     fun getExpensesBetweenDates(startDate: Long, endDate: Long): LiveData<List<ExpenseDataModel>>

    // Get total amount spent for a specific category
    @Query("SELECT SUM(amount) FROM expense_table WHERE category = :category")
     fun getTotalAmountForCategory(category: String): LiveData<Int>?

    @Query("SELECT SUM(amount) FROM expense_table WHERE type = :type")
     fun getTotalAmountForType(type: String): LiveData<Int>?

    // Get total amount spent
    @Query("SELECT SUM(amount) FROM expense_table")
     fun getTotalAmountSpent(): LiveData<Int>?

    @Query("""
    SELECT category, SUM(amount) AS totalAmount, color, img 
    FROM expense_table 
    WHERE type = :type
    GROUP BY category
""")
    fun getSumGroupByCategory(type: String): LiveData<List<CategoryAggregate>>

    @Query(" SELECT type, SUM(amount) AS totalAmount, color, img FROM expense_table GROUP BY type")
    fun getSumGroupByType(): LiveData<List<TypeAggregate>>


}