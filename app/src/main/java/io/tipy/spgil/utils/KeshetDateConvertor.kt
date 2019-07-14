package io.tipy.spgil.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class KeshetDateConvertor {
    @ToJson
    fun toJson(value: Date): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value)
    }

    @FromJson
    fun fromJson(value: String): Date {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value)
    }
}