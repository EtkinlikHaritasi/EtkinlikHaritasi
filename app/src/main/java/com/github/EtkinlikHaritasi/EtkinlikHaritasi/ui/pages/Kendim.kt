package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.room.util.TableInfo
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.DateTimeStrings
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database.AppDatabase
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Participation
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.EventRepository
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.ParticipationRepository
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.Typography
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.collections.Map

class Kendim
{
    val açıkken_simge = Icons.Filled.AccountCircle
    val kapalıyken_simge = Icons.Outlined.AccountCircle
    val başlık = "Kendim"

    @Composable
    fun İçerik(modifier: Modifier, user: MutableState<User?>, loginToken: String,
               database: AppDatabase)
    {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var tbaIsOpen = remember { mutableStateOf<Boolean?>(null) }
        var events = remember { mutableStateOf<Map<Event, Participation>?>(null) }
        var refresh = remember { mutableStateOf<Boolean>(false) }

        LifecycleEventEffect(Lifecycle.Event.ON_START) {
            scope.launch {
                var partRepo = ParticipationRepository()
                var eventRepo = EventRepository(database.eventDao())

                var sorted = eventRepo.getEvents(loginToken).orEmpty().sortedByDescending {
                    DateTimeStrings.CalendarOfEvent(it)
                }

                var updated = mutableMapOf<Event, Participation>()
                for (e in sorted)
                {
                    var p = partRepo.getParticipation(user.value!!.id, e.eventId, loginToken)
                    if (p == null)
                        continue

                    updated.put(e, p)
                }

                events.value = updated
            }
        }

        LaunchedEffect(refresh) {
            if (refresh.value)
            {
                scope.launch {
                    var partRepo = ParticipationRepository()
                    var eventRepo = EventRepository(database.eventDao())

                    var sorted = eventRepo.getEvents(loginToken).orEmpty().sortedByDescending {
                        DateTimeStrings.CalendarOfEvent(it)
                    }

                    var updated = mutableMapOf<Event, Participation>()
                    for (e in sorted)
                    {
                        var p = partRepo.getParticipation(user.value!!.id, e.eventId, loginToken)
                        if (p == null)
                            continue

                        updated.put(e, p)
                    }

                    events.value = updated
                }
                refresh.value = false
            }
        }

        Scaffold (
            topBar = {
                Box(
                    modifier.fillMaxWidth().padding(12.dp)
                ) {
                    if (tbaIsOpen.value == null) {
                        Text(
                            text = "${user.value?.firstName} ${user.value?.lastName}",
                            style = Typography.displayLarge,
                            modifier = Modifier.padding(12.dp).align(Alignment.CenterStart)
                        )
                    }
                    else {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterStart),
                            onClick = {
                                tbaIsOpen.value = null
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Geri Düğmesi"
                            )
                        }
                        Text(
                            text = if (tbaIsOpen.value == true) "Katılacaklarım"
                                else "Katıldıklarım",
                            style = Typography.headlineLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = modifier.fillMaxSize().padding(paddingValues)
            )
            {
                if (tbaIsOpen.value == null) {
                    Column(
                        modifier = Modifier.fillMaxSize().align(Alignment.TopCenter)
                    ) {
                        TextButton(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            onClick = {
                                tbaIsOpen.value = true
                            }
                        ) {
                            Text(
                                text = "Katılacağım Etkinlikler",
                                style = Typography.titleLarge
                            )
                        }
                        TextButton(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            onClick = {
                                tbaIsOpen.value = false
                            }
                        ) {
                            Text(
                                text = "Katıldığım Etkinlikler",
                                style = Typography.titleLarge
                            )
                        }
                    }
                } else if (tbaIsOpen.value == true) {
                    LazyColumn (
                        modifier = Modifier.fillMaxSize().padding(28.dp)
                    ) {
                        for ((e,p) in events.value.orEmpty())
                        {
                            if (p.checkedIn)
                                continue

                            var cal = DateTimeStrings.CalendarOfEvent(e)
                            if (cal == null)
                                continue
                            if (cal < Calendar.getInstance())
                                continue

                            item {
                                Row (
                                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(0.8f)
                                    ) {
                                        Text(
                                            text = e.title,
                                            style = Typography.titleLarge,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Text(
                                            text = e.description,
                                            style = Typography.bodyLarge,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Text(
                                            text = DateTimeStrings.dMyHms(cal, ".", ".", " "),
                                            style = Typography.labelLarge,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Text(
                                            text = "Konum: ${e.lat}, ${e.lng}",
                                            style = Typography.labelMedium,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                    IconButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            scope.launch {
                                                var partRepo = ParticipationRepository()
                                                partRepo.deleteParticipation(p.userId, p.eventId,
                                                    loginToken)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Outlined.DeleteForever,
                                            contentDescription = "İmha Et",
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn (
                        modifier = Modifier.fillMaxSize().padding(28.dp)
                    ) {
                        for ((e,p) in events.value.orEmpty())
                        {
                            if (!p.checkedIn)
                                continue

                            var cal = DateTimeStrings.CalendarOfEvent(e)
                            if (cal == null)
                                continue

                            item {
                                Row (
                                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(0.8f)
                                    ) {
                                        Text(
                                            text = e.title,
                                            style = Typography.titleLarge,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Text(
                                            text = e.description,
                                            style = Typography.bodyLarge,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Text(
                                            text = DateTimeStrings.dMyHms(cal, ".", ".", " "),
                                            style = Typography.labelLarge,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Text(
                                            text = "Konum: ${e.lat}, ${e.lng}",
                                            style = Typography.labelMedium,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}