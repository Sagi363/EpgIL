package io.tipy.spgil.network.response

import java.util.*

data class Kan11Response(var title: String = "",
                         var start_time: Date,
                         var end_time: Date,
                         var live_desc: String = "",
                         var picture_code: String = "",
                         var program_image: String = "")