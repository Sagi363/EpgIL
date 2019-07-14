package io.tipy.spgil.models

import java.util.*

data class Program(
    var title: String = "",
    var startTime: Date,
    var endTime: Date,
    var description: String = "",
    var image: String = "",
    var live: Boolean = false)