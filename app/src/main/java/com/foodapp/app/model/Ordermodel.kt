package com.foodapp.app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

import java.io.Serializable

class Ordermodel{
    var user_id:String?=null
    var order_total:String?=null
    var payment_id:String?=null
    var payment_type:String?=null
    var address:String?=null
    var discount_amount:String?=null
    var discount_pr:String?=null
    var tax:String?=null
    var tax_amount:String?=null
    var lat:String?=null
    var lang:String?=null
    var delivery_charge:String?=null
    var order_type:String?=null
    var order_notes:String?=null
    var promocode:String?=null
   var  cartdata: ArrayList<CartItemModel>?=null
constructor(user_id:String?,
            order_total:String?,
            payment_id:String?,
            payment_type:String?,
            address:String?,
            promocode:String?,
            discount_amount:String?,
            discount_pr:String?,
          tax:String?,
           tax_amount:String?,

            delivery_charge:String?,
            order_type:String?,
            order_notes:String?,
            lat:String?,
            lang:String?,
            cartItemModel: ArrayList<CartItemModel>
           ) {
    this.user_id=user_id
    this.order_total=order_total
    this.order_notes=order_notes
    this.delivery_charge=delivery_charge
    this.tax=tax
    this.promocode=promocode
    this.tax_amount=tax_amount
    this.discount_pr=discount_pr
    this.discount_amount=discount_amount
    this.lat=lat
    this.lang=lang
    this.payment_id=payment_id
    this.payment_type=payment_type
    this.address=address
    this.cartdata=cartItemModel
    this.order_type=order_type

}




}