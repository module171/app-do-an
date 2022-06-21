package com.foodapp.app.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_vnpay.*
import android.os.Bundle
import com.foodapp.app.R
import com.foodapp.app.model.Ordermodel
import com.foodapp.app.utils.interfaceScript
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.TextView
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.fragment.OrderHistoryFragment
import com.foodapp.app.sqlite.DatabaseHandler
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.openActivity
import com.foodapp.app.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class vnpayActivity : BaseActivity() {

    var dbHelper =  DatabaseHandler(this)


    override fun setLayout(): Int {
        return R.layout.activity_vnpay
    }

    override fun InitView() {
        var user_id = SharePreference.getStringPref(
            this@vnpayActivity,
            SharePreference.userId
        )!!
        var  order_total = intent.getStringExtra("getAmount")!!

        var  order_id = intent.getIntExtra("order_id",0)!!



        var url:String="https://fastfood171.site/vnpay_php/index.php?amount=${order_total}&&order_id=${order_id}&&user_id=${user_id}"


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if(url!!.equals("https://fastfood171.site/vnpay_php/success.php")){

                    view!!.loadUrl(url)
                    successfulDialog(
                        this@vnpayActivity,
                        "đơn hàng của bạn thanh toán thành công"
                    )

                }else{
                    view!!.loadUrl(url)
                }

                return true
            }
        }
//        webView.webViewClient =WebViewClient()
        // this will load the url of the website
        webView.loadUrl(url)
        webView.addJavascriptInterface(interfaceScript(applicationContext),"Android")
        // this will enable the javascript settings
        webView.settings.javaScriptEnabled = true
        webView.setWebChromeClient(WebChromeClient())
        webView.getSettings().setDomStorageEnabled(true);
        // if you want to enable zoom feature
        webView.settings.setSupportZoom(true)
    }
    fun successfulDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = m_view.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = m_view.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                startActivity(Intent(this@vnpayActivity, DashboardActivity::class.java).putExtra("pos","2"))
                finish()
                finishAffinity()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    fun callApiOrder(map:Ordermodel){

        val call = ApiClient.getClient.setOrderPayment(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                       Common.getLog("success","thành công")
                    } else if (restResponse.getStatus().equals("0")) {
                        Common.getLog("success","thất bại")
                    }
                }else{
                    Common.getLog("fail",response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.getLog("error",t.message.toString())
//                Common.dismissLoadingProgress()
//                Common.alertErrorOrValidationDialog(
//                    this@PaymentPayActivity,
//                    resources.getString(R.string.error_msg)
//                )
            }
        })
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