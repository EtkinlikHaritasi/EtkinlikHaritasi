package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import android.widget.Toast

object NFCUtils
{
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var intentFilters: Array<IntentFilter>? = null

    fun initialize(activity: Activity)
    {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)

        if (nfcAdapter == null)
        {
            Toast.makeText(activity, "Bu cihaz NFC desteklemiyor", Toast.LENGTH_SHORT).show()
            return
        }

        if (!nfcAdapter!!.isEnabled) //Baştaki Ünlem not için sondaki 2 ünlem not null için varlar
        {
            Toast.makeText(activity, "Lütfen NFC'yi açın", Toast.LENGTH_SHORT).show()
        }

        val intent = Intent(activity, activity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        pendingIntent = PendingIntent.getActivity(
            activity, 0, intent, PendingIntent.FLAG_MUTABLE
        )

        val filter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        intentFilters = arrayOf(filter)
    }

    fun enableForegroundDispatch(activity: Activity)
    {
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, intentFilters, null)
    }

    fun disableForegroundDispatch(activity: Activity)
    {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    fun processNfcIntent(activity: Activity, intent: Intent)
    {
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TECH_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED)
        {
            val tag: Tag? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
            {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            }
            else
            {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }



            tag?.let {
                val tagId = it.id.joinToString("")
                {
                    byte -> "%02x".format(byte)
                }

                Toast.makeText(activity, "NFC Tag ID: $tagId", Toast.LENGTH_LONG).show()
            } ?: run {
                Toast.makeText(activity, "Tag okunamadı", Toast.LENGTH_SHORT).show()
            }

        }
    }


}