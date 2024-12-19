import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R
import com.example.expensetracker.data.ExpenseViewModel
import com.example.expensetracker.data.PieChartDatamodel

@Composable
fun BarChart(
    radiusOuter: Dp = 75.dp,
    chartBarWidth: Dp = 25.dp,
    animDuration: Int = 1000,
    expenseViewModel: ExpenseViewModel,
    selectedTabIndex: Int
) {
    val type = if (selectedTabIndex == 0) "expense" else "income"
    val totalSum = expenseViewModel.getTotalAmountForType(type)?.observeAsState(0) // Default value 0 if null
    val groupedData by expenseViewModel.getSumGroupByCategory(type).observeAsState(emptyList())

    var chartData by remember { mutableStateOf(emptyList<PieChartDatamodel>()) }
    var animationPlayed by remember { mutableStateOf(false) }

    // Populate chart data when grouped data changes
    LaunchedEffect(groupedData) {
        chartData = groupedData.map { value ->
            val percentage = if (totalSum?.value!! > 0) {
                (value.totalAmount.toFloat() / totalSum?.value!!.toFloat()) * 100f
            } else 0f
            val color = when (value.category) {
                "Health" -> Color(0xFFE64747)
                "House" -> Color(0xFFF5A662)
                "Food" -> Color(0xFFCF9DCA)
                "Education" -> Color(0xFF5B92C2)
                "Gift" -> Color(0xFFE677BF)
                "Groceries" -> Color(0xFF4DB36A)
                "Family" -> Color(0xFFECF545)
                "Workout" -> Color(0xFFFAB669)
                "Transport" -> Color(0xFF2C8AE8)
                "Other" -> Color(0xFF86888A)
                else -> Color.Gray
            }
            PieChartDatamodel(percentage, color)
        }
    }

    // Animated size of the chart
    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            easing = LinearOutSlowInEasing
        )
    )

    // Animated progress for each segment
    val animatedProgress = chartData.map { data ->
        animateFloatAsState(
            targetValue = data.arcPercentage / 100f,
            animationSpec = tween(
                durationMillis = animDuration,
                easing = LinearOutSlowInEasing
            )
        )
    }

    LaunchedEffect(true) {
        animationPlayed = true
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(animateSize.dp),
            contentAlignment = Alignment.Center
        ) {
            if (groupedData.isEmpty() || totalSum?.value == 0) {
                // Show grey chart with "Data not present" message
                Canvas(modifier = Modifier.size(radiusOuter * 2f)) {
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
                Canvas(modifier = Modifier.size(radiusOuter * 2f)) {
                    val gapAngle = 2f
                    var currentStartAngle = 0f
                    chartData.forEachIndexed { index, value ->
                        val sweepAngle = value.arcPercentage * animatedProgress[index].value
                        drawArc(
                            color = value.color,
                            startAngle = currentStartAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Round)
                        )
                        currentStartAngle += sweepAngle + gapAngle
                    }
                }

                Text(
                    text = "\u20B9${totalSum}", // Displaying total sum in INR
                    fontSize = 28.sp,
                    color = colorResource(R.color.color_ffffff),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
