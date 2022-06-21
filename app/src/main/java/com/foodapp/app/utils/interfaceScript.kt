package com.foodapp.app.utils

import android.app.Activity
import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.SingleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class interfaceScript {
    private  var context: Context?=null

    constructor(context: Context){


        this.context=context
    }
    @JavascriptInterface
    public fun showtoast(order_id:Int){


        Toast.makeText(context,order_id.toString(),Toast.LENGTH_LONG).show()
//        Common.showLoadingProgress(context as Activity)
//        val map = HashMap<String, String>()
//        map.put("user_id", SharePreference.getStringPref(context as Activity, SharePreference.userId)!!)
//        map.put("order_id", order_id.toString())
//        val call = ApiClient.getClient.updatestatus(map)
//        call.enqueue(object : Callback<SingleResponse> {
//            override fun onResponse(
//                call: Call<SingleResponse>,
//                response: Response<SingleResponse>
//            ) {
//                if (response.code() == 200) {
//                    Common.dismissLoadingProgress()
//                    val restResponce: SingleResponse = response.body()!!
//                    if (restResponce.getStatus().equals("1")) {
//                        Common.showSuccessFullMsg(
//                            context as Activity,
//                            "cap nhat don hang thanh cong"
//                        )
//
//
//                    } else if (restResponce.getStatus().equals("0")) {
//                        Common.dismissLoadingProgress()
//                        Common.alertErrorOrValidationDialog(
//                            context as Activity,
//                            "cap nhat don hang thanh cong"
//                        )
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
//                Common.dismissLoadingProgress()
//                Common.alertErrorOrValidationDialog(
//                    context as Activity,
//                   t.message
//                )
//            }
//        })





    }

}