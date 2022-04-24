package com.foodapp.app.utils

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast

class interfaceScript {
    private  var context: Context?=null

    constructor(context: Context){


        this.context=context
    }
    @JavascriptInterface
    public fun showtoast(mess:String){

        Toast.makeText(context,mess,Toast.LENGTH_LONG).show()




    }
}