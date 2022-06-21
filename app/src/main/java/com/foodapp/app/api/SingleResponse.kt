package com.foodapp.app.api

class SingleResponse {
    private var message: String? = null

    private var status: String? = null
    private var order_id: Int? = null
    fun getMessage(): String? {
        return message
    }

    fun setOrder_id(order_ID: Int?) {
        this.order_id = order_ID
    }
    fun getOrder_id(): Int? {
        return order_id
    }

    fun setMessage(message: String?) {
        this.message = message
    }
    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

}