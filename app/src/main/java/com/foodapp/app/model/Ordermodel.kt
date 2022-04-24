package com.foodapp.app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.io.Serializable

data class Ordermodel(val user_id:String?,val order_total:String?,
                      val payment_id:String?,
                      val payment_type:String?,
                      val address:String?,
                      val discount_amount:String?,
                      val discount_pr:String?,
                      val tax:String?,
                      val tax_amount:String?,
                      val lat:String?,
                      val lang:String?,
                      val delivery_charge:String?,
                      val order_type:String?,
                      val order_notes:String?

                      ) : Serializable {


constructor(user_id:String?,
            order_total:String?,
            payment_id:String?,
            payment_type:String?,
            address:String?,
            discount_amount:String?,
            discount_pr:String?,
          tax:String?,
           tax_amount:String?,
            lat:String?,
            lang:String?,
            delivery_charge:String?,
            order_type:String?,
            order_notes:String?,
            cartItemModel: ArrayList<CartItemModel>
           ) : this(user_id, order_total, payment_id, payment_type, address, discount_amount, discount_pr, tax, tax_amount, lat, lang, delivery_charge, order_type, order_notes) {


}




}