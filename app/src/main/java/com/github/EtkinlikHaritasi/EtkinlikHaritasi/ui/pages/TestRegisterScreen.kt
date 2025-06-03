package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.KeşfetSayfası
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.RegisterRepository
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.viewModel.RegisterViewModel

@Composable
fun RegisterScreen(navController: NavController? = null) {
    val viewModel = remember { RegisterViewModel(RegisterRepository()) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val registrationSuccess = viewModel.registrationSuccess.value

    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess == true) {
            println("✅ Kayıt başarılı")
          //  navController?.navigate(KeşfetSayfası)
            navController?.popBackStack()

        } else if (registrationSuccess == false) {
            println("❌ Kayıt başarısız")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Üye Ol", style = MaterialTheme.typography.headlineMedium)

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
            viewModel.register(email, password)
        }) {
            Text("Üye Ol")
        }

        if (registrationSuccess == false) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Kayıt başarısız, lütfen tekrar deneyin.", color = MaterialTheme.colorScheme.error)
        }
    }
}
