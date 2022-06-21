package com.foodapp.app.model

class AddonsModel {
//    private var item_id: String? = null
    private var itemimage: ImagesModel?=null
    private var price: String? = null
    private var totalprice: String? = null
    private var name: String? = null
    private  var soluong:String?=null
    private var id: String? = null
    private var imageaddon:String?=null
    private var isAddosSelect:Boolean=false

    constructor(id: String?,name: String?,imageaddon:String?,totalprice: String?,soluong: String?){

        this.name=name
        this.totalprice=totalprice
        this.imageaddon=imageaddon
        this.soluong=soluong
        this.id=id

    }
    fun getImages(): ImagesModel? {
        return itemimage
    }

    fun setImages(images: ImagesModel?) {
        this.itemimage = images
    }
    fun getsoluong(): String? {
        return soluong
    }

    fun setsoluong(soluong: String?) {
        this.soluong = soluong
    }
    fun getimageadd(): String? {
        return imageaddon
    }

    fun setimageadd(imageaddon: String?) {
        this.imageaddon = imageaddon
    }
//    fun getItem_id(): String? {
//        return item_id
//    }
//
//    fun setItem_id(item_id: String?) {
//        this.item_id = item_id
//    }

    fun getPrice(): String? {
        return price
    }

    fun setPrice(price: String?) {
        this.price = price
    }
    fun gettotalPrice(): String? {
        return totalprice
    }

    fun settotalPrice(price: String?) {
        this.totalprice = price
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun isSelectAddons(): Boolean? {
        return isAddosSelect
    }

    fun setSelectAddons(isAddosSelect: Boolean?) {
        this.isAddosSelect = isAddosSelect!!
    }



}