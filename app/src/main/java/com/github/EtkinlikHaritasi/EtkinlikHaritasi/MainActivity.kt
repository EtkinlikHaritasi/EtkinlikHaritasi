package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import LoginViewModel
import android.Manifest
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.EtkinlikHaritasiTheme
import androidx.compose.material3.*
import java.util.concurrent.TimeUnit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages.*
import kotlinx.serialization.Serializable
import androidx.work.*

import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.Connectivity.NearbyDeviceUtils
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.LoginRepository
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.RegisterRepository
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.UserRepository
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.viewModel.RegisterViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.workers.EventSyncWorker


@Serializable
object KeşfetSayfası
@Serializable
object RobotSayfası
@Serializable
object BiletSayfası
@Serializable
object MikrofonSayfası
@Serializable
object KendimSayfası
@Serializable
object JWTTestEkrani


class MainActivity : ComponentActivity()
{
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val permissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.NEARBY_WIFI_DEVICES // Burası tekrar ele alınacak
    )

    private fun checkAndRequestPermissions() {
        val missing = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 101)
        }
    }

    fun scheduleEventWorker(context: Context) {
        Log.d("EventSyncWorker", "Worker başla")

        val oneTimeRequest = OneTimeWorkRequestBuilder<EventSyncWorker>().build()
        WorkManager.getInstance(context).enqueue(oneTimeRequest)

        val periodicRequest = PeriodicWorkRequestBuilder<EventSyncWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "event_sync_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    @ExperimentalMaterial3Api
    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?)
    {
        scheduleEventWorker(this)
        fusedLocationProviderClient = LocationUtils.getFusedLocationProviderClient(this)

        LocationUtils.checkLocationPermission(this)

        LocationUtils.startContinuousLocationUpdates(this, fusedLocationProviderClient)

        checkAndRequestPermissions()


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EtkinlikHaritasiTheme {
                var loginViewModel = remember {
                    LoginViewModel(LoginRepository())
                }
                var registerViewModel = remember {
                    RegisterViewModel(RegisterRepository())
                }

                var user = remember { mutableStateOf<User?>(null) }


                if (loginViewModel.loginToken.value != null && user.value != null)
                {
                    val navController = rememberNavController()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            AltMenü(navController)
                        }
                    ) { innerPadding ->
                        NavHost(navController = navController, startDestination = KeşfetSayfası)
                        {
                            composable<KeşfetSayfası> {
                                Keşfet().İçerik(
                                    modifier = Modifier.padding(innerPadding),
                                    user = user)
                            }
                            composable<RobotSayfası> {
                                Robot().İçerik(
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            composable<BiletSayfası> {
                                Bilet().İçerik(
                                    modifier = Modifier.padding(innerPadding),
                                    user = user
                                )
                            }
                            composable<MikrofonSayfası> {
                                Mikrofon().İçerik(
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            composable<KendimSayfası> {
                                Kendim().İçerik(
                                    modifier = Modifier.padding(innerPadding),
                                    user = user
                                )
                            }
                        }
                    }
                }
                else
                {
                    GirişKayıt().İçerik(
                        loginViewModel = loginViewModel,
                        registerViewModel = registerViewModel,
                        user = user
                    )
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent)
    {
        super.onNewIntent(intent)

        NFCUtils.processNfcIntent(this, intent)
    }

}

@Composable
fun AltMenü( navController: NavController, modifier: Modifier = Modifier) {

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination

    val items = listOf("Keşfet", "Bilmiyorum", "Kendim")
    val selectedIcons = listOf(Icons.Filled.Map, Icons.Filled.QuestionMark, Icons.Filled.AccountCircle)
    val unselectedIcons =
        listOf(Icons.Outlined.Map, Icons.Outlined.QuestionMark, Icons.Outlined.AccountCircle)

    NavigationBar {

        var keşfet = Keşfet()
        var bu = (currentDestination?.hierarchy?.any {it.hasRoute(KeşfetSayfası::class)} == true)
        NavigationBarItem(
            icon = {
                Icon(
                    if (!bu) keşfet.kapalıyken_simge else keşfet.açıkken_simge,
                    contentDescription = keşfet.başlık
                )
            },
            label = {
                Text(keşfet.başlık)
            },
            selected = bu,
            onClick = {
                navController.navigate(KeşfetSayfası) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }
        )

        var robot = Robot()
        bu = currentDestination?.hierarchy?.any {it.hasRoute(RobotSayfası::class)} == true
        NavigationBarItem(
            icon = {
                Icon(
                    if (!bu) robot.kapalıyken_simge else robot.açıkken_simge,
                    contentDescription = robot.başlık
                )
            },
            label = {
                Text(robot.başlık)
            },
            selected = bu,
            onClick = {
                navController.navigate(RobotSayfası) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }
        )

        var bilet = Bilet()
        bu = currentDestination?.hierarchy?.any {it.hasRoute(BiletSayfası::class)} == true
        NavigationBarItem(
            icon = {
                Icon(
                    if (!bu) bilet.kapalıyken_simge else bilet.açıkken_simge,
                    contentDescription = bilet.başlık
                )
            },
            label = {
                Text(bilet.başlık)
            },
            selected = bu,
            onClick = {
                navController.navigate(BiletSayfası) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }
        )

        var mikrofon = Mikrofon()
        bu = currentDestination?.hierarchy?.any {it.hasRoute(MikrofonSayfası::class)} == true
        NavigationBarItem(
            icon = {
                Icon(
                    if (!bu) mikrofon.kapalıyken_simge else mikrofon.açıkken_simge,
                    contentDescription = mikrofon.başlık
                )
            },
            label = {
                Text(mikrofon.başlık)
            },
            selected = bu,
            onClick = {
                navController.navigate(MikrofonSayfası) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }
        )

        var kendim = Kendim()
        bu = currentDestination?.hierarchy?.any {it.hasRoute(KendimSayfası::class)} == true
        NavigationBarItem(
            icon = {
                Icon(
                    if (!bu) kendim.kapalıyken_simge else kendim.açıkken_simge,
                    contentDescription = kendim.başlık
                )
            },
            label = {
                Text(kendim.başlık)
            },
            selected = bu,
            onClick = {
                navController.navigate(KendimSayfası) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }
        )
        /*items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selection.value == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item
                    )
                },
                label = { Text(item) },
                selected = selection.value == index,
                onClick = { selection.intValue = index }
            )
        }*/
    }
}


