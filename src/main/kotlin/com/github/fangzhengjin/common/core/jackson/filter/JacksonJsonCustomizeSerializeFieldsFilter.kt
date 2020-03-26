package com.github.fangzhengjin.common.core.jackson.filter

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
@Suppress("deprecation")
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

    override fun findFilter(filterId: Any?): BeanPropertyFilter {
        throw UnsupportedOperationException("Access to deprecated filters not supported");
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