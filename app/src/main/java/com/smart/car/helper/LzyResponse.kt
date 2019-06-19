/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smart.car.helper

import java.io.Serializable

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
class LzyResponse<T> : Serializable {

    var code: Int = 0
    var msg: String? = null
    var data: T? = null

    override fun toString(): String {
        return "LzyResponse{\n" +//

                "\tcode=" + code + "\n" +//

                "\tmsg='" + msg + "\'\n" +//

                "\tdata=" + data + "\n" +//

                '}'.toString()
    }

    companion object {

        private const val serialVersionUID = 5213230387175987834L
    }
}
