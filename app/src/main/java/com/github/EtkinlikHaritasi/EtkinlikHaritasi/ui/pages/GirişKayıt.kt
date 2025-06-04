package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import LoginViewModel
import android.text.Layout
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.dao.EventDao
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database.AppDatabase
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database.AppDatabaseInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.EventRepository
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.UserRepository
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.Typography
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.viewModel.RegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.security.SecureRandom
import kotlin.math.log

class GirişKayıt
{
    @Composable
    fun İçerik(loginViewModel: LoginViewModel, registerViewModel: RegisterViewModel,
               user: MutableState<User?>)
    {
        var scope = rememberCoroutineScope()
        var girişte = remember { mutableStateOf(true) }

        if (girişte.value)
        {
            GirişSayfası(girişte, loginViewModel, user, scope)
        }
        else
        {
            KayıtSayfası(girişte, loginViewModel, registerViewModel, user, scope)
        }
    }

    @Composable
    fun GirişSayfası(girişte: MutableState<Boolean>, loginViewModel: LoginViewModel,
                     user: MutableState<User?>, scope: CoroutineScope)
    {
        var e_posta = remember { mutableStateOf("") }
        var parola = remember { mutableStateOf("") }

        LaunchedEffect(loginViewModel.loginToken.value) {
            if (loginViewModel.loginToken.value != null)
            {
                Log.d("Login", "${loginViewModel.loginToken.value}")
                user.value = UserRepository().getUser(
                    e_posta.value.trim(),
                    "${loginViewModel.loginToken.value}"
                ).body()
                Log.d("Kullanıcı", "${user.value}")
            }
        }

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
                                    loginViewModel.login(e_posta.value.trim(), parola.value)
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
                     registerViewModel: RegisterViewModel, user: MutableState<User?>,
                     scope: CoroutineScope)
    {
        var ad = remember { mutableStateOf("") }
        var soyadı = remember { mutableStateOf("") }
        var yaş = remember { mutableStateOf(0) }
        var e_posta = remember { mutableStateOf("") }
        var parola = remember { mutableStateOf("") }

        var new_user = remember { mutableStateOf<User?>(null) }
        var new_passwd = remember { mutableStateOf<String>("") }

        LaunchedEffect(registerViewModel.registrationSuccess.value, new_user.value) {
            if (registerViewModel.registrationSuccess.value == true && new_user.value != null)
            {
                loginViewModel.login(new_user.value!!.email, new_passwd.value)
            }
        }

        var repo = UserRepository()
        LaunchedEffect(loginViewModel.loginToken.value) {
            if (loginViewModel.loginToken.value != null)
            {
                Log.d("Kullanıcı", "${repo.getUser(new_user.value!!.email, loginViewModel.loginToken.value!!).body()}")
                UserRepository().addUser(
                    new_user.value!!,
                    loginViewModel.loginToken.value!!)
                Log.d("Kullanıcı", "${repo.getUser(new_user.value!!.email, loginViewModel.loginToken.value!!).body()}")
                user.value = new_user.value
            }
        }

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

                                    new_user.value = User(
                                        id = SecureRandom().nextInt(),
                                        firstName = ad.value.trim(),
                                        lastName = soyadı.value.trim(),
                                        age = yaş.value,
                                        email = e_posta.value.trim()
                                    )

                                    new_passwd.value = parola.value
                                    registerViewModel.register(new_user.value!!.email, new_passwd.value)
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