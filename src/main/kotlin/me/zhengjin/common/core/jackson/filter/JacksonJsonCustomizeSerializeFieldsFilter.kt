/*
 * MIT License
 *
 * Copyright (c) 2022 ZhengJin Fang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.zhengjin.common.core.jackson.filter

import com.fasterxml.jackson.annotation.JsonFilter
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter
import com.fasterxml.jackson.databind.ser.FilterProvider
import com.fasterxml.jackson.databind.ser.PropertyFilter
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import kotlin.reflect.KClass

/**
 * 自定义属性过滤器
 */
@JsonFilter("jacksonJsonCustomizeSerializeFieldsFilter")
class JacksonJsonCustomizeSerializeFieldsFilter : FilterProvider() {
    private var includeMap = mutableMapOf<KClass<*>, MutableSet<String>>()
    private var excludeMap = mutableMapOf<KClass<*>, MutableSet<String>>()

    fun include(type: KClass<*>, vararg fields: String): JacksonJsonCustomizeSerializeFieldsFilter {
        addToMap(includeMap, type, fields.toMutableList())
        return this
    }

    fun exclude(type: KClass<*>, vararg fields: String): JacksonJsonCustomizeSerializeFieldsFilter {
        addToMap(excludeMap, type, fields.toMutableList())
        return this
    }

    private fun addToMap(map: MutableMap<KClass<*>, MutableSet<String>>, type: KClass<*>, fields: MutableList<String>) {
        val fieldSet = map.getOrDefault(type, mutableSetOf())
        fieldSet.addAll(fields)
        map[type] = fieldSet
    }

    @Deprecated("Deprecated in Java")
    override fun findFilter(filterId: Any?): BeanPropertyFilter {
        throw UnsupportedOperationException("Access to deprecated filters not supported")
    }

    override fun findPropertyFilter(filterId: Any?, valueToFilter: Any?): PropertyFilter {
        return object : SimpleBeanPropertyFilter() {
            override fun serializeAsField(pojo: Any, jgen: JsonGenerator, provider: SerializerProvider, writer: PropertyWriter) {
                if (apply(pojo::class, writer.name)) {
                    writer.serializeAsField(pojo, jgen, provider)
                } else if (!jgen.canOmitFields()) {
                    writer.serializeAsOmittedField(pojo, jgen, provider)
                }
            }
        }
    }

    fun apply(type: KClass<*>?, name: String): Boolean {
        val includeFields: MutableSet<String>? = includeMap[type]
        val excludeFields: MutableSet<String>? = excludeMap[type]

        if (!includeFields.isNullOrEmpty() && includeFields.contains(name)) {
            return true
        }

        if (!excludeFields.isNullOrEmpty()) {
            // 如果同时使用了包含与排除 包含优先
            return if (includeFields.isNullOrEmpty()) {
                !excludeFields.contains(name)
            } else {
                includeFields.contains(name)
            }
        }

        if (includeFields.isNullOrEmpty() && excludeFields.isNullOrEmpty()) {
            return true
        }

        return false
    }
}
