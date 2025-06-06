package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database.AppDatabase
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.Typography

class Kendim
{
    val açıkken_simge = Icons.Filled.AccountCircle
    val kapalıyken_simge = Icons.Outlined.AccountCircle
    val başlık = "Kendim"

    @Composable
    fun İçerik(modifier: Modifier, user: MutableState<User?>, loginToken: String,
               database: AppDatabase)
    {
        var tbaIsOpen = remember { mutableStateOf<Boolean>(false) }

        Box(
            modifier = modifier.fillMaxSize()
        )
        {
            if (!tbaIsOpen.value) {
                Text(
                    text = "${user.value?.firstName} ${user.value?.lastName}",
                    style = Typography.displayLarge,
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart)
                )
                Button(
                    modifier = Modifier.fillMaxWidth().padding(12.dp).align(Alignment.BottomCenter),
                    onClick = {}
                ) {
                    Text(
                        text = "Katılacağım Etkinlikler",
                        style = Typography.titleLarge
                    )
                }
            }
        }
    }

    @Composable
    fun EventToBeAttended(modifier: Modifier, user: User, loginToken: String,
                          tbaIsOpen: MutableState<Boolean>)
    {}
}