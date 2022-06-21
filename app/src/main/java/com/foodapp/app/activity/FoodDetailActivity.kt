package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.foodapp.app.R
import com.foodapp.app.adaptor.ImageSliderAdaptor
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.RestResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.isCheckNetwork
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import com.foodapp.app.utils.SharePreference.Companion.isCurrancy
import com.foodapp.app.utils.SharePreference.Companion.userId
import com.bumptech.glide.Glide
import com.foodapp.app.model.*
import com.foodapp.app.sqlite.DatabaseHandler
import com.foodapp.app.utils.Common.getLog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_foodorderdetail.*
import kotlinx.android.synthetic.main.row_addons.view.*
import kotlinx.android.synthetic.main.row_cart.view.*
import kotlinx.android.synthetic.main.row_ingrediants.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class FoodDetailActivity : BaseActivity() {
    var timer: Timer? = null
    private var currentPage = 0
    private var imagelist: ArrayList<String>? = null
    private var imagelistPlaceHolder: ArrayList<Drawable>? = null
    var itemModel: ItemDetailModel? = null
    var listSeletecAddons = ArrayList<AddonsModel>()
    var qty = 0
    internal var dbHelper =  DatabaseHandler(this)
    override fun setLayout(): Int {
        return R.layout.activity_foodorderdetail
    }

    override fun InitView() {
        imagelist = ArrayList()
        imagelistPlaceHolder = ArrayList()

        if (isCheckNetwork(this@FoodDetailActivity)) {
            callApiFoodDetail(intent.getStringExtra("foodItemId")!!)
        } else {
            alertErrorOrValidationDialog(
                this@FoodDetailActivity,
                resources.getString(R.string.no_internet)
            )
        }

        ivBack.setOnClickListener {
            finish()
        }
        ivCart.setOnClickListener {
            if (SharePreference.getBooleanPref(this@FoodDetailActivity, SharePreference.isLogin)) {
                openActivity(CartActivity::class.java)
            }else{
                openActivity(LoginActivity::class.java)
                finish()
                finishAffinity()
            }
        }
    }

    private fun loadPagerImages(imageHase: ArrayList<*>) {
        val adapter = ImageSliderAdaptor(this@FoodDetailActivity, imageHase)
        tabLayout.setupWithViewPager(viewPager, true)
        viewPager.setAdapter(adapter)
        val handler = Handler()

        if (imageHase.size == 1) {
            tabLayout.setVisibility(View.GONE)
        }
        val Update = Runnable {
            if (currentPage == imageHase.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(Update)
            }
        }, 500, 3000)


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onPause() {
        super.onPause()
        if (timer != null)
            timer!!.cancel()
    }


    override fun onResume() {
        super.onResume()
        if (SharePreference.getBooleanPref(this@FoodDetailActivity, SharePreference.isLogin)) {
            rlCount.visibility=View.VISIBLE
            if(Common.isCartTrue){
                if (isCheckNetwork(this@FoodDetailActivity)) {
                    Common.isCartTrue=false
                    callApiCartCount(false,false,"")
                } else {
                    alertErrorOrValidationDialog(this@FoodDetailActivity, resources.getString(R.string.no_internet))
                }
            }
        }else{
            rlCount.visibility=View.GONE
            timer = Timer()
            val handler = Handler()
            val Update = Runnable {
                if (currentPage == imagelist!!.size) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage++, true)
            }

            if (imagelist!!.size == 1) {
                tabLayout.visibility = View.GONE
            }
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(Update)
                }
            }, 4000, 3000)
        }

    }


    private fun callApiFoodDetail(strFoodId: String) {
        showLoadingProgress(this@FoodDetailActivity)
        val map = HashMap<String, String>()
        map.put("item_id", strFoodId)
        val call = ApiClient.getClient.setItemDetail(map)
        call.enqueue(object : Callback<RestResponse<ItemDetailModel>> {
            override fun onResponse(
                call: Call<RestResponse<ItemDetailModel>>,
                response: Response<RestResponse<ItemDetailModel>>
            ) {
                if (response.code() == 200) {
                    //dismissLoadingProgress()
                    val restResponce: RestResponse<ItemDetailModel> = response.body()!!
                    setRestaurantData(restResponce.getData())
                    if(SharePreference.getBooleanPref(this@FoodDetailActivity,SharePreference.isLogin)){
                        if (isCheckNetwork(this@FoodDetailActivity)) {
                            callApiCartCount(true,false,"")
                            dismissLoadingProgress()
                        } else {
                            alertErrorOrValidationDialog(this@FoodDetailActivity, resources.getString(R.string.no_internet))
                        }
                    }else{
                        dismissLoadingProgress()
                    }

                }else{
                    if(SharePreference.getBooleanPref(this@FoodDetailActivity,SharePreference.isLogin)){
                        if (isCheckNetwork(this@FoodDetailActivity)) {
//                            callApiCartCount(true,false,"")
                            dismissLoadingProgress()
                        } else {
                            alertErrorOrValidationDialog(this@FoodDetailActivity, resources.getString(R.string.no_internet))
                        }
                    }else{
                        dismissLoadingProgress()
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<ItemDetailModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@FoodDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiAddToCart(
        itemDetailModel: ItemDetailModel,
        strAddonsGetId: String,
        listAddons: ArrayList<AddonsModel>
    ) {
        showLoadingProgress(this@FoodDetailActivity)
        val price = tvAddtoCart.text.toString().replace(resources.getString(R.string.addtocart)+" "+getStringPref(this@FoodDetailActivity, isCurrancy), "")

        val actulePrice=price.replace(",", "")
        Common.getLog("getPrice", actulePrice)

        val cartmodel=CartItemModel(
            Integer.parseInt(itemDetailModel.getId()!!),
            Integer.parseInt(getStringPref(this@FoodDetailActivity, userId)!!),
            itemDetailModel.getItem_name(),
            itemDetailModel.getItem_price(),
            actulePrice,
            strAddonsGetId,
            Integer.parseInt(tvFoodQty.text.toString()),
            edNotes.text.toString(),
            intent.getStringExtra("itemimage")!!

            );
      var status= dbHelper.addCart(cartmodel,listAddons)



        if (isCheckNetwork(this@FoodDetailActivity)) {
            callApiCartCount(true,true, "thêm giỏ hàng thành công")
        } else {
            alertErrorOrValidationDialog(this@FoodDetailActivity, resources.getString(R.string.no_internet))
        }

                val listAddos = ArrayList<AddonsModel>()
                setSelectedAddonsAdaptor(listAddos)
                edNotes.setText("")
                qty=0
                tvFoodQty.text = "1"
                tvAddtoCart.text = resources.getString(R.string.addtocart)+" "+ getStringPref(this@FoodDetailActivity, isCurrancy).plus(String.format(Locale.US,"%,.2f",itemModel!!.getItem_price()!!.toDouble()))
                listAddons.clear()

    }

    @SuppressLint("SetTextI18n", "NewApi")
    private fun setRestaurantData(restResponce: ItemDetailModel?) {
        itemModel = restResponce
        if (restResponce!!.getImages()!!.size > 0) {
            val listImage = ArrayList<String>()
            for (i in 0 until restResponce.getImages()!!.size) {
                listImage.add(restResponce.getImages()!!.get(i).getItemimage()!!)
            }
            loadPagerImages(listImage)
        } else {
            imagelistPlaceHolder!!.add(ResourcesCompat.getDrawable(resources,R.drawable.placeholder,null)!!)
            loadPagerImages(imagelistPlaceHolder!!)
        }

//        if (restResponce.getIngredients()!!.size > 0) {
//            rvIngredients.visibility = View.VISIBLE
//            tvNoDataFound.visibility = View.GONE
//            setItemIngrdiantsAdaptor(restResponce.getIngredients()!!)
//        } else {
//            rvIngredients.visibility = View.GONE
//            tvNoDataFound.visibility = View.VISIBLE
//        }


        tvFoodName.text = restResponce.getItem_name()
        tvFoodPrice.text = getStringPref(this@FoodDetailActivity, isCurrancy)+String.format(Locale.US,"%,.2f",restResponce.getItem_price()!!.toDouble())
        tvFoodType.text = restResponce.getCategory_name()
//        tvTime.text = restResponce.getDelivery_time()
        tvDetail.text = restResponce.getItem_description()

        tvAddtoCart.text = resources.getString(R.string.addtocart)+" ${getStringPref(
            this@FoodDetailActivity,
            isCurrancy
        ).plus(String.format(Locale.US,"%,.2f",itemModel!!.getItem_price()!!.toDouble()))}"

        qty=tvFoodQty.text.toString().toInt()
        ivMinus.setOnClickListener {
            if (qty > 1) {
                // ivMinus.isClickable = true
                qty = qty - 1
                tvFoodQty.text = qty.toString()
                if (listSeletecAddons.size > 0) {
                    var price: Double = 0.0
                    for (i in 0 until listSeletecAddons.size) {
                        price = price + listSeletecAddons.get(i).getPrice()!!.toDouble()
                    }
                    val getPrice = qty * (price + itemModel!!.getItem_price()!!.toDouble())
                    tvAddtoCart.text =
                        resources.getString(R.string.addtocart)+" "+String.format(Locale.US,"%,.2f",getPrice).plus(
                            getStringPref(this@FoodDetailActivity, isCurrancy)
                        )
                } else {
                    val getPrice = qty * itemModel!!.getItem_price()!!.toDouble()
                    tvAddtoCart.text =
                        resources.getString(R.string.addtocart)+" "+String.format(Locale.US,"%,.2f",getPrice).plus(
                            getStringPref(this@FoodDetailActivity, isCurrancy)
                        )
                }
            }
        }

        ivPlus.setOnClickListener {
            if(qty<100){
                qty = qty + 1
                tvFoodQty.text = qty.toString()
                if (listSeletecAddons.size > 0) {
                    var price: Double = 0.0
                    for (i in 0 until listSeletecAddons.size) {
                        price = price + listSeletecAddons.get(i).getPrice()!!.toDouble()
                    }
                    val getPrice = qty * (price + itemModel!!.getItem_price()!!.toDouble())
                    tvAddtoCart.text =
                        resources.getString(R.string.addtocart)+" "+String.format(Locale.US,"%,.2f",getPrice).plus(
                            getStringPref(this@FoodDetailActivity, isCurrancy)
                        )
                } else {
                    val getPrice = qty * itemModel!!.getItem_price()!!.toDouble()
                    tvAddtoCart.text =
                        resources.getString(R.string.addtocart)+" "+String.format(Locale.US,"%,.2f",getPrice).plus(
                            getStringPref(this@FoodDetailActivity, isCurrancy)
                        )
                }
            }else{
                alertErrorOrValidationDialog(this@FoodDetailActivity,"Maximum quantity allowed ${getStringPref(this@FoodDetailActivity,SharePreference.isMiniMumQty)}")
            }

        }

        if(itemModel!!.getItem_status().equals("2")){
            tvAddtoCart.visibility=View.GONE
            rlItem.visibility=View.VISIBLE
            rlQty.visibility=View.GONE
        }else{
            tvAddtoCart.visibility=View.VISIBLE
            rlItem.visibility=View.GONE
            rlQty.visibility=View.VISIBLE
        }

        if (restResponce.getAddons().size > 0) {
            ivAddAdons.imageTintList =ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
        } else {
            ivAddAdons.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null))
        }


        var listAddons = ArrayList<AddonsModel>()
        ivAddAdons.setOnClickListener {
            if (restResponce.getAddons().size > 0) {
                val list = restResponce.getAddons()
                for (i in 0 until list.size) {
                    list[i].setSelectAddons(false)
                }
                listAddons = openDialogAddons(this@FoodDetailActivity, list)
            }
        }

        tvAddtoCart.setOnClickListener {
            if (SharePreference.getBooleanPref(this@FoodDetailActivity, SharePreference.isLogin)) {
                var strAddonsGetId = ""
                Common.getLog("getFinalAddonsList", Gson().toJson(listAddons))
                if (listAddons.size > 0) {
                    if (listAddons.size == 1) {
                        strAddonsGetId = listAddons.get(0).getId()!!
                    } else if (listAddons.size == 2) {
                        strAddonsGetId = listAddons.get(0).getId() + "," + listAddons.get(1).getId()
                    } else if (listAddons.size > 2) {
                        val get = listAddons.size - 1
                        Common.getLog("getFinalListId", get.toString())
                        for (i in 0 until listAddons.size) {
                            if (i == 0) {
                                strAddonsGetId = listAddons.get(i).getId()!! + ","
                            } else if (i == get) {
                                strAddonsGetId = strAddonsGetId + listAddons.get(i).getId()
                            } else {
                                strAddonsGetId = strAddonsGetId + listAddons.get(i).getId() + ","
                            }
                        }
                    }
                }

                if (isCheckNetwork(this@FoodDetailActivity)) {
                    callApiAddToCart(itemModel!!, strAddonsGetId,listAddons)

                } else {
                    alertErrorOrValidationDialog(
                        this@FoodDetailActivity,
                        resources.getString(R.string.no_internet)
                    )
                }
            } else {
                openActivity(LoginActivity::class.java)
                finish()
            }

        }
    }

    private fun setItemIngrdiantsAdaptor(ingredientsItemList: ArrayList<IngredientsModel>) {
        val cartItemAdapter =
            object : BaseAdaptor<IngredientsModel>(this@FoodDetailActivity, ingredientsItemList) {
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: IngredientsModel,
                    position: Int
                ) {
                    Glide.with(this@FoodDetailActivity)
                        .load(ingredientsItemList.get(position).getIngredients_image())
                        .into(holder!!.itemView.ivIngrediants)
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_ingrediants
                }

                override fun setNoDataView(): TextView? {
                    return null
                }
            }
//        rvIngredients.adapter = cartItemAdapter
//        rvIngredients.layoutManager =
//            LinearLayoutManager(this@FoodDetailActivity, LinearLayoutManager.HORIZONTAL, false)
//        rvIngredients.itemAnimator = DefaultItemAnimator()
//        rvIngredients.isNestedScrollingEnabled = true9
    }


    @SuppressLint("SetTextI18n")
    fun openDialogAddons(
        activity: Activity,
        addonsList: ArrayList<AddonsModel>
    ): ArrayList<AddonsModel> {
        val dialog: Dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.windowAnimations = R.style.DialogAnimation
        dialog.window!!.attributes = lp
        dialog.setContentView(R.layout.dlg_addons)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val ivCancel = dialog.findViewById<ImageView>(R.id.ivCancel)
        val rvAddons = dialog.findViewById<RecyclerView>(R.id.rvAddons)
        val tvDone = dialog.findViewById<TextView>(R.id.tvDone)
        val addonsSeletedList = setAddonsAdaptor(rvAddons, addonsList)
        ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        val addonsSelectedList = ArrayList<AddonsModel>()
        tvDone.setOnClickListener {
            addonsSelectedList.clear()
            for (i in 0 until addonsSeletedList.size) {
                if (addonsSeletedList[i].isSelectAddons()!!) {
                    val addonsModel = addonsSeletedList[i]
                    addonsSelectedList.add(addonsModel)
                }
            }
            listSeletecAddons = setSelectedAddonsAdaptor(addonsSelectedList)
            var price = 0.0
            for (i in 0 until listSeletecAddons.size) {
                price = price + listSeletecAddons.get(i).gettotalPrice()!!.toDouble()
            }
            val getPrice =
                tvFoodQty.text.toString().toInt() * (price + itemModel!!.getItem_price()!!
                    .toDouble())
            tvAddtoCart.text =
                resources.getString(R.string.addtocart)+" "+getStringPref(this@FoodDetailActivity, isCurrancy).plus(String.format(Locale.US,"%,.2f",getPrice))
            dialog.dismiss()
        }
        dialog.show()
        return addonsSelectedList
    }

    private fun setAddonsAdaptor(
        rvAddons: RecyclerView,
        addonsList: ArrayList<AddonsModel>
    ): ArrayList<AddonsModel> {
        var getAddonsList = addonsList
        var getAddonsList1=addonsList
        Common.getLog("getAddonsData", "===" + Gson().toJson(getAddonsList).toString() + "")
        val addonsItemAdapter =
            object : BaseAdaptor<AddonsModel>(this@FoodDetailActivity, getAddonsList) {
                @SuppressLint("NewApi", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: AddonsModel,
                    position: Int
                ) {
                    Glide.with(this@FoodDetailActivity).load(getAddonsList[position].getImages()!!.getItemimageadd()).into(holder!!.itemView.ivimageaddon)
                    Common.getLog("image", getAddonsList[position].getImages().toString())
                    val tvAddonsName: TextView = holder!!.itemView.findViewById(R.id.tvAddonsName)
                    val tvAddonsPrice: TextView = holder.itemView.findViewById(R.id.tvAddonsPrice)
                    val ivCheck: ImageView = holder.itemView.findViewById(R.id.ivCheck)
                    tvAddonsName.text = getAddonsList[position].getName()
                    if(String.format(Locale.US,"%.2f",getAddonsList[position].getPrice()!!.toDouble())=="0.00"){
                        tvAddonsPrice.text ="Free"
                    }else{
                        tvAddonsPrice.text =(getAddonsList[position].getPrice()!!.toDouble() * Integer.parseInt(holder.itemView.tvFoodQty1.text.toString())).toString()
                    }

                    holder.itemView.ivPlus1.setOnClickListener {
                        holder.itemView.ivPlus1.isClickable =true
                        var soluong=Integer.parseInt(holder.itemView.tvFoodQty1.text.toString())+1

                        var gia=0.0;
                         gia=soluong * getAddonsList[position].getPrice()!!.toDouble()
                        holder.itemView.tvFoodQty1.text=soluong.toString()
                        holder.itemView.tvAddonsPrice.text=gia.toString()

//                        getAddonsList[position].setPrice(gia.toString())
                        getLog("price",getAddonsList[position].getPrice().toString())

//                        notifyDataSetChanged()
                    }
                    holder.itemView.ivMinus1.setOnClickListener {
                        if(Integer.parseInt(holder.itemView.tvFoodQty1.text.toString())>1){

                            holder.itemView.ivPlus1.isClickable =true
                            var soluong=Integer.parseInt(holder.itemView.tvFoodQty1.text.toString())-1
                            var gia=soluong * getAddonsList[position].getPrice()!!.toDouble()

                            holder.itemView.tvFoodQty1.text=soluong.toString()
                            holder.itemView.tvAddonsPrice.text=gia.toString()

//                            getAddonsList[position].setPrice(gia.toString())
//                            getLog("price",getAddonsList1[position].getPrice().toString())
//                            notifyDataSetChanged()
                        }else if(Integer.parseInt(holder.itemView.tvFoodQty1.text.toString())<1){

                            holder.itemView.ivMinus1.isClickable =false
                        }


                    }

                  /*  tvAddonsPrice.text = getStringPref(
                        this@FoodDetailActivity,
                        isCurrancy
                    )+String.format(Locale.US,"%.2f",getAddonsList[position].getPrice()!!.toDouble())*/

                    if (getAddonsList[position].isSelectAddons()!!) {
                        ivCheck.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_check,null))
                        ivCheck.imageTintList =
                            ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                    } else {
                        ivCheck.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_uncheck,null))
                        ivCheck.imageTintList =
                            ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                    }

                    ivCheck.setOnClickListener {
                        if (getAddonsList[position].isSelectAddons()!!) {
                            Common.getLog("check",getAddonsList[position].isSelectAddons()!!.toString())
                            ivCheck.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_uncheck,null))
                            ivCheck.imageTintList =
                                ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                            getAddonsList[position].setSelectAddons(false)
                            getAddonsList[position].settotalPrice(holder.itemView.tvAddonsPrice.text.toString())
                            getAddonsList[position].setsoluong(holder.itemView.tvFoodQty1.text.toString())
                            getLog("totalprice",getAddonsList[position].getsoluong().toString())
                            notifyDataSetChanged()
                        } else {
                            Common.getLog("check",getAddonsList[position].isSelectAddons()!!.toString())

                            ivCheck.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_check,null))
                            ivCheck.imageTintList =
                                ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                            getAddonsList[position].setSelectAddons(true)
                            getAddonsList[position].settotalPrice(holder.itemView.tvAddonsPrice.text.toString())
                            getAddonsList[position].setsoluong(holder.itemView.tvFoodQty1.text.toString())
                            getLog("totalprice",getAddonsList[position].getsoluong().toString())
                            notifyDataSetChanged()
                        }
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_addons
                }

                override fun setNoDataView(): TextView? {
                    return null
                }
            }
        rvAddons.adapter = addonsItemAdapter
        rvAddons.layoutManager = LinearLayoutManager(this@FoodDetailActivity)
        rvAddons.itemAnimator = DefaultItemAnimator()
        rvAddons.isNestedScrollingEnabled = true
        return getAddonsList
    }

    private fun setSelectedAddonsAdaptor(lisAddons: ArrayList<AddonsModel>): ArrayList<AddonsModel> {
        val getSelectedlist = lisAddons
        val selectedaddonsItemAdapter =
            object : BaseAdaptor<AddonsModel>(this@FoodDetailActivity, getSelectedlist) {
                @SuppressLint("SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: AddonsModel,
                    position: Int
                ) {

                    Glide.with(this@FoodDetailActivity).load(lisAddons[position].getImages()!!.getItemimageadd()).into(holder!!.itemView.ivimageaddon)

                    val tvAddonsName: TextView = holder!!.itemView.findViewById(R.id.tvAddonsName)
                    val tvAddonsPrice: TextView = holder.itemView.findViewById(R.id.tvAddonsPrice)
                    val tvqly: TextView = holder.itemView.findViewById(R.id.tvFoodQty2)
                    val ivCancel: ImageView = holder.itemView.findViewById(R.id.ivCancel)
                    tvAddonsName.text = lisAddons[position].getName()
                    if(String.format(Locale.US,"%,.2f",lisAddons[position].getPrice()!!.toDouble())=="0.00"){
                        tvAddonsPrice.text ="Free"
                    }else{
                        tvAddonsPrice.text =lisAddons[position].gettotalPrice()
                        tvqly.text="x"+lisAddons[position].getsoluong()
                    }

                    ivCancel.setOnClickListener {
                        lisAddons.removeAt(position)
                        if (lisAddons.size > 0) {
                            rvAddons.visibility = View.VISIBLE
                            tvNoDataAddonsFound.visibility = View.GONE
                            var price = 0.0
                            for (i in 0 until getSelectedlist.size) {
                                price = price + getSelectedlist.get(i).getPrice()!!.toDouble()
                            }
                            val getPrice:Double = tvFoodQty.text.toString().toInt()*(price + itemModel!!.getItem_price()!!.toDouble())
                            tvAddtoCart.text = resources.getString(R.string.addtocart)+" "+String.format(Locale.US,"%,.2f",getPrice).plus(getStringPref(this@FoodDetailActivity,isCurrancy))
                        } else {
                            rvAddons.visibility = View.GONE
                            tvNoDataAddonsFound.visibility = View.VISIBLE
                            val getPrice:Double =
                                tvFoodQty.text.toString().toInt() * itemModel!!.getItem_price()!!
                                    .toDouble()
                            tvAddtoCart.text = resources.getString(R.string.addtocart)+" "+String.format(Locale.US,"%,.2f",getPrice).plus(getStringPref(this@FoodDetailActivity,isCurrancy))

                        }
                        notifyDataSetChanged()
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_selectedaddons
                }

                override fun setNoDataView(): TextView? {
                    return null
                }
            }

        if (lisAddons.size > 0) {
            rvAddons.visibility = View.VISIBLE
            tvNoDataAddonsFound.visibility = View.GONE
            rvAddons.adapter = selectedaddonsItemAdapter
            rvAddons.layoutManager = LinearLayoutManager(this@FoodDetailActivity)
            rvAddons.itemAnimator = DefaultItemAnimator()
            rvAddons.isNestedScrollingEnabled = true
        } else {
            rvAddons.visibility = View.GONE
            tvNoDataAddonsFound.visibility = View.VISIBLE
        }

        return getSelectedlist

    }

    private fun callApiCartCount(isFristTime: Boolean,isQtyUpdate:Boolean,strMsg:String) {
        if(!isFristTime){
            Common.getLog("fisttime",isFristTime.toString())
            showLoadingProgress(this@FoodDetailActivity)
        }



                tvCount.text=dbHelper.viewCart().size.toString()
                if(isQtyUpdate){
                    Common.isCartTrueOut=true
                    Common.showSuccessFullMsg(this@FoodDetailActivity,strMsg)
                    dismissLoadingProgress()
                }else{
                    dismissLoadingProgress()
                }
                Common.getLog("fisttime",isFristTime.toString())
                if(!isFristTime){

                    timer = Timer()
                    val handler = Handler()
                    val Update = Runnable {
                        if (currentPage == imagelist!!.size) {
                            currentPage = 0
                        }
                        viewPager.setCurrentItem(currentPage++, true)
                    }

                    if (imagelist!!.size == 1) {
                        tabLayout.visibility = View.GONE
                    }
                    timer!!.schedule(object : TimerTask() {
                        override fun run() {
                            handler.post(Update)
                        }
                    }, 4000, 3000)
                }



    }

    var dialog: Dialog? = null

    fun dismissLoadingProgress() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    fun showLoadingProgress(context: Activity) {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dlg_progress)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }
}