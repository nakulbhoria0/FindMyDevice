package com.nakulbhoria.findmydevice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!!.action.equals(Intent.ACTION_BOOT_COMPLETED)){
            val intentService = Intent(context,MyService::class.java)
            context!!.startService(intentService)
        }
    }
}