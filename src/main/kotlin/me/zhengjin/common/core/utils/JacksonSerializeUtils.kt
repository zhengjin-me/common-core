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

package me.zhengjin.common.core.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import me.zhengjin.common.core.jackson.filter.JacksonJsonCustomizeSerializeFieldsFilter
import java.util.TimeZone
import kotlin.reflect.KClass

object JacksonSerializeUtils {

    @JvmStatic
    fun defaultJacksonConfig(objectMapper: ObjectMapper = ObjectMapper()): ObjectMapper {
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS)
        // 默认为ISO8601格式时间
        objectMapper.dateFormat = StdDateFormat().withTimeZone(TimeZone.getTimeZone("Asia/Shanghai"))
        // 关闭反序列化出现未定义属性时 报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        // 关闭日期序列化为时间戳的功能
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        // 关闭序列化的时候没有为属性找到getter方法 报错
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        // 允许出现特殊字符和转义符
        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
        // 允许出现单引号
        objectMapper.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true)
        return objectMapper
    }

    @JvmStatic
    fun serializeFieldsFilterInclude(data: Any, type: KClass<*>, vararg fields: String): String {
        val objectMapper = defaultJacksonConfig(ObjectMapper())
        objectMapper.setFilterProvider(JacksonJsonCustomizeSerializeFieldsFilter().include(type, *fields))
        objectMapper.addMixIn(type.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
        return objectMapper.writeValueAsString(data)
    }

    @JvmStatic
    fun serializeFieldsFilterExclude(data: Any, type: KClass<*>, vararg fields: String): String {
        val objectMapper = defaultJacksonConfig(ObjectMapper())
        objectMapper.setFilterProvider(JacksonJsonCustomizeSerializeFieldsFilter().exclude(type, *fields))
        objectMapper.addMixIn(type.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
        return objectMapper.writeValueAsString(data)
    }

    @JvmStatic
    fun serializeFieldsFilter(data: Any, type: KClass<*>, includes: List<String> = listOf(), excludes: List<String> = listOf()): String {
        val objectMapper = defaultJacksonConfig(ObjectMapper())
        objectMapper.setFilterProvider(
            JacksonJsonCustomizeSerializeFieldsFilter()
                .include(type, *includes.toTypedArray())
                .exclude(type, *excludes.toTypedArray())
        )
        objectMapper.addMixIn(type.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
        return objectMapper.writeValueAsString(data)
    }
}
