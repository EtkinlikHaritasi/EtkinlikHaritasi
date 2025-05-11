package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class Robot
{
    val açıkken_simge = Icons.Filled.SmartToy
    val kapalıyken_simge = Icons.Outlined.SmartToy
    val başlık = "Robot"

    @Composable
    fun İçerik(modifier: Modifier = Modifier)
    {
        Text(
            text = "Robot",
            modifier = modifier
        )
    }
}