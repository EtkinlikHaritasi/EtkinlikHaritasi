package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.NFCUtils
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import QRCodeScanner
import android.R
import android.app.Activity
import android.content.Context
import android.text.Layout
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.Connectivity.NearbyDeviceUtils
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.DateTimeStrings
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.Typography
import java.util.Calendar

class Bilet
{
    val açıkken_simge = Icons.Filled.LocalActivity
    val kapalıyken_simge = Icons.Outlined.LocalActivity
    val başlık = "Bilet"

    fun placeholderEvents(): List<Event>
    {
        return listOf(
            Event(
                eventId = 0,
                title = "Morbi Tortor",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                lat = 40.1,
                lng = 30.0,
                date = "2026-12-13",
                time = "11:11",
                organizerId = -2
            ),
            Event(
                eventId = 1,
                title = "Integer Lobortis",
                description = "Proin posuere augue nulla, ut scelerisque purus luctus id.",
                lat = 40.08945,
                lng = 29.890483,
                date = "2025-10-20",
                time = "12:22",
                organizerId = -3
            ),
            Event(
                eventId = 2,
                title = "ex volutpat pretium",
                description = "Nam porttitor mi vel purus efficitur finibus.",
                lat = 39.5674,
                lng = 35.2348,
                date = "2025-08-05",
                time = "11:11",
                organizerId = -2
            ),
            Event(
                eventId = 3,
                title = "Maecenas",
                description = "Curabitur a velit quis risus scelerisque scelerisque.",
                lat = 39.01234,
                lng = 27.9989,
                date = "2026-12-13",
                time = "11:11",
                organizerId = -1
            ),
            Event(
                eventId = 4,
                title = "Vulputate",
                description = "Suspendisse convallis efficitur lacinia.",
                lat = 40.1,
                lng = 30.0,
                date = "2026-01-10",
                time = "11:11",
                organizerId = -1
            ),
            Event(
                eventId = 4,
                title = "Vulputate non",
                description = "Suspendisse convallis efficitur lacinia.",
                lat = 40.1,
                lng = 30.0,
                date = "2024-01-10",
                time = "11:11",
                organizerId = -1
            )
        )
    }

    @Composable
    fun İçerik(modifier: Modifier, user: MutableState<User?>)
    {
        if (LocalActivity.current == null)
            return

        var activity = LocalActivity.current as Activity
        var context = LocalContext.current

        var isTicketController = remember { mutableStateOf<Boolean?>(null) }
        var onParOrOrgScreen = remember { mutableStateOf(true) }
        var nearbyUtils = remember { mutableStateOf<NearbyDeviceUtils?>(null) }

        var allEvents: List<Event> = placeholderEvents()

        var selectedEvent = remember { mutableStateOf<Event?>(null) }

        if (onParOrOrgScreen.value)
        {
            ParOrOrg(
                modifier = modifier,
                user = user,
                activity = activity,
                context = context,
                isTicketController = isTicketController,
                onParOrOrgScreen = onParOrOrgScreen
            )
        }
        else
        {
            if (selectedEvent.value == null)
            {
                EveSel(
                    modifier = modifier,
                    user = user,
                    activity = activity,
                    context = context,
                    isTicketController = isTicketController,
                    onParOrOrgScreen = onParOrOrgScreen,
                    allEvents = allEvents,
                    selectedEvent = selectedEvent
                )
            }
            else
            {
                NorQ(
                    modifier = modifier,
                    user = user,
                    activity = activity,
                    context = context,
                    isTicketController = isTicketController,
                    onParOrOrgScreen = onParOrOrgScreen,
                    selectedEvent = selectedEvent,
                    nearbyUtils = nearbyUtils
                )
            }
        }
    }

    @Composable
    fun NorQ(modifier: Modifier, user: MutableState<User?>, activity: Activity, context: Context,
             isTicketController: MutableState<Boolean?>, onParOrOrgScreen: MutableState<Boolean>,
             selectedEvent: MutableState<Event?>, nearbyUtils: MutableState<NearbyDeviceUtils?>)
    {
        Scaffold (
            modifier = modifier,
            topBar = {
                Box (
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                ) {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = {
                            selectedEvent.value = null
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Geri Düğmesi"
                        )
                    }
                    Text(
                        text = "${selectedEvent.value?.title}",
                        style = Typography.headlineLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        ) { paddingValues ->
            Column (
                modifier = modifier.fillMaxSize().padding(paddingValues)
            )
            {
                Box(
                    modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth()
                )
                {
                    TextButton(
                        modifier = Modifier.fillMaxSize(0.7f).align(Alignment.Center),
                        onClick = {
                            /*
                            if (selectedEvent.value != null) {
                                nearbyUtils.value = NearbyDeviceUtils(
                                    context = context,
                                    deviceName = "${selectedEvent.value?.eventId}-${user.value.id}"
                                )
                                if (isTicketController.value == true)
                                {
                                    nearbyUtils.value?.startAdvertising()
                                }
                                else if (isTicketController.value == false)
                                {
                                    nearbyUtils.value?.startDiscovery()
                                }
                                else
                                {
                                    nearbyUtils.value = null
                                }
                            }
                             */
                        },
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column {
                            Icon(
                                Icons.Outlined.Contactless,
                                contentDescription = "Sensör simgesi",
                                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxSize(0.45f)
                            )
                            Text(
                                text = "Yakındaki Cihazlar",
                                style = Typography.headlineLarge
                            )
                        }
                    }
                }
                HorizontalDivider(thickness = Dp.Hairline)
                Box(
                    modifier = Modifier.fillMaxSize()
                )
                {
                    TextButton(
                        modifier = Modifier.fillMaxSize(0.7f).align(Alignment.Center),
                        onClick = {  },
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column {
                            Icon(
                                Icons.Filled.QrCodeScanner,
                                contentDescription = "QR kod simgesi",
                                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxSize(0.45f)
                            )
                            Text(
                                text = "QR Bilet",
                                style = Typography.headlineLarge
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun EveSel(modifier: Modifier, user: MutableState<User?>, activity: Activity, context: Context,
               isTicketController: MutableState<Boolean?>, onParOrOrgScreen: MutableState<Boolean>,
               allEvents: List<Event>, selectedEvent: MutableState<Event?>)
    {
        if (isTicketController.value == null)
            onParOrOrgScreen.value = true

        var events = allEvents.filter {
            var cal = DateTimeStrings.CalendarOfEvent(event = it)
            cal != null
                    && cal >= Calendar.getInstance()
                    && if (isTicketController.value!!) it.organizerId == user.value?.id
                    else it.organizerId != user.value?.id
        }.sortedBy {
            DateTimeStrings.CalendarOfEvent(event = it)
        }

        Scaffold (
            modifier = modifier,
            topBar = {
                Box (
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                ) {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = {
                            selectedEvent.value = null
                            onParOrOrgScreen.value = true
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Geri Düğmesi"
                        )
                    }
                    Text(
                        text = if (isTicketController.value!!) "Bilet Kontrol Et"
                            else "Etkinliğe Gir",
                        style = Typography.headlineLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                items (events.size) { i ->
                    var cal = DateTimeStrings.CalendarOfEvent(events[i])
                    Row (
                       modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                selectedEvent.value = events[i]
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = events[i].title,
                                    style = Typography.titleLarge,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Text(
                                    text = events[i].description,
                                    style = Typography.bodyLarge,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Text(
                                    text = DateTimeStrings.dMyHms(cal!!, ".", ".", " "),
                                    style = Typography.labelLarge,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Text(
                                    text = "Konum: ${events[i].lat}, ${events[i].lng}",
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

    @Composable
    fun ParOrOrg(modifier: Modifier, user: MutableState<User?>, activity: Activity, context: Context,
                 isTicketController: MutableState<Boolean?>, onParOrOrgScreen: MutableState<Boolean>)
    {
        Column (
            modifier = modifier.fillMaxSize()
        )
        {
            Box(
                modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth()
            )
            {
                TextButton(
                    modifier = Modifier.fillMaxSize(0.7f).align(Alignment.Center),
                    onClick = {
                        isTicketController.value = true
                        onParOrOrgScreen.value = false
                    },
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column {
                        Icon(
                            Icons.Outlined.Sensors,
                            contentDescription = "Sensör simgesi",
                            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxSize(0.45f)
                        )
                        Text(
                            text = "Bilet Kontrol Et",
                            style = Typography.headlineLarge
                        )
                    }
                }
            }
            HorizontalDivider(thickness = Dp.Hairline)
            Box(
                modifier = Modifier.fillMaxSize()
            )
            {
                TextButton(
                    modifier = Modifier.fillMaxSize(0.7f).align(Alignment.Center),
                    onClick = {
                        isTicketController.value = false
                        onParOrOrgScreen.value = false
                    },
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column {
                        Icon(
                            Icons.Outlined.ConfirmationNumber,
                            contentDescription = "Bilet simgesi",
                            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxSize(0.45f)
                        )
                        Text(
                            text = "Etkinliğe Gir",
                            style = Typography.headlineLarge
                        )
                    }
                }
            }
        }
    }

    @ExperimentalGetImage
    @Composable
    fun OldNFQ(modifier: Modifier, user: MutableState<User>)
    {
        if (LocalActivity.current != null)
        {
            var activity = LocalActivity.current as Activity
            LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
                NFCUtils.initialize(activity)
            }

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                NFCUtils.enableForegroundDispatch(activity)
            }

            LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
                NFCUtils.disableForegroundDispatch(activity)
            }


            var context = LocalContext.current
            var QRScannerOn = remember { mutableStateOf(false) }
            var qrCodeResult by remember { mutableStateOf<String?>(null) }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) {}

            Column(modifier = modifier)
            {

                Column(modifier = Modifier.fillMaxHeight(0.7f).fillMaxWidth())
                {
                    Box(
                        modifier = Modifier.fillMaxSize(0.9f).clip(RoundedCornerShape(16.dp))
                            .align(Alignment.CenterHorizontally)
                    )
                    {
                        if (!QRScannerOn.value)
                        {
                            Icon(
                                Icons.Outlined.Contactless,
                                "NFC ile bilet okuma",
                                modifier = Modifier.fillMaxSize(0.7f)
                                    .align(Alignment.Center)
                            )
                        }
                        else
                        {
                            QRCodeScanner { result ->
                                qrCodeResult = result
                                Log.d("QR Scanner", result)
                                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth())
                {
                    Text(
                        text = if (!QRScannerOn.value) "NFC bilet okunuyor"
                        else "QR bilet okunuyor",
                        style = Typography.headlineLarge,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                    FilledTonalButton(
                        onClick = {
                            nfqSwitch(QRScannerOn, activity, context, permissionLauncher)
                        },
                        modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.Center)
                    )
                    {
                        Icon(
                            if (!QRScannerOn.value) Icons.Outlined.QrCodeScanner
                            else Icons.Outlined.Nfc,
                            if (!QRScannerOn.value) "QR kod okuyucu"
                            else "NFC okuyucu",
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                        )
                        Text(
                            text = if (!QRScannerOn.value) "QR bilet oku"
                            else "NFC bilet oku",
                            style = Typography.bodyLarge,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }

    fun nfqSwitch(QRScannerOn: MutableState<Boolean>, activity: Activity, context: Context,
                  permissionLauncher: ManagedActivityResultLauncher<String, Boolean>)
    {
        if (QRScannerOn.value)
        {
            // Bu kısım niyeyse çalışmıyor.
            NFCUtils.enableForegroundDispatch(activity)
        }
        else
        {
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!permissionGranted)
            {
                permissionLauncher.launch(Manifest.permission.CAMERA)
                return
            }

            NFCUtils.disableForegroundDispatch(activity)
        }

        QRScannerOn.value = !QRScannerOn.value

    }

}