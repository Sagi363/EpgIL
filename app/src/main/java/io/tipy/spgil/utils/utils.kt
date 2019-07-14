package io.tipy.spgil.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import io.tipy.spgil.PreferenceHelper
import io.tipy.spgil.models.Channels
import java.text.SimpleDateFormat
import java.util.*

fun saveChannelId(title: String, channelId: Long, context: Context) {
    PreferenceHelper.setLongPreference(context, title, channelId)
}

fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
    var drawable = ContextCompat.getDrawable(context, drawableId)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = DrawableCompat.wrap(drawable!!).mutate()
    }

    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

fun getYesterdayDate(): Date {
    val c = Calendar.getInstance()
    c.setTime(Date())
    c.add(Calendar.DATE, -1)
    return c.time
}

fun getTomorrowDate(): Date {
    val c = Calendar.getInstance()
    c.setTime(Date())
    c.add(Calendar.DATE, 1)
    return c.time
}

fun addDaysToDate(daysToAdd: Int): Date {
    val c = Calendar.getInstance()
    c.setTime(Date())
    c.add(Calendar.DATE, daysToAdd)
    return c.time
}

fun addTimeToDate(date: Date, timeToAddInMS: Long): Date {
    val c = Calendar.getInstance()
    c.setTime(date)
    c.add(Calendar.MILLISECOND, timeToAddInMS.toInt())
    return c.time
}

fun Date.getDateInKanDateFormat() = SimpleDateFormat("dd/MM/yyyy").format(this)
fun Date.hoursMinutesFormat() = SimpleDateFormat("HH:mm").format(this)
fun getFirstProgramId(channel: Channels, context: Context) =
    PreferenceHelper.getLongPreference(context, channel.title + FIRST_PROGRAM_ID, -1L)
fun setFirstProgramId(channel: Channels, firstProgramId: Long, context: Context) =
    PreferenceHelper.setLongPreference(context, channel.title + FIRST_PROGRAM_ID, firstProgramId)
fun isProgramsCreated(context: Context) = PreferenceHelper.getBooleanPreference(context, PROGRAMS_CREATED_KEY, false)
fun setProgramsCreated(context: Context) = PreferenceHelper.setBooleanPreference(context, PROGRAMS_CREATED_KEY, true)
fun isChannelsCreated(context: Context) = PreferenceHelper.getBooleanPreference(context, CHANNELS_CREATED_KEY, false)
fun setChannelsCreated(context: Context) = PreferenceHelper.setBooleanPreference(context, CHANNELS_CREATED_KEY, true)
fun getChannelId(title: String, context: Context) = PreferenceHelper.getLongPreference(context, title, -1L)

const val CHANNELS_CREATED_KEY = "CHANNELS_CREATED"
const val PROGRAMS_CREATED_KEY = "PROGRAMS_CREATED"
const val FIRST_PROGRAM_ID = "_FIRST_PROGRAM_ID"
const val NUMBER_OF_CHANNELS_ON_HOMESCREEN = 14