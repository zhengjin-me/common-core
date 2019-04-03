package com.github.fangzhengjin.common.core.entity

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter
import org.springframework.http.HttpStatus
import java.io.Serializable

/**
 * @version V1.0
 * @title: HttpResult
 * @package com.github.fangzhengjin.common.core.entity
 * @description: 响应数据标准类
 * @author fangzhengjin
 * @date 2019/1/28 14:52
 */
class HttpResult<T> @JvmOverloads constructor(
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
        return JSON.toJSONString(this)
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun <T> ok(message: String? = null): HttpResult<T> {
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
            clazz: Class<T>? = null,
            includeFields: MutableList<String> = mutableListOf(),
            excludeFields: MutableList<String> = mutableListOf()
        ): String {
            val propertyPreFilter = SimplePropertyPreFilter(clazz)
            propertyPreFilter.includes.addAll(includeFields)
            propertyPreFilter.excludes.addAll(excludeFields)

            return if (body != null) {
                JSON.toJSONString(
                    HttpResult(
                        code = HttpStatus.OK.value(),
                        message = HttpStatus.OK.reasonPhrase,
                        body = body
                    ), propertyPreFilter
                )
            } else {
                ""
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
