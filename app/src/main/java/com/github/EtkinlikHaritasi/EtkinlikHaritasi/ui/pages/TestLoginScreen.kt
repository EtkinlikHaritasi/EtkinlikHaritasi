package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages



import LoginViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.KeşfetSayfası
//import com.github.EtkinlikHaritasi.EtkinlikHaritasi.RegisterSayfasi
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api.AuthApi
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.auth.RetrofitInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.LoginRepository



@Composable
fun TestLoginScreen(navController: NavController? = null) {
   // val context = LocalContext.current

    val loginViewModel = remember {
        //val api = RetrofitInstance.retrofit.create(AuthApi::class.java)
        val repo = LoginRepository()
        LoginViewModel(repo)
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
  //  val loginResult = loginViewModel.loginResult.value
    val loginToken = loginViewModel.loginToken.value


    LaunchedEffect(loginToken) {
        if (loginToken != null) {
            println("Firebase Token alındı: $loginToken")
            // Burada veritabanı isteği vs. yapılabilir
            navController?.navigate(KeşfetSayfası)

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Test Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            loginViewModel.login(email, password)
        }) {
            Text("JWT Giriş Test")
        }

        Spacer(modifier = Modifier.height(16.dp))


        TextButton(onClick = {
            //navController?.navigate(RegisterSayfasi)
        }) {
            Text("Hesabın yok mu? Üye Ol", color = MaterialTheme.colorScheme.primary)
        }

    }
}
