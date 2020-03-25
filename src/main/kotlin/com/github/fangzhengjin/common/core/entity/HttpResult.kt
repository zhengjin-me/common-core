package com.github.fangzhengjin.common.core.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.fasterxml.jackson.databind.util.StdDateFormat
import org.springframework.http.HttpStatus
import java.io.Serializable
import java.util.*

/**
 * @version V1.0
 * @title: HttpResult
 * @package com.github.fangzhengjin.common.core.entity
 * @description: 响应数据标准类
 * @author fangzhengjin
 * @date 2019/1/28 14:52
 */
class HttpResult<T> private constructor(
        var code: Int? = 200,
        var message: String? = "",
        @Suppress("UNCHECKED_CAST")
        var body: T? = "" as T
) : Serializable {

    val isOk: Boolean
        get() {
            return HttpStatus.OK.value() == this.code
        }

    override fun toString(): String {
        val objectMapper = ObjectMapper()
        defaultJacksonConfig(objectMapper)
        return objectMapper.writeValueAsString(this)
    }

    companion object {

        @JvmStatic
        fun defaultJacksonConfig(objectMapper: ObjectMapper) {
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
        }

        @JvmStatic
        @JvmOverloads
        fun ok(message: String? = null): HttpResult<String> {
            return HttpResult(
                    code = HttpStatus.OK.value(),
                    message = message ?: HttpStatus.OK.reasonPhrase,
                    body = null
            )
        }

        @JvmStatic
        @JvmOverloads
        fun <T> ok(body: T?, message: String? = null): HttpResult<T> {
            return HttpResult(
                code = HttpStatus.OK.value(),
                message = message ?: HttpStatus.OK.reasonPhrase,
                body = body
            )
        }

        @JvmOverloads
        @JvmStatic
        fun <T> ok(
            body: T?,
            includeFields: Set<String> = setOf(),
            excludeFields: Set<String> = setOf()
        ): String {
            if (includeFields.isNotEmpty() && excludeFields.isNotEmpty()) throw RuntimeException("includeFields and excludeFields cannot be used simultaneously")
            val filter by lazy {
                if (includeFields.isNotEmpty()) {
                    // include
                    return@lazy SimpleBeanPropertyFilter.filterOutAllExcept(includeFields)
                }
                if (excludeFields.isNotEmpty()) {
                    // exclude
                    return@lazy SimpleBeanPropertyFilter.serializeAllExcept(excludeFields)
                }
                null
            }
            val objectMapper = ObjectMapper()
            defaultJacksonConfig(objectMapper)
            val httpResult = HttpResult(
                    code = HttpStatus.OK.value(),
                    message = HttpStatus.OK.reasonPhrase,
                    body = body
            )
            return if (filter == null) {
                objectMapper.writeValueAsString(httpResult)
            } else {
                objectMapper.setFilterProvider(SimpleFilterProvider().setDefaultFilter(filter)).writeValueAsString(httpResult)
            }
        }

        @JvmStatic
        @JvmOverloads
        fun <T> fail(
            message: String? = null,
            code: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        ): HttpResult<T> {
            return HttpResult(
                code = code.value(),
                message = message ?: code.reasonPhrase,
                body = null
            )
        }

        @JvmStatic
        @JvmOverloads
        fun <T> fail(
            message: String? = null,
            code: Int
        ): HttpResult<T> {
            return HttpResult(
                code = code,
                message = message,
                body = null
            )
        }

        @JvmStatic
        @JvmOverloads
        fun <T> fail(
            message: String? = null,
            code: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            body: T?
        ): HttpResult<T> {
            @Suppress("UNCHECKED_CAST")
            return HttpResult(
                code = code.value(),
                message = message ?: code.reasonPhrase,
                body = body
            )
        }

        @JvmStatic
        @JvmOverloads
        fun <T> fail(
            message: String? = null,
            code: Int,
            body: T?
        ): HttpResult<T> {
            @Suppress("UNCHECKED_CAST")
            return HttpResult(
                    code = code,
                    message = message,
                    body = body
            )
        }
    }
}

class Test {
    var aaa: String = "aaa"
    var bbb: String = "bbb"
}

fun main() {
    println(HttpResult.ok(Test(), excludeFields = setOf("bbb", "aaa")))
}