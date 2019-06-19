package com.smart.car.helper

import java.io.Serializable

class ResponseData<T> : Serializable {

    var status: Int = 0
    var info: String? = null
    var data: T? = null

    companion object {

        private const val serialVersionUID = 5213230387175987834L
    }
}