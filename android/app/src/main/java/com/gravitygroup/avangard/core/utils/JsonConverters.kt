package com.gravitygroup.avangard.core.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*

class DateConverter {

    @FromJson
    fun fromJson(timestamp: Long) = Date(timestamp)

    @ToJson
    fun toJson(date: Date) = date.time
}