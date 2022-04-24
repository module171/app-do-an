package com.foodapp.app.activity

import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_vnpay.*
import android.os.Bundle
import com.foodapp.app.R
import com.foodapp.app.model.Ordermodel
import com.foodapp.app.utils.interfaceScript
import android.os.Parcelable
class vnpayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vnpay)


        val myIntent = intent
        val ordermodel = intent.getSerializableExtra("ordermodel") as? Ordermodel
        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        webView.webViewClient = WebViewClient()

        // this will load the url of the website
        webView.loadUrl("http://192.168.165.12:82/testscript/index.php?id=${ordermodel?.lat}")
        webView.addJavascriptInterface(interfaceScript(applicationContext),"Android")
        // this will enable the javascript settings
        webView.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        webView.settings.setSupportZoom(true)
    }

    // if you press Back button this code will work
    override fun onBackPressed() {
        // if your webview can go back it will go back
        if (webView.canGoBack())
            webView.goBack()
        // if your webview cannot go back
        // it will exit the application
        else
            super.onBackPressed()
    }
}