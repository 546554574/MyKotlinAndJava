package com.smart.car.ui.model

class UpdateAppVo {

    /**
     * data : {"id":3,"num":"1.0.2","url":"http://www.baidu.com","content":"哈哈哈哈","is_must_update":0,"create_time":"2018-11-26 10:34:08","delete_time":null,"client":"android"}
     * info : ok
     * status : 1
     */

    var data: DataBean? = null
    var info: String? = null
    var status: Int = 0

    class DataBean {
        /**
         * id : 3
         * num : 1.0.2
         * url : http://www.baidu.com
         * content : 哈哈哈哈
         * is_must_update : 0
         * create_time : 2018-11-26 10:34:08
         * delete_time : null
         * client : android
         */

        var id: Int = 0
        var num: String? = null
        var url: String? = null
        var content: String? = null
        var is_must_update: Int = 0
        var create_time: String? = null
        var delete_time: Any? = null
        var client: String? = null
    }
}
