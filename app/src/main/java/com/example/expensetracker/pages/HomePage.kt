package com.example.expensetracker.pages

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.expensetracker.AuthState
import com.example.expensetracker.AuthViewModel
import com.example.expensetracker.R
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import kotlin.io.path.ExperimentalPathApi

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomePage(modifier: Modifier=Modifier,navController: NavController,authViewModel: AuthViewModel){
    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    var backPressedOnce by remember { mutableStateOf(false) }
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    // Handle back press to exit the app
    if (backPressedOnce) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            backPressedOnce = false
        }
    }

    BackHandler {
        if (backPressedOnce) {
            // Exit the app
            (context as? android.app.Activity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    ExpenseIncomeTabScreen(modifier,navController)

}


@ExperimentalPagerApi
@OptIn(ExperimentalUnitApi::class)
@Composable
fun ExpenseIncomeTabScreen(modifier: Modifier,navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val tabs = listOf("EXPENSES", "INCOME")
    val coroutineScope = rememberCoroutineScope()

    // Observe pagerState and update selectedTabIndex
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(colorResource(id = R.color.color_515753)) // Background color for the screen
    ) {
        // Top Section with Total
        Box(modifier = Modifier.background(colorResource(R.color.app_theme_color), shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Total",
                        fontSize = 14.sp,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.houselogo), // Replace with actual icon
                        contentDescription = "Dropdown",
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "₹5,847",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = colorResource(R.color.app_theme_color),
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .height(2.dp)
                                .padding(horizontal = 48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color = androidx.compose.ui.graphics.Color.White, shape = RoundedCornerShape(8.dp))
                        )
                    },
                    contentColor = androidx.compose.ui.graphics.Color.Transparent,
                    divider = {HorizontalDivider(color = androidx.compose.ui.graphics.Color.Transparent)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)

                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            unselectedContentColor = androidx.compose.ui.graphics.Color.Transparent,
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                        ){
                            Text(
                                text = title,
                                fontSize = 20.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        // TabRow for Expenses and Income
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .padding(horizontal = 28.dp)
                .offset(y = (-30).dp)
                .zIndex(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(colorResource(R.color.color_454545))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(colorResource(R.color.color_454545)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "<",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.clickable { /* Handle left click */ }
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        listOf("Day", "Week", "Month", "Year", "Period").forEach { period ->
                            Text(
                                text = period,
                                fontSize = 14.sp,
                                color = if (period == "Week") Color(0xFF00E676) else androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = ">",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.clickable { /* Handle right click */ }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nov 3 – Nov 9",
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))



                PieChart(
//                        data = null
                    data = mapOf(
                        Pair("Sample-1", 150),
                        Pair("Sample-2", 120),
                        Pair("Sample-3", 80),
                        Pair("Sample-4", 50),
                        Pair("Sample-5", 20)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                FloatingActionButton(
                    onClick = {
                        navController.navigate("addscreen")
                    },
                    contentColor = androidx.compose.ui.graphics.Color.Black,
                    modifier = Modifier.size(40.dp)
                        .padding(vertical = 2.dp),
                    containerColor = colorResource(R.color.color_d6aa09)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(24.dp),
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }
    }
}



