package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class Keşfet
{
    val açıkken_simge = Icons.Filled.Map
    val kapalıyken_simge = Icons.Outlined.Map
    val başlık = "Keşfet"

    @Composable
    fun İçerik(modifier: Modifier = Modifier)
    {
        Text(
            text = "Keşfet",
            modifier = modifier
        )
    }
}