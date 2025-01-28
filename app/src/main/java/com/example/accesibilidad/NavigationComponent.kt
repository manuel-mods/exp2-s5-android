import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.accesibilidad.ForgotPasswordScreen
import com.example.accesibilidad.HomeScreen
import com.example.accesibilidad.LoginScreen
import com.example.accesibilidad.RegisterScreen

@Composable
fun NavigationComponent() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("home") { HomeScreen() }
    }
}

