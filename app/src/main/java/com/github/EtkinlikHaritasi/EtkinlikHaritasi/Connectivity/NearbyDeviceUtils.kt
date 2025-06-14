package com.github.EtkinlikHaritasi.EtkinlikHaritasi.Connectivity

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlin.io.path.fileVisitor

class NearbyDeviceUtils(private val context: Context,
                        private val deviceName: String,
                        //var discoveries: SnapshotStateList<DiscoveredEndpointInfo>,
                        var recievedData: MutableState<String?>,
                        var connected: MutableState<Boolean>,
                        var endpoint: MutableState<String?>,
                        private val strategy: Strategy = Strategy.P2P_POINT_TO_POINT)
{
    private var connectionsClient = Nearby.getConnectionsClient(context)

    var discoveries = mutableStateListOf<DiscoveredEndpointInfo>()



    //Alıcı telefonun çalıştırdığı task alıcıdan gelen isteği otomatik kabul ediyor.
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback()
    {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo)
        {
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution)
        {
            Log.d("a", "Bağlantı durumu: ${result.status}")
            if (result.status.isSuccess)
            {
                Log.d("a", "Bağlantı başarılı: $endpointId")
                endpoint.value = endpointId
                connected.value = true
            }
            else
            {
                Log.d("a", "Bağlantı başarısız: ${result.status.statusMessage}")
                endpoint.value = null
                connected.value = false
            }
        }

        override fun onDisconnected(endpointId: String)
        {
            Log.d("a","Bağlantı kesildi: $endpointId")
            endpoint.value = null
            connected.value = false
        }
    }

    //Arayıcı telefonun çalıştırdığı task durmadan alıcı telefon arıyıp bulursa listeye ekliyor ama doğru şeyleri mi ekliyor deneyemedim
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback()
    {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo)
        {
            Log.d("a","Cihaz bulundu: ${info.endpointName}")

            if (discoveries.none { it == info })
            {
                connectionsClient.requestConnection(info.endpointName, endpointId,
                    connectionLifecycleCallback)
                discoveries.add(info)
                Log.d("a", discoveries.toString())
                Log.d("a", "Yeni cihaz eklendi: ${info.endpointName} - $endpointId")
            }
        }

        override fun onEndpointLost(endpointId: String)
        {
            Log.d("a","Cihaz kayboldu: $endpointId")
        }
    }

    //Alıcı Telefonun Çalıştıracağı Fonksiyon
    fun startAdvertising()
    {
        val options = AdvertisingOptions.Builder().setStrategy(strategy).build()
        connectionsClient.startAdvertising(
            deviceName,
            context.packageName,
            connectionLifecycleCallback,
            options
        ).addOnSuccessListener {
            Log.d("Advertising","Advertising başladı")
        }.addOnFailureListener {
            Log.d("Advertising","Advertising hatası: ${it.message}")
        }
    }

    //Arayıcı Telefonun Çalıştıracağı Fonksiyon
    fun startDiscovery()
    {
        val options = DiscoveryOptions.Builder().setStrategy(strategy).build()
        connectionsClient.startDiscovery(
            context.packageName,
            endpointDiscoveryCallback,
            options
        ).addOnSuccessListener {
            Log.d("Discovery","Discovery başladı")
        }.addOnFailureListener {
            Log.d("Discovery","Discovery hatası: ${it.message}")
        }
    }


    private val payloadCallback = object : PayloadCallback()
    {
        override fun onPayloadReceived(endpointId: String, payload: Payload)
        {
            val data = payload.asBytes()?.toString(Charsets.UTF_8)
            recievedData.value = data
            Log.d("a","Veri alındı: $data")
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    fun sendData(endpointId: String, message: String)
    {
        val payload = Payload.fromBytes(message.toByteArray())
        connectionsClient.sendPayload(endpointId, payload)
    }
}
