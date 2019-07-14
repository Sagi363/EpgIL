/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.tipy.spgil.ui

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.media.tv.Channel
import android.support.media.tv.ChannelLogoUtils
import android.support.media.tv.PreviewProgram
import android.support.media.tv.TvContractCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import io.tipy.scootaroundorid.data.network.TvGuideService
import io.tipy.spgil.models.Channels
import io.tipy.spgil.PreferenceHelper
import io.tipy.spgil.models.Channels.*
import io.tipy.spgil.models.Program
import io.tipy.spgil.R
import io.tipy.spgil.network.response.Kan11Response
import io.tipy.spgil.network.response.Reshet13Response
import io.tipy.spgil.network.response.Show
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import io.tipy.spgil.UpdateService
import android.app.ActivityManager
import android.support.v4.app.NotificationCompat.getChannelId
import android.util.Log
import io.tipy.spgil.network.response.Keseht12Response
import io.tipy.spgil.utils.*

class MainActivity : Activity() {

    private var mServiceIntent: Intent? = null
    private var mUpdateService: UpdateService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if channel exist
        if (!isChannelsCreated(baseContext))
            createChannels()

        if (!isProgramsCreated(baseContext))
            createProgramsOnHomescreen()

        upadteChannels()
//        mUpdateService = UpdateService()
//        mUpdateService?.let {
//            mServiceIntent = Intent(baseContext, it.javaClass)
//            if (!isMyServiceRunning(it.javaClass)) {
//                startService(mServiceIntent)
//            }
//        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.i("isMyServiceRunning?", false.toString() + "")
        return false
    }

    private fun createProgramsOnHomescreen() {
        listOf(KAN_11, KESHET_12, RESHET_13).forEach { channel ->
            val channelId = getChannelId(channel.title, baseContext)

            if (channelId == -1L) {
                Exception("${channel.title}, channelId == -1, createProgramsOnHomescreen()")
            }
            else {
                (1..NUMBER_OF_CHANNELS_ON_HOMESCREEN).forEach {
                    val builder = PreviewProgram.Builder()
                    builder.setChannelId(channelId)
                        .setType(TvContractCompat.PreviewPrograms.TYPE_CLIP)
                        .setTitle(channel.title)
                        .setDescription("תוכנית $it")
                        .setIntentUri(Uri.parse(""))
                        .setInternalProviderId("")

                    val programUri = baseContext.contentResolver.insert(TvContractCompat.PreviewPrograms.CONTENT_URI,
                        builder.build().toContentValues())

                    val programId = ContentUris.parseId(programUri)

                    if (it == 1) setFirstProgramId(channel, programId, baseContext)
                }
            }
        }

        setProgramsCreated(baseContext)
    }

    private fun createChannels() {
        val builder = Channel.Builder()

        listOf(KAN_11, KESHET_12, RESHET_13).forEach { channel ->
            builder.setType(TvContractCompat.Channels.TYPE_PREVIEW)
                .setDisplayName(channel.title)
                .setAppLinkIntentUri(Uri.parse(""))

            val channelUri = this.contentResolver.insert(
                TvContractCompat.Channels.CONTENT_URI, builder.build().toContentValues())

            val channelId = ContentUris.parseId(channelUri)
            saveChannelId(channel.title, channelId, baseContext)
            setChannelsCreated(baseContext)
            val channelLogo = getBitmapFromVectorDrawable(baseContext, channel.logo)
            ChannelLogoUtils.storeChannelLogo(baseContext, channelId, channelLogo)
            TvContractCompat.requestChannelBrowsable(baseContext, channelId)
        }
    }

    override fun onDestroy() {
        stopService(mServiceIntent)
        super.onDestroy()
    }


    ///////////////

    private fun upadteChannels() {
        listOf(KAN_11, KESHET_12, RESHET_13).forEach { channel ->
            val channelId = getChannelId(channel.title, baseContext)

            if (channelId == -1L) {
                Exception("${channel.title}, channelId == -1, upadteChannels()")
            }
            else {
                getTvGuideFromApi(channel)
            }
        }

    }

    private fun getTvGuideFromApi(channel: Channels) {
        var programs = listOf<Program>()

        GlobalScope.launch {
            when (channel) {
                KAN_11 -> {
                    val todayResponse = TvGuideService(Channels.KAN_11.baseUrl).getKan11Programs(
                        date = Date().getDateInKanDateFormat()).await()
                    val tomorrowResponse = TvGuideService(Channels.KAN_11.baseUrl).getKan11Programs(
                        date = getTomorrowDate().getDateInKanDateFormat()).await()
                    programs = getKanTodayPrograms(todayResponse, tomorrowResponse)
                }
                RESHET_13 -> {
                    val response = TvGuideService(Channels.RESHET_13.baseUrl).getReshet13Programs().await()
                    programs = getReshetTodayPrograms(response)
                }
                KESHET_12 -> {
                    val response = TvGuideService(Channels.KESHET_12.baseUrl).getKeshet12Programs().await()
                    programs = getKeshetTodayPrograms(response)

//
//                    val channelId = getChannelId(channel.title, baseContext)
//                    val programId = getFirstProgramId(channel, baseContext)
//                    updateProgram(channelId, programId, Program(counter.toString(), Date(), Date(), "", ""))
                }
            }

            updateHomeScreenChannel(channel, programs)
        }
    }

    private fun updateHomeScreenChannel(channel: Channels, programs: List<Program>) {
        // Save channel id
        val channelId = getChannelId(channel.title, baseContext)

        if (channelId == -1L) {
            Exception("${channel.title}, channelId == -1, updateHomeScreenChannel(${programs.size} programs)")
        }
        else {
            val firstProgramId = getFirstProgramId(channel, baseContext)
            if (firstProgramId == -1L) Exception("${channel.title}, firstProgramId == -1, updateHomeScreenChannel()")

            var tenNextProgram = listOf<Program>()
            // find current show and save 10 next shows
            programs.forEachIndexed { index, program ->
                if (program.startTime.before(Date()) && program.endTime.after(Date())) {
                    if (index + NUMBER_OF_CHANNELS_ON_HOMESCREEN <= programs.size)
                        tenNextProgram = programs.subList(index, index + NUMBER_OF_CHANNELS_ON_HOMESCREEN)
                    else
                        tenNextProgram = listOf<Program>()
                    // TODO: GET MORE SHOW FROM TOMORROW
                }
            }

            // update homescreen
            (firstProgramId..firstProgramId + NUMBER_OF_CHANNELS_ON_HOMESCREEN - 1).forEachIndexed { index, programId ->
                updateProgram(channelId, programId, tenNextProgram.get(index))
            }
        }
    }

    private fun updateProgram(channelId: Long, programId: Long, program: Program) {
        val programStartTime = program.startTime.hoursMinutesFormat()
        val programEndTime = program.endTime.hoursMinutesFormat()
        val imageUrl = fixKanImageUrl(program.image)
        val liveImage = "https://raw.githubusercontent.com/Sagi363/EpgIL/master/app/src/main/res/drawable/live.png"

        val builder = PreviewProgram.Builder()
        builder.setChannelId(channelId)
            .setType(TvContractCompat.PreviewPrograms.TYPE_CLIP)
            .setTitle("$programStartTime - $programEndTime | ${program.title}")
            .setDescription(program.description)
            .setPosterArtUri(Uri.parse(imageUrl))
            .setLive(program.live)
            .setLogoUri(if (program.live) Uri.parse(liveImage) else null)
            .setIntentUri(Uri.parse(""))
            .setInternalProviderId("")

        baseContext.contentResolver.update(
            TvContractCompat.buildPreviewProgramUri(programId),
            builder.build().toContentValues(), null, null
        )
    }

    private fun fixKanImageUrl(image: String): String {
        return if (image.contains("https://")) image
        else "https://kanweb.blob.core.windows.net/download/pictures/$image"
    }

    private fun getKanTodayPrograms(
        todayResponse: List<Kan11Response>,
        tomorrowResponse: List<Kan11Response>
    ): List<Program> {
        val kanPrograms = mutableListOf<Program>()
        listOf(todayResponse, tomorrowResponse).forEach {
            it.forEach {
                kanPrograms.add(
                    Program(
                        it.title,
                        it.start_time,
                        it.end_time,
                        it.live_desc,
                        if (it.picture_code.isBlank()) it.program_image else it.picture_code
                    )
                )
            }
        }
        return kanPrograms
    }

    private fun getKeshetTodayPrograms(response: Keseht12Response): List<Program> {
        val kesehtPrograms = mutableListOf<Program>()
        response.programs.forEach {
            kesehtPrograms.add(
                Program(
                    it.ProgramName,
                    it.StartTime,
                    addTimeToDate(it.StartTime, it.DurationMs),
                    it.EventDescription,
                    it.Picture,
                    it.LiveBroadcast)
            )
        }
        return kesehtPrograms
    }

    private fun getReshetTodayPrograms(reshet: Reshet13Response): List<Program> {
        var programs = mutableListOf<Program>()
        var currDay: Array<Show> = arrayOf()
        var nextDay: Array<Show> = arrayOf()
        var todayShortDate = ""
        var nextShortDate = ""
        var nextNextShortDate = ""
        val liveTextToRemove = " - ש.ח"
        val tommorowDate = getTomorrowDate()

        for (day in reshet.obj.broadcastDayList) {
            if (before6Am() && day.shortDate.equals(SimpleDateFormat("dd.MM").format(getYesterdayDate()))) {
                // Take the day before
                currDay = day.shows
                todayShortDate = day.shortDate + "." + SimpleDateFormat("yyyy").format(getYesterdayDate())
            }
            else if (before6Am() && day.shortDate.equals(SimpleDateFormat("dd.MM").format(Date()))) {
                nextDay = day.shows
                nextShortDate = day.shortDate + "." + SimpleDateFormat("yyyy").format(Date())
                break
            }
            else if (day.shortDate.equals(SimpleDateFormat("dd.MM").format(Date()))) {
                currDay = day.shows
                todayShortDate = day.shortDate + "." + SimpleDateFormat("yyyy").format(Date())
            }
            else if (day.shortDate.equals(SimpleDateFormat("dd.MM").format(tommorowDate))) {
                nextDay = day.shows
                nextShortDate = day.shortDate + "." + SimpleDateFormat("yyyy").format(tommorowDate)
                nextNextShortDate = day.shortDate + "." + SimpleDateFormat("yyyy").format(addDaysToDate(2))
                break
            }
        }

        currDay.forEachIndexed { index, show ->
            val startTime =
                if (show.show_date != 0L)
                    SimpleDateFormat("dd.MM.yyyy HH:mm").parse(todayShortDate + " " + show.start_time)
                else
                    SimpleDateFormat("dd.MM.yyyy HH:mm").parse(nextShortDate + " " + show.start_time)
            val endTime =
                if (show.show_date != 0L && index + 1 < currDay.size) {
                    // today
                    SimpleDateFormat("dd.MM.yyyy HH:mm").parse(todayShortDate + " " + currDay[index + 1].start_time)
                }
                else if (show.show_date == 0L && index + 1 < currDay.size) {
                    // tommorow (after 00:00)
                    SimpleDateFormat("dd.MM.yyyy HH:mm").parse(nextShortDate + " " + currDay[index + 1].start_time)
                }
                else if (show.show_date == 0L && index + 1 == currDay.size) {
                    // tommorow last for currDay
                    SimpleDateFormat("dd.MM.yyyy HH:mm").parse(nextShortDate + " " + nextDay[0].start_time)
                }
                else {
                    Date()
                }
            programs.add(Program(show.title.getReshetTitle(liveTextToRemove),
                                 startTime, endTime, "", show.images.image, show.title.isLiveTvShow(liveTextToRemove)))
        }

        nextDay.sliceArray(0..10).forEachIndexed { index, show ->
            val startTime =
                if (show.show_date != 0L)
                    SimpleDateFormat("dd.MM.yyyy HH:mm").parse(nextShortDate + " " + show.start_time)
                else
                    SimpleDateFormat("dd.MM.yyyy HH:mm").parse(nextNextShortDate + " " + show.start_time)
            val endTime =
                if (show.show_date != 0L) {
                    SimpleDateFormat("dd.MM.yyyy HH:mm").parse(nextShortDate + " " + nextDay[index + 1].start_time)
                }
                else {
                    // T
                    SimpleDateFormat("dd.MM.yyyy HH:mm").parse(nextNextShortDate + " " + nextDay[index + 1].start_time)
                }
            programs.add(Program(show.title.getReshetTitle(liveTextToRemove),
                                 startTime, endTime, "", show.images.image, show.title.isLiveTvShow(liveTextToRemove)))
        }

        return programs
    }

    private fun before6Am() = Date().hours < 6
    private fun String.isLiveTvShow(liveText: String) = title.contains(liveText)
    private fun String.getReshetTitle(textToRemove: String) =
        if (this.contains(textToRemove)) this.replace(textToRemove, "") else this
}
