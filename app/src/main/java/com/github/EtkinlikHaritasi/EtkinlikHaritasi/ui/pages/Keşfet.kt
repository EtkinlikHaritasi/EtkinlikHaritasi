package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.fonts.FontStyle
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.DateTimeStrings
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.LocationUtils
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.*
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database.*
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.Typography
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.*
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.utils.DirectionApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.security.SecureRandom

class Keşfet
{
    val açıkken_simge = Icons.Filled.Map
    val kapalıyken_simge = Icons.Outlined.Map
    val başlık = "Keşfet"

    @ExperimentalMaterial3Api
    @Composable
    fun İçerik(modifier: Modifier = Modifier, user: MutableState<User?>,
               loginToken: String, database: AppDatabase)
    {
        val context = LocalContext.current
        val fusedLocationProviderClient = remember {
            LocationUtils.getFusedLocationProviderClient(context)
        }
        var current_location = remember { mutableStateOf(LocationUtils.lastKnownLocation) }
        var focus_location: LatLng = LatLng(40.76, 29.9)
        var locationGotClicked = remember { mutableStateOf(false) }
        var clickedLocation = remember { mutableStateOf(focus_location) }
        var events = remember { mutableStateOf<List<Event>?>(null) }
        var participations by remember {
            mutableStateOf<List<Participation>>(emptyList<Participation>())
        }
        var eventInfoDialogOpen = remember { mutableStateOf(false) }
        var clickedEvent = remember { mutableStateOf<Event?>(null) }

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        var routePoints = remember { mutableStateOf(listOf<LatLng>()) }
        var routeShownBool = remember { mutableStateOf(false) }


        LifecycleEventEffect(Lifecycle.Event.ON_START) {
            scope.launch {
                var eventRepo = EventRepository(database.eventDao())
                eventRepo.refreshEventsFromApi(loginToken)

                events.value = eventRepo.getEvents(loginToken)?.filter {
                    var cal = DateTimeStrings.CalendarOfEvent(it)
                    cal != null && cal > Calendar.getInstance()
                }?.sortedBy {
                    DateTimeStrings.CalendarOfEvent(it)
                }
                                //database.eventDao().getAllEvents().value

                var participationRepo = ParticipationRepository()
                participations = participationRepo.getParticipationsByUser(user.value!!.id, loginToken)
            }
        }

        Box(
            modifier = modifier
        ) {
            BottomSheetScaffold(
                modifier = Modifier.fillMaxWidth(),
                sheetContent = {
                    Row()
                    {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = Dp(10f), vertical = Dp(5f)),
                            verticalArrangement = Arrangement.spacedBy(space = Dp(20f))
                        )
                        {
                            item {
                                Row()
                                {
                                    Text(
                                        text = "Yakındaki Etkinlikler",
                                        style = Typography.headlineMedium,
                                    )
                                }
                            }
                            items(events.value.orEmpty().size) { i ->
                                EventListRow(
                                    event = events.value!![i],
                                    clickedEvent = clickedEvent,
                                    eventInfoDialogOpen = eventInfoDialogOpen
                                )
                            }
                        }
                    }
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                }
            )
            {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = MapProperties(isMyLocationEnabled = true),
                    contentPadding = PaddingValues(bottom = Dp(50f)),
                    cameraPositionState = rememberCameraPositionState() {
                        position = CameraPosition.fromLatLngZoom(focus_location, 11.0f)
                    },
                    onMyLocationButtonClick = {
                        routePoints.value = emptyList<LatLng>()
                        routeShownBool.value = false
                        false
                    },
                    onMapLongClick = { selection ->
                        locationGotClicked.value = true
                        clickedLocation.value = selection
                    },
                    onMapLoaded = { }
                )
                {
                    if (events.value != null)
                    {
                        for (x in events.value)
                        {
                            AdvancedMarker(
                                state = rememberUpdatedMarkerState(LatLng(x.lat, x.lng)),
                                contentDescription = "${x.title} Etkinliği",
                                tag = x.eventId,
                                title = x.title,
                                snippet = x.description,
                                draggable = false,
                                onClick = { marker ->
                                    eventInfoDialogOpen.value = true
                                    clickedEvent.value = x
                                    true
                                }
                            )
                        }

                        if(routeShownBool.value)
                        {
                            Polyline(
                                points = routePoints.value,
                                color = Color.Blue,
                                width = 8f
                            )
                        }
                    }
                }

                if (eventInfoDialogOpen.value && clickedEvent.value != null)
                {
                    EtkinlikBilgisi(
                        event = clickedEvent.value!!,
                        eventInfoDialogOpen = eventInfoDialogOpen,
                        user =  user.value,
                        loginToken =  loginToken,
                        participations = participations,
                        context = context,
                        scope = scope,
                        routePoints = routePoints,
                        routeShown = routeShownBool
                    )
                }

                if (locationGotClicked.value)
                {
                    YeniEtkinlikOluşturucu(
                        konum = clickedLocation.value,
                        locationGotClicked = locationGotClicked,
                        db = database,
                        scope = scope,
                        user = user,
                        loginToken = loginToken,
                        events = events
                    )
                }

            }

        }

    }

    @Composable
    fun EtkinlikBilgisi(event: Event, eventInfoDialogOpen: MutableState<Boolean>,
                        user: User?, loginToken: String, participations: List<Participation>,
                        context: Context, scope: CoroutineScope,
                        routePoints: MutableState<List<LatLng>>, routeShown: MutableState<Boolean>)
    {
        var date = DateTimeStrings.yMd_toCalendar(event.date, "-")!!
        val Hm = event.time.split(":")
        date.set(Calendar.HOUR_OF_DAY, Hm[0].toInt())
        date.set(Calendar.MINUTE, Hm[1].toInt())
        AlertDialog(
            icon = {
                Icon(
                    Icons.Filled.Event,
                    contentDescription = "Etkinlik"
                )
            },
            title = {
                Text(event.title)
            },
            text = {
                Column()
                {
                    Text(event.description)
                    OutlinedTextField(
                        value = DateTimeStrings.dMyHms(date, ".", ".", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tarih") }
                    )
                    FilledTonalButton(
                        modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.CenterHorizontally)
                            .padding(6.dp),
                        onClick = {
                            val location_latitude = LocationUtils.lastKnownLocation!!.latitude;
                            val location_longtitude = LocationUtils.lastKnownLocation!!.longitude;
                            val destination_latitude = event.lat;
                            val destination_longtitude = event.lng;

                            scope.launch{
                                routePoints.value = DirectionApi.fetchRoutePoints(
                                    context.applicationInfo.metaData
                                        .getString("com.google.android.geo.API_KEY")!!,
                                    LatLng(location_latitude, location_longtitude),
                                    LatLng(destination_latitude, destination_longtitude))
                                routeShown.value = true
                                eventInfoDialogOpen.value = false
                            }

                        }
                    ) {
                        Text(text = "Git")
                    }
                }
            },
            onDismissRequest = {
                eventInfoDialogOpen.value = false
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        eventInfoDialogOpen.value = false
                    }
                ) {
                    Text(text = "Vazgeç")
                }
            },
            confirmButton = {
                if (user?.id != event.organizerId && participations.none{ it.userId == user?.id }) {
                    TextButton(
                        onClick = {
                            var new_part = Participation(
                                userId = user!!.id,
                                eventId = event.eventId,
                                checkedIn = false
                            )
                            scope.launch {
                                ParticipationRepository().addParticipation(new_part, loginToken)
                            }
                            eventInfoDialogOpen.value = false
                        }
                    ) {
                        Text(text = "Katıl")
                    }
                }
            },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = true
            )
        )
    }

    @ExperimentalMaterial3Api
    @Composable
    fun YeniEtkinlikOluşturucu(konum: LatLng, locationGotClicked: MutableState<Boolean>,
                               db: AppDatabase, scope: CoroutineScope, user: MutableState<User?>,
                               loginToken: String, events: MutableState<List<Event>?>)
    {
        var ad = remember { mutableStateOf("") }
        var açıklama = remember { mutableStateOf("") }
        var seçilen_tarih = remember { mutableStateOf<Long?>(null) }
        var seçilen_saat = remember { mutableStateOf<Int?>(null) }
        var seçilen_dakika = remember { mutableStateOf<Int?>(null) }

        var showDatePicker = remember { mutableStateOf(false) }
        var showTimePicker = remember { mutableStateOf(false) }

        var eventRepo = EventRepository(db.eventDao())

        Dialog(
            onDismissRequest = {
                locationGotClicked.value = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = true
            )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.7f),
                shape = RoundedCornerShape(28.dp) // Extra Large
            )
            {
                Box(
                    modifier = Modifier.fillMaxSize().padding(20.dp)
                )
                {
                    Column(
                        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
                    )
                    {

                        Text(
                            text = "Yeni Etkinlik",
                            style = Typography.headlineLarge
                        )

                        OutlinedTextField(
                            value = "${konum.latitude}",
                            onValueChange = {},
                            label = {
                                Text(text = "Enlem")
                            },
                            readOnly = true,
                            enabled = false
                        )
                        OutlinedTextField(
                            value = "${konum.longitude}",
                            onValueChange = {},
                            label = {
                                Text(text = "Boylam")
                            },
                            readOnly = true,
                            enabled = false
                        )
                        OutlinedTextField(
                            value = ad.value,
                            onValueChange = {
                                ad.value = it
                            },
                            label = {
                                Text(text = "Etkinlik Adı")
                            },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = açıklama.value,
                            onValueChange = {
                                açıklama.value = it
                            },
                            label = {
                                Text(text = "Açıklama")
                            },
                            maxLines = 5
                        )

                        // Tarih Seçici
                        OutlinedTextField(
                            value = if (seçilen_tarih.value != null)
                                DateTimeStrings.dMy(seçilen_tarih.value!!, ".")
                            else "",
                            onValueChange = {},
                            label = {
                                Text(text = "Tarih")
                            },
                            readOnly = true,
                            modifier = Modifier.pointerInput(seçilen_tarih.value) {
                                awaitEachGesture {
                                    awaitFirstDown(pass = PointerEventPass.Initial)
                                    val upEvent =
                                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                    if (upEvent != null) {
                                        showDatePicker.value = true
                                    }
                                }
                            }
                        )

                        // Saat seçici
                        OutlinedTextField(
                            value = if (seçilen_saat.value != null && seçilen_dakika.value != null)
                                "${seçilen_saat.value}.${seçilen_dakika.value}"
                            else "",
                            onValueChange = {},
                            label = {
                                Text(text = "Saat")
                            },
                            readOnly = true,
                            modifier = Modifier.pointerInput(
                                seçilen_saat.value, seçilen_dakika.value
                            ) {
                                awaitEachGesture {
                                    awaitFirstDown(pass = PointerEventPass.Initial)
                                    val upEvent =
                                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                    if (upEvent != null) {
                                        showTimePicker.value = true
                                    }
                                }
                            }
                        )


                        if (showDatePicker.value) {
                            DatePickerModal(
                                onDateSelected = { selection ->
                                    if (selection != null)
                                        seçilen_tarih.value = selection
                                },
                                onDismiss = {
                                    showDatePicker.value = false
                                }
                            )
                        }

                        if (showTimePicker.value) {
                            val currentTime = Calendar.getInstance()

                            val timePickerState = rememberTimePickerState(
                                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                                initialMinute = currentTime.get(Calendar.MINUTE),
                                is24Hour = true,
                            )

                            TimePickerDialog(
                                onDismiss = {
                                    showTimePicker.value = false
                                },
                                onConfirm = {
                                    seçilen_saat.value = timePickerState.hour
                                    seçilen_dakika.value = timePickerState.minute
                                    showTimePicker.value = false
                                }
                            ) {
                                TimePicker(
                                    state = timePickerState,
                                )
                            }
                        }

                    }

                    Row(
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                    {
                        TextButton(
                            onClick = {
                                locationGotClicked.value = false
                            }
                        ) {
                            Text(text = "Vazgeç")
                        }

                        TextButton(
                            onClick = {
                                if (seçilen_tarih.value != null && seçilen_saat.value != null
                                    && seçilen_dakika.value != null)
                                {
                                    if (ad.value.isNotBlank() && açıklama.value.isNotBlank())
                                    {
                                        var new_event = Event(
                                            eventId = SecureRandom().nextInt(),
                                            title = ad.value,
                                            description = açıklama.value,
                                            lat = konum.latitude,
                                            lng = konum.longitude,
                                            date = DateTimeStrings.yMd(seçilen_tarih.value!!, "-"),
                                            time = "${seçilen_saat.value}:${seçilen_dakika.value}",
                                            organizerId = user.value!!.id
                                        )

                                        scope.launch {
                                            eventRepo.addEvent(new_event, loginToken)
                                            eventRepo.refreshEventsFromApi(loginToken)
                                            events.value = eventRepo.getEvents(loginToken)
                                        }
                                    }
                                }
                                locationGotClicked.value = false
                            }
                        ) {
                            Text(text = "Oluştur")
                        }
                    }
                }
            }
        }
    }

    @ExperimentalMaterial3Api
    @Composable
    fun DatePickerModal(
        onDateSelected: (Long?) -> Unit,
        onDismiss: () -> Unit
    ) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }) {
                    Text("Tarihi Seç")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Vazgeç")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    @ExperimentalMaterial3Api
    @Composable
    fun TimePickerDialog(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit,
        content: @Composable () -> Unit
    )
    {
        AlertDialog(
            onDismissRequest = onDismiss,
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(text = "Vazgeç")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm
                ) {
                    Text(text = "Saati Seç")
                }
            },
            text = {
                content()
            }
        )
    }

    fun konumum_düğmesi(context: Context,
                        fusedLocationProviderClient: FusedLocationProviderClient):Boolean
    {
        if (!LocationUtils.isLocationEnabled(context))
        {
            LocationUtils.showLocationServicePrompt(context)
        }
        else
        {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
            {
                //LocationUtils.fetchLocation(context, fusedLocationProviderClient)
            }
            else
            {
                Toast.makeText(context, "Konum izni verilmedi", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    @Composable
    fun EventListRow(event: Event, clickedEvent: MutableState<Event?>,
                     eventInfoDialogOpen: MutableState<Boolean>)
    {
        var cal = DateTimeStrings.CalendarOfEvent(event)
        Row (
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    clickedEvent.value = event
                    eventInfoDialogOpen.value = true
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = event.title,
                        style = Typography.titleLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = event.description,
                        style = Typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = DateTimeStrings.dMyHms(cal!!, ".", ".", " "),
                        style = Typography.labelLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "Konum: ${event.lat}, ${event.lng}",
                        style = Typography.labelMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}