package io.tipy.spgil.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import io.tipy.spgil.network.response.BroadcastDay
import io.tipy.spgil.network.response.Reshet13Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ReshetObjConvertor {
    @ToJson
    fun toJson(value: BroadcastDay): String {
        return ""
    }

    @FromJson
    fun fromJson(value: String): BroadcastDay {
        val a= value
        return BroadcastDay("", arrayOf(), 0, "")
    }
}