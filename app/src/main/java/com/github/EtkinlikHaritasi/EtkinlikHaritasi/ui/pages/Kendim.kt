package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class Kendim
{
    val açıkken_simge = Icons.Filled.AccountCircle
    val kapalıyken_simge = Icons.Outlined.AccountCircle
    val başlık = "Kendim"

    @Composable
    fun İçerik(modifier: Modifier)
    {
        Text(
            text = "Kendim",
            modifier = modifier
        )
    }
}