package io.tipy.spgil.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateConvertor {
    @ToJson
    fun toJson(value: Date): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(value)
    }

    @FromJson
    fun fromJson(value: String): Date {
        val dateFormat1 = "yyyy-MM-dd'T'HH:mm:ss"
        val dateFormat2 = "dd/MM/yyyy HH:mm:ss"
        return SimpleDateFormat(if (value.contains(' ')) dateFormat2 else dateFormat1).parse(value)
    }
}