package com.foodapp.app.model

class OrderdataModel {
    private var itemimage: FoodItemImageModel? = null
    private var item_id: Int? = null
    private var price: String? = null
    private var qty: Int? = null
    private var item_name: String? = null
    private var item_price: String? = null
    private var id: Int? = null
    private var addons_id: String? = null
    private var item_notes: String? = null
    private var addons: ArrayList<AddonsModel>?=null
    private var user_id: Int? = null
    private var image: String? = null
    private  var addonsModel:ArrayList<AddonsModel>?=null
    constructor(id: Int?,item_id: Int?,user_id: Int?,item_name: String?,item_price: String?,price: String?,addons_id: String?,qty: Int?,item_notes: String?,image: String?,addons: ArrayList<AddonsModel>?){

        this.id=id
        this.user_id=user_id
        this.item_id=item_id
        this.price=price
        this.item_name=item_name
        this.addons_id=addons_id
        this.qty=qty
        this.image=image
        this.item_notes=item_notes
        this.item_price=item_price

        this.addons=addons
    }
    constructor(item_id: Int?,user_id: Int?,item_name: String?,item_price: String?,price: String?,addons_id: String?,qty: Int?,item_notes: String?,image: String?){


        this.user_id=user_id
        this.item_id=item_id
        this.price=price
        this.item_name=item_name
        this.addons_id=addons_id
        this.qty=qty
        this.image=image
        this.item_notes=item_notes
        this.item_price=item_price

    }

    fun getitemm_price():String?{


        return item_price
    }
    fun setitem_price(item_price:String?){


        this.item_price=item_price
    }


    fun getUser_id():Int?{


        return user_id
    }
    fun setUser_id(user_id:Int?){


        this.user_id=user_id
    }
    fun getimage():String?{


        return image
    }
    fun setimage(image:String?){


        this.image=image
    }
    fun getItemimage(): FoodItemImageModel? {
        return itemimage
    }

    fun setItemimage(itemimage: FoodItemImageModel?) {
        this.itemimage = itemimage
    }

    fun getItem_id(): Int? {
        return item_id
    }

    fun setItem_id(item_id: Int?) {
        this.item_id = item_id
    }

    fun getPrice(): String? {
        return price
    }

    fun setPrice(price: String?) {
        this.price = price
    }

    fun getQty(): Int? {
        return qty
    }

    fun setQty(qty: Int?) {
        this.qty = qty
    }

    fun getItem_name(): String? {
        return item_name
    }

    fun setItem_name(item_name: String?) {
        this.item_name = item_name
    }

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getItem_notes(): String? {
        return item_notes
    }

    fun setItem_notes(item_notes: String?) {
        this.item_notes = item_notes
    }

    fun getAddons(): ArrayList<AddonsModel> {
        return addons!!
    }

    fun setAddons(addons: ArrayList<AddonsModel>?) {
        this.addons = addons
    }

    fun getAddons_id(): String? {
        return addons_id
    }

    fun setAddons_id(addons_id: String?) {
        this.addons_id = addons_id
    }
}