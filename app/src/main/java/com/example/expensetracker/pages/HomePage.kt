package com.example.expensetracker.pages

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.example.expensetracker.Utils
import com.example.expensetracker.data.CategoryAggregate
import com.example.expensetracker.data.ExpenseViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import kotlin.io.path.ExperimentalPathApi

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomePage(modifier: Modifier=Modifier,navController: NavController,authViewModel: AuthViewModel,expenseViewModel: ExpenseViewModel){
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

    ExpenseIncomeTabScreen(modifier,navController,expenseViewModel)

}
val LazyListState.isScrolled:Boolean
    get() = firstVisibleItemIndex >1 || firstVisibleItemScrollOffset>1

@ExperimentalPagerApi
@OptIn(ExperimentalUnitApi::class)
@Composable
fun ExpenseIncomeTabScreen(modifier: Modifier,navController: NavController,expenseViewModel: ExpenseViewModel) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val tabs = listOf("EXPENSES", "INCOME")
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val type = if (selectedTabIndex == 0) Utils.EXPENSE_TYPE_STRING else Utils.INCOME_TYPE_STRING
    val incomeAmount = expenseViewModel.getTotalAmountForType("income")?.observeAsState(initial = 0)?.value
    val expenseAmount = expenseViewModel.getTotalAmountForType("expense")?.observeAsState(initial = 0)?.value
    val totalAmount = (incomeAmount ?:0)- (expenseAmount ?:0)
    val totalSum = expenseViewModel.getTotalAmountForType(type)?.observeAsState(initial = 0) // Default value 0 if null
    val groupedData by expenseViewModel.getSumGroupByCategory(type).observeAsState(emptyList())
    val lazyListState = rememberLazyListState()

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    val shimmerColors = listOf(
        colorResource(R.color.color_transparent),
        colorResource(R.color.color_ffffff).copy(alpha = 0.4f),
        colorResource(R.color.color_transparent)
    )


    val transition = rememberInfiniteTransition()
    val translateX by transition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )


    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateX, 0f),
        end = Offset(translateX + 300f, 0f)
    )

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
                        fontSize = 18.sp,
                        color = colorResource(R.color.color_ffffff),
                        fontWeight = FontWeight.Bold,
                        )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.totalamount), // Replace with actual icon
                        contentDescription = "totalAmtImg",
                        tint = colorResource(R.color.color_ffffff),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                        text = "${totalAmount}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if(totalAmount<0) colorResource(R.color.color_f7435e) else colorResource(R.color.color_ffffff),
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
                                .background(
                                    color = colorResource(R.color.color_ffffff),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                    },
                    contentColor = colorResource(R.color.color_transparent),
                    divider = {HorizontalDivider(color = colorResource(R.color.color_transparent))},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)

                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            unselectedContentColor = colorResource(R.color.color_transparent),
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
                                color = colorResource(R.color.color_ffffff)
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
//                .height(330.dp)
                .padding(horizontal = 20.dp)
                .offset(y = (-30).dp)
                .zIndex(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(colorResource(R.color.color_454545))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
//                    .fillMaxHeight()
                    .background(colorResource(R.color.color_454545)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "<",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.color_ffffff),
                        modifier = Modifier.clickable { /* Handle left click */ }
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        listOf("Day", "Week", "Month", "Year", "Period").forEach { period ->
                            Text(
                                text = period,
                                fontSize = 14.sp,
                                color = if (period == "Week") Color(0xFF00E676) else colorResource(R.color.color_ffffff),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = ">",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.color_ffffff),
                        modifier = Modifier.clickable { /* Handle right click */ }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nov 3 – Nov 9",
                    fontSize = 16.sp,
                    color = colorResource(R.color.color_ffffff),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                if(lazyListState.isScrolled && groupedData.size>=5){
                    Text(
                        text = "\u20B9${totalSum?.value}",  // Displaying total sum in INR
                        fontSize = 28.sp,
                        color = colorResource(R.color.color_ffffff),
                        fontWeight = FontWeight.Bold
                    )
                }else {
                    PieChart(
                        expenseViewModel = expenseViewModel,
                        selectedTabIndex = selectedTabIndex
                    )
                }

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.End)
                        .padding(start = 0.dp, top = 0.dp, end = 20.dp, bottom = 20.dp)
                ) {
                    FloatingActionButton(
                        onClick = { navController.navigate("addscreen") },
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.Center),
                        containerColor = colorResource(R.color.color_d6aa09),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(40.dp),
                            tint = colorResource(R.color.color_454545)
                        )
                    }

                    // Apply the shimmer overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize() // Matches the FAB size
                            .clip(CircleShape) // Ensures it matches the circular shape of the FAB
                            .background(shimmerBrush)
                    )
                }

            }
        }
        LazyColumn(modifier= Modifier
            .fillMaxWidth()
            .padding(4.dp),
            state =lazyListState) {
            items(groupedData){item ->
                ExpenseItem(totalSum?.value?:1,
                    item,
                    {category ->
                        navController.navigate("categoryDescriptionPage/${category}/${type}")
                    }
                )
            }
        }
    }
}

@Composable
fun ExpenseItem(
    totalSum: Int,
    item: CategoryAggregate,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(colorResource(R.color.color_454545), RoundedCornerShape(8.dp))
            .clickable {
                onClick(item.category)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Icon and Title Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp, top = 14.dp, bottom = 14.dp)
        ) {
            // Icon with background
            Spacer(modifier = Modifier.width(14.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(35.dp)
                    .background(colorResource(id = item.color), CircleShape)
            ) {
                Image(
                    painter = painterResource(id = item.img),
                    contentDescription = "categoryImg",
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Title
            Text(
                text = item.category,
                color = colorResource(R.color.color_ffffff),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Percentage and Amount Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 0.dp,end = 16.dp, top = 14.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.Start,

        ) {
            val percentage = if (totalSum != 0) (item.totalAmount * 100 / totalSum) else 0

            Text(
                text = "$percentage%", // Proper percentage calculation
                color = colorResource(R.color.color_ffffff),
                fontSize = 14.sp,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.width(40.dp))

            Text(
                text = "\u20B9${item.totalAmount}", // Correct Unicode for Rupee
                color = colorResource(R.color.color_ffffff),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.width(14.dp))
        }
    }
}
