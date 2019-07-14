package io.tipy.spgil.network.response

import com.squareup.moshi.Json

data class Reshet13Response(
    @Json(name = "\\\"0\\\"")        val obj: Obj,
    @Json(name = "date")             val dddate: String)

data class Obj(
    @Json(name = "broadcastDayList") val broadcastDayList: Array<BroadcastDay>)

data class BroadcastDay(
    @Json(name = "shortDate")        val shortDate: String,
    @Json(name = "shows")            val shows: Array<Show>,
    @Json(name = "timestamp")        val timestamp: Int,
    @Json(name = "weekday")          val weekday: String)

data class Show(
    @Json(name = "images")           val images: Images,
    @Json(name = "title")            val title: String,
    @Json(name = "show_date")        val show_date: Long,
    @Json(name = "start_time")       val start_time: String)

data class Images(
    @Json(name = "content_img")      val image: String
)



