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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EtkinlikHaritasiTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AltMenü()
                    }
                ) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
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
fun AltMenü(modifier: Modifier = Modifier) {
    //var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Keşfet", "Bilmiyorum", "Kendim")
    val selectedIcons = listOf(Icons.Filled.Map, Icons.Filled.QuestionMark, Icons.Filled.AccountCircle)
    val unselectedIcons =
        listOf(Icons.Outlined.Map, Icons.Outlined.QuestionMark, Icons.Outlined.AccountCircle)

    NavigationBar {

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        /*if (selectedItem == index) selectedIcons[index] else*/ unselectedIcons[index],
                        contentDescription = item
                    )
                },
                label = { Text(item) },
                selected = false,//selectedItem == index,
                onClick = {}//{ selectedItem = index }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EtkinlikHaritasiTheme {
        Greeting("Android")
    }
}