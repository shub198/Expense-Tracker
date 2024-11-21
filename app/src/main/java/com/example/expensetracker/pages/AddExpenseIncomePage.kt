package com.example.expensetracker.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.expensetracker.AuthState
import com.example.expensetracker.AuthViewModel
import com.example.expensetracker.R
import com.example.expensetracker.data.CategoryGridModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@OptIn(ExperimentalPagerApi::class)
@Composable
fun AddExpenseIncomePage(modifier: Modifier=Modifier,navController: NavController,authViewModel: AuthViewModel){
    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    AddExpenseIncomeView(modifier,navController)

}


@ExperimentalPagerApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseIncomeView(modifier: Modifier = Modifier, navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val tabs = listOf("EXPENSES", "INCOME")
    val coroutineScope = rememberCoroutineScope()
    var amount by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableStateOf(-1) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isTextFieldFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

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
        ) {
            Box(
                modifier = Modifier.background(
                    colorResource(R.color.app_theme_color),
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                )
            ) {
                Column {
                    Spacer(Modifier.height(10.dp))
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
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Add Transaction",
                            fontSize = 20.sp,
                            color = Color.White
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
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                        },
                        contentColor = Color.Transparent,
                        divider = { HorizontalDivider(color = Color.Transparent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                unselectedContentColor = Color.Transparent,
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
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount", color = Color.White) },
                    maxLines = 1,
                    leadingIcon = {
                        Text(
                            text = "₹",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isTextFieldFocused = focusState.isFocused
                        }
                        .background(Color.Transparent),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        containerColor = Color.Transparent,
                        unfocusedLabelColor = Color.White,
                        focusedLabelColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("Category",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 28.dp)
            )
            Spacer(Modifier.height(8.dp))
            CategoryGridView(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                , selectedIndex = remember { mutableStateOf(selectedIndex) }
            )
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {  },
                    enabled = amount.isNotEmpty() && selectedIndex != -1,
                    modifier = Modifier
                        .height(50.dp)
                        .width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        if (amount.isNotEmpty() && selectedIndex != -1) colorResource(R.color.color_515753) else colorResource(R.color.color_ccb45c)
                    ),
                ) {
                    Text(text = "Add", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}


@Composable
fun CategoryGridView(modifier: Modifier = Modifier,selectedIndex: MutableState<Int>) {
    val courseList = listOf(
        CategoryGridModel(R.drawable._pngtree_medical_health_logo_4135842, "Health", R.color.color_e64747),
        CategoryGridModel(R.drawable.houselogo, "House", R.color.color_f5a662),
        CategoryGridModel(R.drawable.foodlogo, "Food", R.color.color_cf9dca),
        CategoryGridModel(R.drawable.educationlogo, "Education", R.color.color_5b92c2),
        CategoryGridModel(R.drawable.giftlogo, "Gift", R.color.color_e677bf),
        CategoryGridModel(R.drawable.grocerieslogo, "Groceries", R.color.color_4db36a),
        CategoryGridModel(R.drawable.family1, "Family", R.color.color_ecf545),
        CategoryGridModel(R.drawable.gym, "Workout", R.color.color_fab669),
        CategoryGridModel(R.drawable.buslogo1, "Transport", R.color.color_2c8ae8),
        CategoryGridModel(R.drawable.other, "Other", R.color.color_86888a)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        contentPadding = PaddingValues(3.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(courseList.size) { index ->
            val item = courseList[index]
            val isSelected = index == selectedIndex.value

            Column(
                Modifier
                    .fillMaxSize()
                    .background(
                        if (isSelected) Color.Gray else Color.Transparent,
                        shape = RoundedCornerShape(8.dp) // Optional for rounded selection background
                    )
                    .padding(3.dp)
                    .clickable {
                        // Update the selected index only if not already selected
                        if (selectedIndex.value != index) {
                            selectedIndex.value = index
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = item.img),
                    contentDescription = "categoryTypeImage",
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.height(9.dp))
                Text(
                    text = item.type,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}
