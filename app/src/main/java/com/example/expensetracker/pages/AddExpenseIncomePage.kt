package com.example.expensetracker.pages

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.expensetracker.AuthState
import com.example.expensetracker.AuthViewModel
import com.example.expensetracker.R
import com.example.expensetracker.Utils
import com.example.expensetracker.data.CategoryGridModel
import com.example.expensetracker.data.ExpenseDataModel
import com.example.expensetracker.data.ExpenseViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.absoluteValue


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AddExpenseIncomePage(modifier: Modifier=Modifier,navController: NavController,authViewModel: AuthViewModel,expenseViewModel: ExpenseViewModel){
    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    AddExpenseIncomeView(modifier,navController,expenseViewModel)

}


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalPagerApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseIncomeView(modifier: Modifier = Modifier, navController: NavController,expenseViewModel: ExpenseViewModel) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val tabs = listOf("EXPENSES", "INCOME")
    val coroutineScope = rememberCoroutineScope()
    var amount by remember { mutableStateOf("") }
    var selectedIndex = remember { mutableStateOf(-1) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isTextFieldFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var comment by remember {
        mutableStateOf("")
    }

    // CalendarDialog state
    val calendarDialogState = rememberSheetState()

    val disabledDates = generateSequence(LocalDate.now().plusDays(1)) { it.plusDays(1) }
        .takeWhile { it.isBefore(LocalDate.now().plusMonths(1)) } // you can adjust the duration
        .toList()
    CalendarDialog(
        state = calendarDialogState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            style = CalendarStyle.MONTH,
            disabledDates = disabledDates
        ),
        selection = CalendarSelection.Date { date ->
            selectedDate = date
        }
    )

    val context = LocalContext.current
    val courseList = listOf(
        CategoryGridModel(R.drawable._pngtree_medical_health_logo_4135842, Utils.HEALTH_STRING, Utils.HEALTH_COLOR),
        CategoryGridModel(R.drawable.houselogo, Utils.HOME_STRING, Utils.HOME_COLOR),
        CategoryGridModel(R.drawable.foodlogo, Utils.CAFE_STRING, Utils.CAFE_COLOR),
        CategoryGridModel(R.drawable.educationlogo, Utils.EDUCATION_STRING, Utils.EDUCATION_COLOR),
        CategoryGridModel(R.drawable.giftlogo, Utils.GIFTS_STRING, Utils.GIFTS_COLOR),
        CategoryGridModel(R.drawable.grocerieslogo, Utils.GROCERIES_STRING,Utils.GROCERIES_COLOR),
        CategoryGridModel(R.drawable.family1, Utils.FAMILY_STRING, Utils.FAMILY_COLOR),
        CategoryGridModel(R.drawable.gym, Utils.WORKOUT_STRING, Utils.WORKOUT_COLOR),
        CategoryGridModel(R.drawable.buslogo1, Utils.TRANSPORTATION_STRING,Utils.TRANSPORTATION_COLOR),
        CategoryGridModel(R.drawable.other, Utils.OTHER_STRING,Utils.OTHER_COLOR)
    )

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(colorResource(id = R.color.color_515753))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()  // Dismiss keyboard and hide the cursor when tapping outside the text field
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.color_515753))
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.background(
                    colorResource(R.color.app_theme_color),
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                )
            ) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = colorResource(R.color.color_ffffff)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Add Transaction",
                            fontSize = 20.sp,
                            color = colorResource(R.color.color_ffffff)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

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
                        divider = { HorizontalDivider(color = colorResource(R.color.color_transparent)) },
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
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 20.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = colorResource(R.color.color_ffffff)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.length <= 9) {
                            amount = it
                        }
                    },
                    label = { Text("Amount", color = colorResource(R.color.color_ffffff)) },
                    maxLines = 1,
                    leadingIcon = {
                        Text(
                            text = "₹",
                            color = colorResource(R.color.color_ffffff),
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isTextFieldFocused = focusState.isFocused
                        }
                        .background(colorResource(R.color.color_transparent)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.color_ffffff),
                        unfocusedTextColor = colorResource(R.color.color_ffffff),
                        containerColor = colorResource(R.color.color_transparent),
                        unfocusedLabelColor = colorResource(R.color.color_ffffff),
                        focusedLabelColor = colorResource(R.color.color_ffffff),
                        cursorColor = colorResource(R.color.color_ffffff),
                        focusedBorderColor = colorResource(R.color.color_ffffff),
                        unfocusedBorderColor = colorResource(R.color.color_ffffff)
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
            Spacer(Modifier.height(6.dp))

            Text("Category",
                color = Color(0xFFD6AA09),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 28.dp)
            )
            Spacer(Modifier.height(6.dp))

            CategoryGridView(
                modifier = Modifier
                    .padding(8.dp),
                courseList=courseList,
                selectedIndex.value,
                {index->
                    selectedIndex.value = index
                }
            )
            Spacer(Modifier.height(6.dp))

            Text("Select Date",
                color = Color(0xFFD6AA09),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 28.dp))

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.clickable {
                    calendarDialogState.show()
                }
            ) {
                Text(
                    "${selectedDate}",
                    color = colorResource(R.color.color_ffffff),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(start = 28.dp)
                )
                Spacer(Modifier.width(20.dp))
                Image(
                    painter = painterResource(R.drawable.calender),
                    contentDescription = "calenderImage",
                    modifier = Modifier
                        .height(20.dp)
                        .width(20.dp),
                    colorFilter = ColorFilter.tint(colorResource(R.color.color_ffffff))
                )
            }
            Spacer(Modifier.height(12.dp))
            Text("Comment",
                color = Color(0xFFD6AA09),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 28.dp))

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Comment", color = colorResource(R.color.color_ffffff)) },
                maxLines = 2,
                modifier = Modifier
                    .padding(start = 28.dp, end = 20.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isTextFieldFocused = focusState.isFocused
                    }
                    .background(colorResource(R.color.color_transparent)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = colorResource(R.color.color_ffffff),
                    unfocusedTextColor = colorResource(R.color.color_ffffff),
                    containerColor = colorResource(R.color.color_transparent),
                    unfocusedLabelColor = colorResource(R.color.color_ffffff),
                    focusedLabelColor = colorResource(R.color.color_ffffff),
                    cursorColor = colorResource(R.color.color_ffffff),
                    focusedBorderColor = colorResource(R.color.color_ffffff),
                    unfocusedBorderColor = colorResource(R.color.color_ffffff)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Unspecified
                )
            )
            Spacer(Modifier.height(12.dp))

        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .align(Alignment.BottomEnd),

            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    val type = if (selectedTabIndex == 0) Utils.EXPENSE_TYPE_STRING else Utils.INCOME_TYPE_STRING
                    expenseViewModel.insertExpense(ExpenseDataModel(type = type, category = courseList[selectedIndex.value].type, noteString = comment, color = courseList[selectedIndex.value].color, date = convertSelectedDateToMillisInIST(selectedDate), amount = amount.toInt(), img = courseList[selectedIndex.value].img))
                    navController.navigate("home")
                },
                enabled = amount.isNotEmpty() && selectedIndex.value != -1,
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = colorResource(R.color.color_ccb45c).copy(alpha = .5f),
                    containerColor =  colorResource(R.color.color_d6aa09).copy(alpha = .5f)
                ),
            ) {
                Text(text = "Add", color = colorResource(R.color.color_ffffff), fontSize = 16.sp)
            }
        }
    }
}


@Composable
fun CategoryGridView(
    modifier: Modifier = Modifier,
    courseList: List<CategoryGridModel>,
    selectedIndex: Int,
    onClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(colorResource(R.color.color_transparent)),
        contentPadding = PaddingValues(3.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
//        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(courseList.size) { index ->
            val item = courseList[index]
            val isSelected = index == selectedIndex // Check if the current item is selected

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSelected) colorResource(R.color.color_999797) else colorResource(R.color.color_transparent),
                        shape = RoundedCornerShape(8.dp) // Optional for rounded selection background
                    )
                    .padding(1.dp)
                    .clickable {
                        // Trigger the onClick callback with the selected index
                        onClick(index)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = item.img),
                    contentDescription = "categoryTypeImage123",
                    modifier = Modifier
                        .height(30.dp)
                        .width(30.dp),
                    colorFilter = ColorFilter.tint(colorResource(R.color.color_ffffff))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.type,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.color_ffffff)
                )
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun convertSelectedDateToMillisInIST(selectedDate: LocalDate): Long {
    val localDateTime = selectedDate.atStartOfDay()

    // Convert LocalDateTime to ZonedDateTime in IST (Asia/Kolkata time zone)
    val zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Kolkata"))

    // Convert ZonedDateTime to milliseconds
    return zonedDateTime.toInstant().toEpochMilli()
}

