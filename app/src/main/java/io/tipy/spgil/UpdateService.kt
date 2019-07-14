package io.tipy.spgil

import android.content.Intent
import android.os.IBinder
import android.R.string.cancel
import android.app.Service
import android.content.Context
import android.net.Uri
import android.support.annotation.Nullable
import android.support.media.tv.PreviewProgram
import android.support.media.tv.TvContractCompat
import android.util.Log
import io.tipy.scootaroundorid.data.network.TvGuideService
import io.tipy.spgil.models.Channels
import io.tipy.spgil.models.Program
import io.tipy.spgil.network.response.Kan11Response
import io.tipy.spgil.network.response.Keseht12Response
import io.tipy.spgil.network.response.Reshet13Response
import io.tipy.spgil.network.response.Show
import io.tipy.spgil.ui.MainActivity
import io.tipy.spgil.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class UpdateService : Service {

    private var counter = 0
    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null

    constructor() : super() {
        Log.i("HERE", "here I am!")
    }



    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startmTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("EXIT", "ondestroy!")
        val broadcastIntent = Intent(this, UpdateService::class.java)

        sendBroadcast(broadcastIntent)
        stopmTimertask()
    }

    fun startmTimer() {
        //set a new mTimer
        mTimer = Timer()

        //initialize the mTimerTask's job
        initializemTimerTask()

        //schedule the mTimer, to wake up every 1 second
        mTimer!!.schedule(mTimerTask, SERVICE_UPDATE_INTERVAL_IN_SEC * 1000L)

        Log.i("in mTimer", "Timer started")
    }

    /**
     * it sets the mTimer to print the counter every x seconds
     */
    fun initializemTimerTask() {
        mTimerTask = object : TimerTask() {
            override fun run() {
                Log.i("in mTimer", "in mTimer ++++  " + counter++)

//                upadteChannels()
            }
        }
    }

    /**
     * not needed
     */
    fun stopmTimertask() {
        //stop the mTimer, if it's not already null
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        const val SERVICE_UPDATE_INTERVAL_IN_SEC = 10
    }
}