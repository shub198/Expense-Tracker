package com.example.expensetracker.pages

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R
import com.example.expensetracker.data.ExpenseViewModel
import com.example.expensetracker.data.PieChartDatamodel
import com.example.expensetracker.ui.theme.Purple40
import com.example.expensetracker.ui.theme.Purple80



@Composable
fun PieChart(
    radiusOuter: Dp = 85.dp,
    chartBarWidth: Dp = 25.dp,
    animDuration: Int = 8000,
    expenseViewModel: ExpenseViewModel,
    selectedTabIndex:Int
) {
    val type = if (selectedTabIndex == 0) "expense" else "income"
    val totalSum = expenseViewModel.getTotalAmountForType(type)?.observeAsState(0) // Default value 0 if null

    val floatValue = remember { mutableStateListOf<PieChartDatamodel>() }
    val groupedData by expenseViewModel.getSumGroupByCategory(type).observeAsState(emptyList())

    LaunchedEffect(groupedData) {
        floatValue.clear()

        if (groupedData.isNotEmpty()) {
            groupedData.forEachIndexed { index, value ->
                val percentage = (360f * value.totalAmount.toFloat()) / totalSum?.value?.toFloat()!!

                if(value.category=="Health") {
                    floatValue.add(PieChartDatamodel(percentage,Color(0xFFE64747)))
                }else if(value.category=="House"){
                    floatValue.add(PieChartDatamodel(percentage,Color(0xFFF5A662)))
                }else if (value.category=="Food"){
                    floatValue.add(PieChartDatamodel(percentage,Color(0xFFCF9DCA)))
                }else if(value.category=="Education"){
                    floatValue.add(PieChartDatamodel(percentage,Color(0xFF5B92C2)))
                }else if (value.category=="Gift"){
                    floatValue.add(PieChartDatamodel(percentage,Color(0xFFE677BF)))
                }else if(value.category=="Groceries"){
                    floatValue.add(PieChartDatamodel(percentage,Color(0xFF4DB36A)))
                }else if (value.category=="Family"){
                    floatValue.add(PieChartDatamodel(percentage,Color(0xFFECF545)))
                }else if(value.category=="Workout"){
                    floatValue.add(PieChartDatamodel(percentage,Color(0xFFFAB669)))
                }else if (value.category=="Transport"){
                    floatValue.add(PieChartDatamodel(percentage,Color(0xFF2C8AE8)))
                }else if(value.category=="Other"){
                    floatValue.add(PieChartDatamodel(percentage,Color(0xFF86888A)))
                }
            }
        }
    }

    var animationPlayed by remember { mutableStateOf(false) }
    var lastValue = 0f

    // Animated size of the chart
    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 10f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(animateSize.dp),
            contentAlignment = Alignment.Center
        ) {
            if (groupedData.isEmpty()) {
                // Show grey chart with "Data not present" message
                Canvas(
                    modifier = Modifier
                        .size(radiusOuter * 2f)
                        .rotate(animateRotation)
                ) {
                    drawArc(
                        color = Color.Gray,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                }
                Text(
                    text = "Data not present",
                    fontSize = 16.sp,
                    color = colorResource(R.color.color_999797),
                    fontWeight = FontWeight.Bold
                )
            } else {
                Canvas(
                    modifier = Modifier
                        .size(radiusOuter * 2f)
                        .rotate(animateRotation)
                ) {
                    val gapAngle = 2f
                    var currentStartAngle = 0f
                    floatValue.forEach { value ->
                        drawArc(
                            color = value.color,
                            startAngle = currentStartAngle,
                            sweepAngle = value.arcPercentage - gapAngle,
                            useCenter = false,
                            style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                        )
                        currentStartAngle += value.arcPercentage
                    }
                    val padding = 8.dp.toPx()
                    val innerRadius = radiusOuter.toPx() - chartBarWidth.toPx() / 2f - padding
                    drawCircle(
                        color = Color.Gray,
                        radius = innerRadius,
                        style = Stroke(
                            width = 4.dp.toPx(),
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(20f, 20f),
                                0f
                            )
                        )
                    )
                }

                Text(
                    text = "\u20B9${totalSum?.value}",  // Displaying total sum in INR
                    fontSize = 28.sp,
                    color = colorResource(R.color.color_ffffff),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

