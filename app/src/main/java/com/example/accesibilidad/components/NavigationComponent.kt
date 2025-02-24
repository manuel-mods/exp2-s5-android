import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.accesibilidad.screens.SearchDevicesScreen
import com.example.accesibilidad.screens.ReadScreen
import com.example.accesibilidad.screens.ForgotPasswordScreen
import com.example.accesibilidad.screens.HelpScreen
import com.example.accesibilidad.screens.TalkScreen
import com.example.accesibilidad.screens.HomeScreen
import com.example.accesibilidad.screens.LoginScreen
import com.example.accesibilidad.screens.RegisterScreen
import com.example.accesibilidad.screens.UsersScreen

@Composable
fun NavigationComponent() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("hablar") { TalkScreen(navController) }
        composable("escribir") { ReadScreen(navController) }
        composable("buscar") { SearchDevicesScreen(navController) }
        composable("usuarios") { UsersScreen(navController) }
        composable("ayuda") { HelpScreen(navController) }
    }

}

