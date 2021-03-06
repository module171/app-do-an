package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodapp.app.R
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.model.CartItemModel
import com.foodapp.app.sqlite.DatabaseHandler
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.dismissLoadingProgress
import com.foodapp.app.utils.Common.getCurrentLanguage
import com.foodapp.app.utils.Common.getLog
import com.foodapp.app.utils.Common.isCheckNetwork
import com.foodapp.app.utils.Common.showLoadingProgress
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.row_cart.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CartActivity : BaseActivity() {
    var cartItemAdapter:BaseAdaptor<CartItemModel>?=null
    var cartItem:ArrayList<CartItemModel>?=ArrayList()
    var item_price:Double?=null
    var dbHelper =  DatabaseHandler(this)
    override fun setLayout(): Int {
        return R.layout.activity_cart
    }

    override fun InitView() {
        getCurrentLanguage(this@CartActivity,false)
        tvCheckout.visibility = View.GONE
        if (isCheckNetwork(this@CartActivity)) {
            Common.getLog("cart", dbHelper.viewCart().size.toString())
          callApiCart(false)
        } else {
          alertErrorOrValidationDialog(
            this@CartActivity,
            resources.getString(R.string.no_internet)
          )
        }

        ivBack.setOnClickListener {
            finish()
        }

        ivHome.setOnClickListener {
            val intent=Intent(this@CartActivity,DashboardActivity::class.java).putExtra("pos","1")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        tvCheckout.setOnClickListener {
            if(isCheckNetwork(this@CartActivity)){
                callApiIsOpen()
            }else{
                alertErrorOrValidationDialog(this@CartActivity,resources.getString(R.string.no_internet))
            }
        }
    }


    private fun callApiCart(isQty: Boolean) {
        if(!isQty){
            Common.getLog("isqty",isQty.toString())
            showLoadingProgress(this@CartActivity)
        }
        val map = HashMap<String, String>()
        map.put("user_id", getStringPref(this@CartActivity, SharePreference.userId)!!)
        dismissLoadingProgress()
//        val restResponce: ListResponse<CartItemModel> = response.body()!!
//        if (restResponce.getStatus().equals("1")) {
            if (dbHelper.viewCart().size > 0) {
                rvCartFood.visibility = View.VISIBLE
                tvNoDataFound.visibility = View.GONE
                tvCheckout.visibility = View.VISIBLE
                cartItem=dbHelper.viewCart()
                Common.getLog("Data", "===" + Gson().toJson(cartItem).toString() + "")
                setFoodCartAdaptor(cartItem!!)
            } else {
                rvCartFood.visibility = View.GONE
                tvNoDataFound.visibility = View.VISIBLE
                tvCheckout.visibility = View.GONE
            }
//        }
//        val call = ApiClient.getClient.getCartItem(map)
//
//        call.enqueue(object : Callback<ListResponse<CartItemModel>> {
//            override fun onResponse(
//                call: Call<ListResponse<CartItemModel>>,
//                response: Response<ListResponse<CartItemModel>>
//            ) {
//                if (response.code() == 200) {
//                    dismissLoadingProgress()
//                    val restResponce: ListResponse<CartItemModel> = response.body()!!
//                    if (restResponce.getStatus().equals("1")) {
//                        if (restResponce.getData().size > 0) {
//                            rvCartFood.visibility = View.VISIBLE
//                            tvNoDataFound.visibility = View.GONE
//                            tvCheckout.visibility = View.VISIBLE
//                            cartItem=restResponce.getData()
//                            setFoodCartAdaptor(cartItem!!)
//                        } else {
//                            rvCartFood.visibility = View.GONE
//                            tvNoDataFound.visibility = View.VISIBLE
//                            tvCheckout.visibility = View.GONE
//                        }
//                    }
//                }else{
//                    dismissLoadingProgress()
//                    rvCartFood.visibility = View.GONE
//                    tvNoDataFound.visibility = View.VISIBLE
//                }
//            }
//
//            override fun onFailure(call: Call<ListResponse<CartItemModel>>, t: Throwable) {
//                dismissLoadingProgress()
//                alertErrorOrValidationDialog(
//                    this@CartActivity,
//                    resources.getString(R.string.error_msg)
//                )
//            }
//        })
    }
    @SuppressLint("ResourceType", "NewApi")
    private fun setFoodCartAdaptor(cartItemList: ArrayList<CartItemModel>) {
        cartItemAdapter = object : BaseAdaptor<CartItemModel>(this@CartActivity, cartItem!!) {
            @SuppressLint("SetTextI18n")
            override fun onBindData(
              holder: RecyclerView.ViewHolder?,
              `val`: CartItemModel,
              position: Int
            ) {
                holder!!.itemView.tvFoodName.text = cartItem!!.get(position).getItem_name()
                holder.itemView.tvFoodPrice.text =String.format(Locale.US,"%,.2f",cartItem!!.get(position).getPrice()!!.toDouble())+' '+Common.getCurrancy(this@CartActivity)
                holder.itemView.tvFoodQty.text = cartItem!!.get(position).getQty().toString()
                Glide.with(this@CartActivity).load(cartItem!!.get(position).getimage()).into( holder.itemView.ivFoodCart)

                if(cartItem!!.get(position).getAddons_id().equals("")||cartItem!!.get(position).getAddons_id()==null){
                    holder.itemView.tvAddons.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.gray))
                }else{
                    holder.itemView.tvAddons.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                }
                if(cartItem!!.get(position).getItem_notes()==""){
                  holder.itemView.tvNotes.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.gray))
                }else{
                  holder.itemView.tvNotes.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                }

                holder.itemView.tvAddons.setOnClickListener {
                  if(cartItem!!.get(position).getAddons().size>0){
                    Common.openDialogSelectedAddons(this@CartActivity,cartItem!!.get(position).getAddons())
                  }
                }
                  item_price=cartItem!!.get(position).getPrice()!!.toDouble()
                holder.itemView.tvNotes.setOnClickListener {
                  if(cartItem!!.get(position).getItem_notes()!=""){
                    Common.alertNotesDialog(this@CartActivity,cartItem!!.get(position).getItem_notes())
                  }
                }

                holder.itemView.ivDeleteCartItem.setOnClickListener {
                  if (isCheckNetwork(this@CartActivity)) {
                    dlgDeleteConformationDialog(this@CartActivity,"Are you sure delete cart item",cartItem!!.get(position).getId()!!.toString(),position)
                  } else {
                    alertErrorOrValidationDialog(
                        this@CartActivity,
                        resources.getString(R.string.no_internet)
                    )
                  }
                }

                holder.itemView.ivMinus.setOnClickListener {
                  if(cartItem!!.get(position).getQty()!!.toInt() > 1){

                    getLog("Qty>>",cartItem!!.get(position).getQty().toString())
                    if (isCheckNetwork(this@CartActivity)) {
                      callApiCartQTYUpdate(cartItemList.get(position),position,false)
                    } else {
                      alertErrorOrValidationDialog(
                        this@CartActivity,
                        resources.getString(R.string.no_internet)
                      )
                    }
                  }else{
                    holder.itemView.ivMinus.isClickable =false
                    getLog("Qty1>>",cartItem!!.get(position).getQty().toString())
                  }
                }
                holder.itemView.ivPlus.setOnClickListener {
                    if(cartItem!!.get(position).getQty()!!.toInt()<100){
                      if (isCheckNetwork(this@CartActivity)) {
                        callApiCartQTYUpdate(cartItemList.get(position),position,true)
                      } else {
                        alertErrorOrValidationDialog(
                          this@CartActivity,
                          resources.getString(R.string.no_internet)
                        )
                      }
                    }else{
                      alertErrorOrValidationDialog(this@CartActivity,"Maximum quantity allowed ${getStringPref(this@CartActivity,SharePreference.isMiniMumQty)}")
                    }
                }
            }
            override fun setItemLayout(): Int {
                return R.layout.row_cart
            }

            override fun setNoDataView(): TextView? {
                return null
            }
        }
        rvCartFood.adapter = cartItemAdapter
        rvCartFood.layoutManager = LinearLayoutManager(this@CartActivity)
        rvCartFood.itemAnimator = DefaultItemAnimator()
        rvCartFood.isNestedScrollingEnabled = true
    }

    private fun callApiCartQTYUpdate(
        cartModel: CartItemModel,
        pos: Int,
        isPlus: Boolean
    ) {
        var qty=0
        if(isPlus){
            qty=cartModel.getQty()!!.toInt()+1
        }else{
            qty=cartModel.getQty()!!.toInt()-1
        }
        showLoadingProgress(this@CartActivity)



        val map = HashMap<String, String>()
//        map.put("cart_id",cartModel.getId()!!)
//        map.put("item_id",cartModel.getItem_id()!!)
        var status=dbHelper.updateCart(cartModel.getId().toString(),qty,(cartModel.getPrice()!!.toDouble() * qty).toString())
        callApiCart(true)
//        map.put("qty",qty.toString())
//        map.put("user_id", getStringPref(this@CartActivity, SharePreference.userId)!!)
//        val call = ApiClient.getClient.setQtyUpdate(map)
//        call.enqueue(object : Callback<SingleResponse> {
//            override fun onResponse(
//                call: Call<SingleResponse>,
//                response: Response<SingleResponse>
//            ) {
//                if (response.code() == 200) {
//                    val restResponce: SingleResponse = response.body()!!
//                    if(restResponce.getStatus().equals("1")){
//                        callApiCart(true)
//                    }else{
//                        dismissLoadingProgress()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
//                dismissLoadingProgress()
//                alertErrorOrValidationDialog(
//                    this@CartActivity,
//                    resources.getString(R.string.error_msg)
//                )
//            }
//        })
    }
    private fun callApiCartItemDelete(strCartId:String,pos:Int) {
        showLoadingProgress(this@CartActivity)
        val map = HashMap<String, String>()
        val status=dbHelper.deleteCart(strCartId)
        map.put("cart_id",strCartId)
        if (status!=-1) {
            dismissLoadingProgress()


                Common.isCartTrue=true
                Common.isCartTrueOut=true
                Common.showSuccessFullMsg(this@CartActivity,"x??a th??nh c??ng")
                cartItem!!.removeAt(pos)
                cartItemAdapter!!.notifyDataSetChanged()
                if(cartItem!!.size>0){
                    tvCheckout.visibility=View.VISIBLE
                }else{
                    tvCheckout.visibility=View.GONE
                    rvCartFood.visibility = View.GONE
                    tvNoDataFound.visibility = View.VISIBLE
                    tvCheckout.visibility = View.GONE
                }

        }
    }

    fun dlgDeleteConformationDialog(act: Activity, msg: String?,strCartId: String,pos:Int) {
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
            val m_view = m_inflater.inflate(R.layout.dlg_confomation, null, false)
            val textDesc: TextView = m_view.findViewById(R.id.tvDesc)
            textDesc.text = msg
            val tvOk: TextView = m_view.findViewById(R.id.tvYes)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                if (isCheckNetwork(this@CartActivity)) {
                    finalDialog.dismiss()
                    callApiCartItemDelete(strCartId,pos)
                } else {
                    alertErrorOrValidationDialog(
                        this@CartActivity,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
            val tvCancle: TextView = m_view.findViewById(R.id.tvNo)
            tvCancle.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun successFullDeleteDialog(act: Activity, msg: String?,pos:Int) {
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
                cartItem!!.removeAt(pos)
                cartItemAdapter!!.notifyDataSetChanged()
                if(cartItem!!.size>0){
                    tvCheckout.visibility=View.VISIBLE
                }else{
                    tvCheckout.visibility=View.GONE
                    rvCartFood.visibility = View.GONE
                    tvNoDataFound.visibility = View.VISIBLE
                    tvCheckout.visibility = View.GONE
                }
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@CartActivity, false)
    }

    private fun callApiIsOpen() {
        startActivity(Intent(this@CartActivity,OrderSummuryActivity::class.java))

    }
}
