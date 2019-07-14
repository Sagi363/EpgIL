package io.tipy.spgil.network.response

import java.util.*

data class Keseht12Response(val programs: Array<Programs>)

data class Programs(val StartTime: Date,
                    val ProgramName: String,
                    val DurationMs: Long,
                    val LiveBroadcast: Boolean,
                    val EventDescription: String,
                    val Picture: String)



