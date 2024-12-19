import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expensetracker.AuthViewModel
import com.example.expensetracker.R
import com.example.expensetracker.data.ExpenseViewModel

@Composable
fun CategoryDescriptionPage(navController: NavController, authViewModel: AuthViewModel,
                            expenseViewModel: ExpenseViewModel,category:String,type:String)
{
    val totalSum = expenseViewModel.getTotalAmountForCategory(category,type)?.observeAsState(initial = 0) // Default value 0 if null
    val data = expenseViewModel.getCategoryWiseData(category,type)?.observeAsState(emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(colorResource(id = R.color.color_515753))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.color_515753))
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier.background(
                    colorResource(R.color.app_theme_color),
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                )
            ) {
//                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
//                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        modifier = Modifier.padding( start = 10.dp),
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colorResource(R.color.color_ffffff)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.weight(1f).padding(top = 10.dp, end = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Bottom
                        ) {
                        Text(
                            text = category,
                            fontSize = 20.sp,
                            color = colorResource(R.color.color_ffffff),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "₹${totalSum?.value}",
                            fontSize = 28.sp,
                            color = colorResource(R.color.color_ffffff),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

    }
}