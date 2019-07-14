package io.tipy.scootaroundorid.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import io.tipy.spgil.network.response.Kan11Response
import io.tipy.spgil.network.response.Keseht12Response
import io.tipy.spgil.network.response.Reshet13Response
import io.tipy.spgil.utils.DateConvertor
import io.tipy.spgil.utils.ReshetObjConvertor
import retrofit2.converter.moshi.MoshiConverterFactory

const val KAN_11_URL = "https://www.kan.org.il/tv-guide/"
const val KESHET_12_URL = "https://www.mako.co.il/AjaxPage?jspName=EPGResponse.jsp" //?jspName=EPGResponse.jsp
const val RESHET_13_URL = "https://13tv.co.il/Services/epg.php" //?key='0'&action='next'

interface TvGuideService {

    @GET("tv_guidePrograms.ashx")
    fun getKan11Programs(@Query("stationID") stationID: Int = 1,
                         @Query("day") date: String): Deferred<List<Kan11Response>>

    @GET("AjaxPage")
    fun getKeshet12Programs(@Query("jspName") jspName: String = "EPGResponse.jsp"): Deferred<Keseht12Response>

    @GET("Services/epg.php")
    fun getReshet13Programs(@Query("key") key: String = "\"0\"",
                            @Query("action") action: String = "\"next\""): Deferred<Reshet13Response>

    companion object {
         operator fun invoke(baseUrl: String): TvGuideService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .build()

             // Custom Type Adapters for Moshi
             val dateMoshi = Moshi.Builder()
                 .add(KotlinJsonAdapterFactory())
                 .add(DateConvertor())
                 .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create(dateMoshi))
                .build()
                .create(TvGuideService::class.java)
        }
    }
}