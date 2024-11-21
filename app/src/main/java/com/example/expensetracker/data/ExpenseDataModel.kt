package com.example.expensetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date


@Entity(tableName = "expense_table")
data class ExpenseDataModel(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val type:String,
    val category:String,
    val noteString: String,
    val color:Int,
    val img:Int,
    val date: Long,
    val amount:Int
)
