package io.tipy.spgil
import android.content.*

import android.util.Log

class SensorRestarterBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(SensorRestarterBroadcastReceiver::class.java.simpleName, "Service Stops! Oooooooooooooppppssssss!!!!")
        context.startService(Intent(context, UpdateService::class.java))
    }
}