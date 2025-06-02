package com.github.EtkinlikHaritasi.EtkinlikHaritasi.Connectivity

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class NearbyDeviceUtils(private val context: Context,
                        private val deviceName: String,
                        private val strategy: Strategy = Strategy.P2P_POINT_TO_POINT)
{
    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val discoveredDevices = mutableListOf<DiscoveredEndpointInfo>()

    //Alıcı telefonun çalıştırdığı task alıcıdan gelen isteği otomatik kabul ediyor.
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback()
    {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo)
        {
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution)
        {
            if (result.status.isSuccess)
            {
                Log.d("a", "Bağlantı başarılı: $endpointId")
            }
            else
            {
                Log.d("a", "Bağlantı başarısız: ${result.status.statusMessage}")
            }
        }

        override fun onDisconnected(endpointId: String)
        {
            Log.d("a","Bağlantı kesildi: $endpointId")
        }
    }

    //Arayıcı telefonun çalıştırdığı task durmadan alıcı telefon arıyıp bulursa listeye ekliyor ama doğru şeyleri mi ekliyor deneyemedim
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback()
    {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo)
        {
            Log.d("a","Cihaz bulundu: ${info.endpointName}")

            if (discoveredDevices.none { it == info })
            {
                discoveredDevices.add(info)
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
