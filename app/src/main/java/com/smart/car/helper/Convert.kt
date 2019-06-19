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


import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.readystatesoftware.chuck.internal.support.JsonConvertor

import java.io.Reader
import java.lang.reflect.Type

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/28
 * 描    述： Gson 数据转换工具类
 * 修订历史：
 * ================================================
 */
object Convert {

    @JvmStatic
    fun create(): Gson {
        return Convert.GsonHolder.gson
    }

    object GsonHolder {
        internal val gson = Gson()
    }

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(json: String, type: Class<T>): T {
        return create().fromJson(json, type)
    }

    fun <T> fromJson(json: String, type: Type): T {
        return create().fromJson(json, type)
    }

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(reader: JsonReader, typeOfT: Type): T {
        return create().fromJson(reader, typeOfT)
    }

    @Throws(JsonSyntaxException::class, JsonIOException::class)
    fun <T> fromJson(json: Reader, classOfT: Class<T>): T {
        return create().fromJson(json, classOfT)
    }

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(json: Reader, typeOfT: Type): T {
        return create().fromJson(json, typeOfT)
    }

    @JvmStatic
    fun toJson(src: Any): String {
        return create().toJson(src)
    }

    fun toJson(src: Any, typeOfSrc: Type): String {
        return create().toJson(src, typeOfSrc)
    }

    fun formatJson(json: String): String {
        try {
            val jp = JsonParser()
            val je = jp.parse(json)
            return JsonConvertor.getInstance().toJson(je)
        } catch (e: Exception) {
            return json
        }

    }

    fun formatJson(src: Any): String {
        return try {
            val jp = JsonParser()
            val je = jp.parse(toJson(src))
            JsonConvertor.getInstance().toJson(je)
        } catch (e: Exception) {
            e.message.toString()
        }

    }
}
