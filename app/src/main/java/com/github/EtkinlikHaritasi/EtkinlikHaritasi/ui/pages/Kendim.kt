package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.Typography

class Kendim
{
    val açıkken_simge = Icons.Filled.AccountCircle
    val kapalıyken_simge = Icons.Outlined.AccountCircle
    val başlık = "Kendim"

    @Composable
    fun İçerik(modifier: Modifier, user: MutableState<User>)
    {
        Box(
            modifier = modifier.fillMaxSize()
        )
        {
            Text(
                text = "${user.value.firstName} ${user.value.lastName}",
                style = Typography.displayLarge
            )
        }
    }
}