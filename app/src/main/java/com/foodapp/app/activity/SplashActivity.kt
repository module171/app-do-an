package com.foodapp.app.activity


import android.content.Context
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.text.format.Formatter
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getBooleanPref


class SplashActivity : BaseActivity(){
    override fun setLayout(): Int {
       return R.layout.activity_splash
    }

    override fun InitView() {

        Common.getCurrentLanguage(this@SplashActivity, false)
        Handler(Looper.getMainLooper()).postDelayed({

//            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//            val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
//            SharePreference.setStringPref(applicationContext,"ip",ipAddress)
//            ApiClient.ip=ipAddress
            if(!getBooleanPref(this@SplashActivity,SharePreference.isTutorial)){
                openActivity(TutorialActivity::class.java)
                finish()
            }else{
                openActivity(DashboardActivity::class.java)
                finish()
            }
        },3000)
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@SplashActivity, false)
    }
}