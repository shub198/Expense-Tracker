package com.example.expensetracker

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat

object Utils {
    const val EXPENSE_STRING = "Expense"
    const val EXPENSE_TYPE_STRING = "expenseType"
    const val INCOME_STRING = "Income"
    const val INCOME_TYPE_STRING = "incomeType"
    val HEALTH_COLOR = R.color.color_e64747
    val HOME_COLOR = R.color.color_fab669
    val CAFE_COLOR = R.color.color_cf9dca
    val EDUCATION_COLOR = R.color.color_5b92c2
    val GIFTS_COLOR = R.color.color_e677bf
    val GROCERIES_COLOR = R.color.color_4db36a
    val FAMILY_COLOR = R.color.color_ecf545
    val WORKOUT_COLOR = R.color.color_f5a662
    val TRANSPORTATION_COLOR = R.color.color_2c8ae8
    val OTHER_COLOR = R.color.color_e64747
    val INCOME_COLOR = R.color.color_86888a
    val INTEREST_COLOR = R.color.color_4db36a


    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}