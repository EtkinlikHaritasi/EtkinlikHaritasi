package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.EtkinlikHaritasiTheme
import androidx.compose.material3.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Serializable
object KeşfetSayfası
@Serializable
object RobotSayfası
@Serializable
object KendimSayfası


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EtkinlikHaritasiTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AltMenü(navController)
                    }
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = KeşfetSayfası)
                    {
                        composable<KeşfetSayfası> { Keşfet().İçerik(Modifier.padding(innerPadding)) }
                        composable<RobotSayfası> { Robot().İçerik(Modifier.padding(innerPadding)) }
                        composable<KendimSayfası> { Kendim().İçerik(Modifier.padding(innerPadding)) }
                    }
                    /*Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )*/
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EtkinlikHaritasiTheme {
        Greeting("Android")
    }
}