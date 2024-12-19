package com.example.expensetracker

import CategoryDescriptionPage
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.data.ExpenseViewModel
import com.example.expensetracker.pages.AddExpenseIncomePage
import com.example.expensetracker.pages.HomePage
import com.example.expensetracker.pages.LoginPage
import com.example.expensetracker.pages.SignupPage

@SuppressLint("NewApi")
@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    expenseViewModel: ExpenseViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel, expenseViewModel)
        }
        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }
        composable("addscreen") {
            AddExpenseIncomePage(modifier, navController, authViewModel, expenseViewModel)
        }
        composable(
            route = "categoryDescriptionPage/{category}/{type}"
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "Health"
            val type = backStackEntry.arguments?.getString("type") ?: "expense"
            CategoryDescriptionPage(navController, authViewModel, expenseViewModel, category, type)
        }
    }
}

