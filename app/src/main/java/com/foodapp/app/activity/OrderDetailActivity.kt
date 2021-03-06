package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodapp.app.R
import com.foodapp.app.api.RestOrderDetailResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.model.OrderDetailModel
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.showLoadingProgress
import com.foodapp.app.api.*
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import com.foodapp.app.utils.SharePreference.Companion.isCurrancy
import kotlinx.android.synthetic.main.activity_orderdetail.*
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderDeliveryCharge
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTaxPrice
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTotalCharge
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTotalPrice
import kotlinx.android.synthetic.main.row_orderitemsummary.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OrderDetailActivity:BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_orderdetail
    }

    override fun InitView() {
        if(Common.isCheckNetwork(this@OrderDetailActivity)){
            callApiOrderDetail()
        }else{
            Common.alertErrorOrValidationDialog(
                this@OrderDetailActivity,
                resources.getString(R.string.no_internet)
            )
        }
        ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun callApiOrderDetail() {
        showLoadingProgress(this@OrderDetailActivity)
        val map = HashMap<String, String>()
        map.put("order_id",intent.getStringExtra("order_id")!!)
        val call = ApiClient.getClient.setgetOrderDetail(map)
        call.enqueue(object : Callback<RestOrderDetailResponse> {
            override fun onResponse(
                call: Call<RestOrderDetailResponse>,
                response: Response<RestOrderDetailResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponce: RestOrderDetailResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        if (restResponce.getData().size > 0) {
                            rvOrderItemFood.visibility = View.VISIBLE
                            setFoodDetailData(restResponce)
                        } else {
                            rvOrderItemFood.visibility = View.GONE
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        rvOrderItemFood.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<RestOrderDetailResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@OrderDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setFoodDetailData(response: RestOrderDetailResponse) {
        if(response.getData().size>0){
            setFoodCategoryAdaptor(response.getData())
        }
        if(response.getOrder_type().equals("2")){
            cvDeliveryAddress.visibility=View.GONE
            cvDriverInformation.visibility=View.GONE
            //cvPinCode.visibility=View.GONE
            if(response.getSummery()!!.getPromocode()==null){
                rlDiscount.visibility=View.GONE
                if(response.getSummery()!!.getOrder_notes()==null){
                    tvNotes.text=""
                    cvOrderNote.visibility=View.GONE
                }else{
                    cvOrderNote.visibility=View.VISIBLE
                    tvNotes.text=response.getSummery()!!.getOrder_notes()
                }
                tvOrderAddress.text=response.getAddress()

                tvOrderTotalPrice.text=String.format(Locale.US,"%,.02f",response.getSummery()!!.getOrder_total()!!.toDouble())+' '+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvOrderTaxPrice.text=String.format(Locale.US,"%,.02f",response.getSummery()!!.getTax()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvOrderDeliveryCharge.text="0.00"+getStringPref(this@OrderDetailActivity,isCurrancy)
                val getTex:Float=(response.getSummery()!!.getOrder_total()!!.toFloat()*response.getSummery()!!.getTax()!!.toFloat())/100.toFloat()
                tvTitleTex.text="Tax (${response.getSummery()!!.getTax()}%)"
                tvOrderTaxPrice.text=String.format(Locale.US,"%,.02f", getTex)+getStringPref(this@OrderDetailActivity,isCurrancy)
                val totalprice=response.getSummery()!!.getOrder_total()!!.toFloat()+getTex+0.00
                tvOrderTotalCharge.text=String.format(Locale.US,"%,.02f", totalprice)+getStringPref(this@OrderDetailActivity,isCurrancy)
            }else{
                rlDiscount.visibility=View.VISIBLE
                if(response.getSummery()!!.getOrder_notes()==null){
                    tvNotes.text=""
                    cvOrderNote.visibility=View.GONE
                }else{
                    cvOrderNote.visibility=View.VISIBLE
                    tvNotes.text=response.getSummery()!!.getOrder_notes()
                }
                tvOrderAddress.text=response.getAddress()

                tvOrderTotalPrice.text=String.format(Locale.US,"%,.02f",response.getSummery()!!.getOrder_total()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvOrderTaxPrice.text=String.format(Locale.US,"%,.02f",response.getSummery()!!.getTax()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvOrderDeliveryCharge.text="0.00"+getStringPref(this@OrderDetailActivity,isCurrancy)

                val getTex:Float=(response.getSummery()!!.getOrder_total()!!.toFloat()*response.getSummery()!!.getTax()!!.toFloat())/100
                tvTitleTex.text="Tax (${response.getSummery()!!.getTax()}%)"
                tvOrderTaxPrice.text=String.format(Locale.US,"%,.02f", getTex)+getStringPref(this@OrderDetailActivity,isCurrancy)

                tvDiscountOffer.text ="-"+String.format(Locale.US,"%,.02f", response.getSummery()!!.getDiscount_amount()!!.toFloat())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvPromoCodeApply.text =response.getSummery()!!.getPromocode()


                val subtotal=response.getSummery()!!.getOrder_total()!!.toFloat()-response.getSummery()!!.getDiscount_amount()!!.toFloat()
                val totalprice=subtotal+getTex+0.00
                tvOrderTotalCharge.text=String.format(Locale.US,"%,.02f", totalprice)+getStringPref(this@OrderDetailActivity,isCurrancy)
            }
        }else{
            cvDeliveryAddress.visibility=View.VISIBLE
            //cvPinCode.visibility=View.VISIBLE

            if(intent.getStringExtra("order_status")!!.equals("3")||intent.getStringExtra("order_status")!!.equals("4")){
                cvDriverInformation.visibility=View.VISIBLE
                llCall.setOnClickListener {
                   val call: Uri = Uri.parse("tel:${response.getSummery()!!.getDriver_mobile()}")
                   val surf = Intent(Intent.ACTION_DIAL, call)
                   startActivity(surf)
                }
                tvUserName.text=response.getSummery()!!.getDriver_name()

                Glide.with(this@OrderDetailActivity).load(response.getSummery()!!.getDriver_profile_image())
                    .placeholder(ResourcesCompat.getDrawable(resources,R.drawable.placeholder,null)).centerCrop()
                    .into(ivUserDetail)
            }else{
                cvDriverInformation.visibility=View.GONE
            }

            if(response.getSummery()!!.getPromocode()==null){
                rlDiscount.visibility=View.GONE
                if(response.getSummery()!!.getOrder_notes()=="r???ng"){
                    tvNotes.text=""
                    cvOrderNote.visibility=View.GONE
                }else{
                    cvOrderNote.visibility=View.VISIBLE
                    tvNotes.text=response.getSummery()!!.getOrder_notes()
                }
                tvOrderAddress.text=response.getAddress()
               var order_price=response.getSummery()!!.getOrder_total()!!.toDouble()-response.getSummery()!!.gettax_amount()!!.toDouble()-response.getSummery()!!.getDelivery_charge()!!.toDouble()
                tvOrderTotalPrice.text=String.format(Locale.US,"%,.02f",response.getSummery()!!.getOrder_total()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvOrderTaxPrice.text=String.format(Locale.US,"%,.02f",response.getSummery()!!.gettax_amount()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvOrderDeliveryCharge.text=String.format(Locale.US,"%,.02f",response.getSummery()!!.getDelivery_charge()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)

                val getTex:Float=(response.getSummery()!!.getOrder_total()!!.toDouble().toFloat()*response.getSummery()!!.getTax()!!.toFloat())/100.toFloat()
                tvTitleTex.text="Tax (${response.getSummery()!!.getTax()}%)"
                tvOrderTaxPrice.text=String.format(Locale.US,"%,.02f", getTex)+getStringPref(this@OrderDetailActivity,isCurrancy)
                val totalprice=response.getSummery()!!.getOrder_total()!!.toDouble()+getTex+response.getSummery()!!.getDelivery_charge()!!.toFloat()
                tvOrderTotalCharge.text=String.format(Locale.US,"%,.02f", totalprice)+getStringPref(this@OrderDetailActivity,isCurrancy)
            }else{
                rlDiscount.visibility=View.VISIBLE
                if(response.getSummery()!!.getOrder_notes()==null){
                    tvNotes.text=""
                    cvOrderNote.visibility=View.GONE
                }else{
                    cvOrderNote.visibility=View.VISIBLE
                    tvNotes.text=response.getSummery()!!.getOrder_notes()
                }
                tvOrderAddress.text=response.getAddress()

                tvOrderTotalPrice.text=String.format(Locale.US,"%,.02f",response.getSummery()!!.getOrder_total()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvOrderTaxPrice.text=String.format(Locale.US,"%,.02f",response.getSummery()!!.getTax()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvOrderDeliveryCharge.text=String.format(Locale.US,"%,.02f",response.getSummery()!!.getDelivery_charge()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)

                val getTex:Float=(response.getSummery()!!.getOrder_total()!!.toFloat()*response.getSummery()!!.getTax()!!.toFloat())/100
                tvTitleTex.text="Tax (${response.getSummery()!!.getTax()}%)"
                tvOrderTaxPrice.text=String.format(Locale.US,"%,.02f", getTex)+getStringPref(this@OrderDetailActivity,isCurrancy)

                tvDiscountOffer.text ="-"+String.format(Locale.US,"%,.02f", response.getSummery()!!.getDiscount_amount()!!.toFloat())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvPromoCodeApply.text =response.getSummery()!!.getPromocode()

                val subtotal=response.getSummery()!!.getOrder_total()!!.toFloat()-response.getSummery()!!.getDiscount_amount()!!.toFloat()
                val totalprice=subtotal+getTex+response.getSummery()!!.getDelivery_charge()!!.toFloat()
                tvOrderTotalCharge.text=String.format(Locale.US,"%,.02f", totalprice)+getStringPref(this@OrderDetailActivity,isCurrancy)
            }
        }

    }

    fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderDetailModel>) {
        val orderHistoryAdapter = object : BaseAdaptor<OrderDetailModel>(this@OrderDetailActivity, orderHistoryList) {
            @SuppressLint("SetTextI18n", "NewApi")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: OrderDetailModel,
                position: Int
            ) {

                val ivFoodItem: ImageView = holder!!.itemView.findViewById(R.id.ivFoodCart)
                val tvOrderFoodName: TextView = holder.itemView.findViewById(R.id.tvFoodName)
                val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPrice)
                val tvQtyNumber: TextView = holder.itemView.findViewById(R.id.tvQtyPrice)
                val tvNotes: TextView = holder.itemView.findViewById(R.id.tvNotes)
                val tvAddons: TextView = holder.itemView.findViewById(R.id.tvAddons)

                tvOrderFoodName.text =orderHistoryList.get(position).getItem_name()
                tvPrice.text = String.format(Locale.US,"%,.2f",orderHistoryList.get(position).getprice_cart()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvQtyNumber.text ="QTY : ${orderHistoryList.get(position).getQty()}"

                Glide.with(this@OrderDetailActivity).load(orderHistoryList.get(position).getItemimage().getImage())
                    .placeholder(ResourcesCompat.getDrawable(resources,R.drawable.placeholder,null)).centerCrop()
                    .into(ivFoodItem)


                if(orderHistoryList.get(position).getAddons().size>0){
                    tvAddons.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                }else{
                    tvAddons.backgroundTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null))
                }
                if(orderHistoryList.get(position).getItem_notes()==null){
                    tvNotes.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null))
                }else{
                    tvNotes.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                }

                holder.itemView.tvAddons.setOnClickListener {
                    if(orderHistoryList.get(position).getAddons().size>0){

                        Common.openDialogSelectedAddons(this@OrderDetailActivity,orderHistoryList.get(position).getAddons())
                    }
                }

                holder.itemView.tvNotes.setOnClickListener {
                    if(orderHistoryList.get(position).getItem_notes()!=null){
                        Common.alertNotesDialog(this@OrderDetailActivity,orderHistoryList.get(position).getItem_notes())
                    }
                }

            }
            override fun setItemLayout(): Int {
                return R.layout.row_orderitemsummary
            }
            override fun setNoDataView(): TextView? {
                return null
            }
        }
        rvOrderItemFood.adapter = orderHistoryAdapter
        rvOrderItemFood.layoutManager = LinearLayoutManager(this@OrderDetailActivity)
        rvOrderItemFood.itemAnimator = DefaultItemAnimator()
        rvOrderItemFood.isNestedScrollingEnabled = true
    }
}