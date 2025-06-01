package com.github.EtkinlikHaritasi.EtkinlikHaritasi.utils

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object DirectionApi
{
    suspend fun fetchRoutePoints(apiKey: String, origin: LatLng?, destination: LatLng): List<LatLng> {
        return withContext(Dispatchers.IO)
        {
            val client = OkHttpClient()

            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${origin!!.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    "&key=$apiKey"

            try
            {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext emptyList<LatLng>()

                val json = JSONObject(body)
                val routes = json.optJSONArray("routes") ?: return@withContext emptyList<LatLng>()
                if (routes.length() == 0) return@withContext emptyList<LatLng>()

                val overviewPolyline = routes.getJSONObject(0).getJSONObject("overview_polyline")
                val encodedPoints = overviewPolyline.getString("points")
                decodePolyline(encodedPoints)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    //Ucube bir şey ordan burdan buldum tekrar isterseniz yapamam anlatamam da boş verin askdopawkdpoa
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val length = encoded.length
        var lat = 0
        var lng = 0

        while (index < length) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }
        return poly
    }
}
