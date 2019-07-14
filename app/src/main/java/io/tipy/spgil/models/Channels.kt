package io.tipy.spgil.models

import io.tipy.spgil.R

enum class Channels(val id: Int, val title: String, val logo: Int, val baseUrl: String, val intentLink: String = "") {
    KAN_11(0, "כאן 11", R.drawable.kan_11, "https://www.kan.org.il/tv-guide/tv_guidePrograms.ashx/"),
    KESHET_12(1, "קשת 12", R.drawable.keshet_12, "https://www.mako.co.il/"),
    RESHET_13(2, "רשת 13", R.drawable.reshet_13, "https://13tv.co.il/")
}