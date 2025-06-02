package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import LoginViewModel
import android.text.Layout
import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.UserRepository
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.Typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.security.SecureRandom
import kotlin.math.log

class GirişKayıt
{
    @Composable
    fun İçerik(loginViewModel: LoginViewModel)
    {
        var scope = rememberCoroutineScope()
        var girişte = remember { mutableStateOf(true) }

        if (girişte.value)
        {
            GirişSayfası(girişte, loginViewModel, scope)
        }
        else
        {
            KayıtSayfası(girişte, loginViewModel, scope)
        }
    }

    @Composable
    fun GirişSayfası(girişte: MutableState<Boolean>, loginViewModel: LoginViewModel,
                     scope: CoroutineScope)
    {
        var e_posta = remember { mutableStateOf("") }
        var parola = remember { mutableStateOf("") }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                Modifier.fillMaxSize().padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(0.9f).align(Alignment.Center)
                ) {
                    Text(
                        text = "Giriş Yap",
                        style = Typography.displayLarge
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(0.9f).align(Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = e_posta.value,
                            onValueChange = {
                                e_posta.value = it
                            },
                            label = {
                                Text("E-Posta")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = parola.value,
                            onValueChange = {
                                parola.value = it
                            },
                            label = {
                                Text("Parola")
                            },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (e_posta.value.isNotBlank() && parola.value.isNotBlank())
                                {
                                    loginViewModel.login(e_posta.value, parola.value)
                                    Log.d("Login", "${loginViewModel.loginToken.value}")
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Giriş Yap",
                                style = Typography.titleLarge
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            girişte.value = false
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Hesabın Yoksa Kaydol",
                            style = Typography.titleMedium
                        )
                    }

                }
            }
        }
    }

    @Composable
    fun KayıtSayfası(girişte: MutableState<Boolean>, loginViewModel: LoginViewModel,
                     scope: CoroutineScope)
    {
        var ad = remember { mutableStateOf("") }
        var soyadı = remember { mutableStateOf("") }
        var yaş = remember { mutableStateOf(0) }
        var e_posta = remember { mutableStateOf("") }
        var parola = remember { mutableStateOf("") }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                Modifier.fillMaxSize().padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(0.9f).align(Alignment.Center)
                ) {
                    Text(
                        text = "Kaydol",
                        style = Typography.displayLarge
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(0.9f).align(Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = ad.value,
                            onValueChange = {
                                ad.value = it
                            },
                            label = {
                                Text("Ad")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = soyadı.value,
                            onValueChange = {
                                soyadı.value = it
                            },
                            label = {
                                Text("Soyadı")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = yaş.value.toString(),
                            onValueChange = {
                                try {
                                    yaş.value = it.toInt()
                                }
                                catch (e: Exception)
                                {
                                    yaş.value = 0
                                }
                            },
                            label = {
                                Text("Yaş")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = e_posta.value,
                            onValueChange = {
                                e_posta.value = it
                            },
                            label = {
                                Text("E-Posta")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = parola.value,
                            onValueChange = {
                                parola.value = it
                            },
                            label = {
                                Text("Parola")
                            },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (e_posta.value.isNotBlank() && parola.value.isNotBlank()
                                    && ad.value.isNotBlank() && soyadı.value.isNotBlank()
                                    && yaş.value > 0)
                                {
                                    e_posta.value.trim()
                                    var yeni_üye = User(
                                        id = SecureRandom().nextInt(),
                                        firstName = ad.value,
                                        lastName = soyadı.value,
                                        age = yaş.value,
                                        email = e_posta.value,
                                        password = parola.value
                                    )

                                    scope.launch {
                                        /*
                                        val response = UserRepository().addUser(yeni_üye)
                                        Log.d("Signup", response.message())
                                        if (response.isSuccessful)
                                        {
                                            loginViewModel.login(yeni_üye.email, yeni_üye.password)
                                            Log.d("Login", "${loginViewModel.loginToken.value}")
                                        }
                                         */
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Kaydol",
                                style = Typography.titleLarge
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            girişte.value = true
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Hesabın Varsa Giriş Yap",
                            style = Typography.titleMedium
                        )
                    }

                }
            }
        }
    }
}