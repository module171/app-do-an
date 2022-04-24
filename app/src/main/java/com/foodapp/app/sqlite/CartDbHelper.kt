package com.foodapp.app.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException
import com.foodapp.app.model.CartItemModel

//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "CartDatabase"
        private val TABLE_CART = "Cart"
        private val TABLE_topping = "Topping"
        private val KEY_ID = "id"
        private val KEY_user_id = "user_id"
        private val KEY_item_id = "item_id"
        private val KEY_topping_id = "topping_id"
        private val KEY_qly = "qly"
        private val KEY_item_price = "item_price"
        private val KEY_price = "price"
        private val KEY_item_name = "item_name"
        private val KEY_item_notes = "item_notes"
        private val KEY_image = "imageitem"

    }
    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CART + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_user_id + " INTEGER,"
                + KEY_item_id + " INTEGER,"
                + KEY_topping_id + " TEXT,"
                + KEY_qly+ " INTEGER,"
                + KEY_price+ " TEXT,"
                + KEY_item_price+ " TEXT,"
                + KEY_image+ " TEXT,"
                + KEY_item_name+ " TEXT,"
                + KEY_item_notes + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CART)
        onCreate(db)
    }


    //method to insert data
    fun addCart(cart: CartItemModel):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
//        contentValues.put(KEY_ID, emp.userId)
        contentValues.put(KEY_item_id,cart.getItem_id()) // EmpModelClass Name
        contentValues.put(KEY_user_id,cart.getUser_id())
        contentValues.put(KEY_price,cart.getPrice())
        contentValues.put(KEY_item_notes,cart.getItem_notes())
        contentValues.put(KEY_qly,cart.getQty())
        contentValues.put(KEY_item_price,cart.getitemm_price())
        contentValues.put(KEY_item_name,cart.getItem_name())
        contentValues.put(KEY_topping_id,cart.getAddons_id())
        contentValues.put(KEY_image,cart.getimage())
        // EmpModelClass Phone
        // Inserting Row
        val success = db.insert(TABLE_CART, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to read data
    fun viewCart():ArrayList<CartItemModel>{
        val cartList:ArrayList<CartItemModel> = ArrayList<CartItemModel>()
        val selectQuery = "SELECT  * FROM ${TABLE_CART}"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var cartId: Int
        var itemid: Int
        var userid: Int
        var price: String
        var image: String
        var qly:Int
        var toppingid:String
        var item_notes:String
        var item_name:String
        var item_price:String
        if (cursor.moveToFirst()) {
            do {
                cartId = cursor.getInt(cursor.getColumnIndex("id"))
                itemid = cursor.getInt(cursor.getColumnIndex("item_id"))
                userid = cursor.getInt(cursor.getColumnIndex("user_id"))
                price = cursor.getString(cursor.getColumnIndex("price"))
                qly = cursor.getInt(cursor.getColumnIndex("qly"))
               toppingid = cursor.getString(cursor.getColumnIndex("topping_id"))
                item_notes= cursor.getString(cursor.getColumnIndex("item_notes"))
                image= cursor.getString(cursor.getColumnIndex("imageitem"))
                item_name=cursor.getString(cursor.getColumnIndex("item_name"))
                item_price=cursor.getString(cursor.getColumnIndex("item_price"))
                val emp= CartItemModel(cartId,itemid,userid,item_name,item_price,price,toppingid,qly,item_notes,image)
                cartList.add(emp)
            } while (cursor.moveToNext())
        }
        return cartList
    }
    //method to update data
    fun updateCart(id:String?,qly:Int?,price:String?):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(KEY_price,price)

        contentValues.put(KEY_qly,qly)


        // Updating Row
        val success = db.update(TABLE_CART, contentValues,"id=?",arrayOf(id))
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to delete data
    fun deleteCart(id:String?):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
//        contentValues.put(KEY_ID, emp.userId) // EmpModelClass UserId
        // Deleting Row
        val success = db.delete(TABLE_CART,"id=?" , arrayOf(id))
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}