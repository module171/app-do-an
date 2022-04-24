package com.foodapp.app.api

import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat.getSystemService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    var ip:String?=null
    val BASE_URL="http://192.168.135.12:82/admin/api/"
    val PrivicyPolicy="http://192.168.165.12:82/food-admin/admin/privacy-policy"
    val termscondition="http://192.168.1.16:82/food-admin/termscondition"
    val MapKey="AIzaSyDCT1ergyIWDnv6lqQZDMyz2oRroy_GNOI"
    val Stripe="Your_stripe_public_key"

    var TIMEOUT: Long = 60 * 2 * 1.toLong()
    val getClient: ApiInterface get() {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient=OkHttpClient.Builder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(ApiInterface::class.java)
    }

}