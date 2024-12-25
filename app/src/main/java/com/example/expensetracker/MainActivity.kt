package com.example.expensetracker

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.data.ExpenseDataBase
import com.example.expensetracker.data.ExpenseDataRepo
import com.example.expensetracker.data.ExpenseViewModel
import com.example.expensetracker.data.ExpenseViewModelFactory
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme

class MainActivity : ComponentActivity() {
    private val database by lazy { ExpenseDataBase.getDatabase(applicationContext) }
    private lateinit var expenseViewModel: ExpenseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupEdgeToEdgeMode()

        // Defer status bar appearance setup
        window.decorView.post {
            setupStatusBarAppearance()
        }

        // Initialize ViewModel
        val repository = ExpenseDataRepo(database.expenseDao())
        val factory = ExpenseViewModelFactory(repository)
        expenseViewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]

        setContent {
            val authViewModel: AuthViewModel by viewModels()
            ExpenseTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        expenseViewModel = expenseViewModel
                    )
                }
            }
        }
    }

    private fun setupEdgeToEdgeMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    @SuppressLint("ResourceAsColor")
    private fun setupStatusBarAppearance() {
        val decorView = window.decorView
        if (decorView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insetsController = window.insetsController
                insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS.inv(),
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                decorView.systemUiVisibility =
                    decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
}
