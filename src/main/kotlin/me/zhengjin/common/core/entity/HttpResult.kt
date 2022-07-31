package me.zhengjin.common.core.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import me.zhengjin.common.core.exception.ServiceException
import me.zhengjin.common.core.utils.JacksonSerializeUtils
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * @version V1.0
 * @title: HttpResult
 * @package me.zhengjin.common.core.entity
 * @description: 响应数据标准类
 * @author fangzhengjin
 * @date 2019/1/28 14:52
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class HttpResult<T>(
    var code: Int? = 200,
    var message: String? = "",
    var body: T? = null
) : Serializable {

    val isOk: Boolean
        get() {
            return HttpStatus.OK.value() == this.code
        }

    override fun toString(): String {
        val objectMapper = JacksonSerializeUtils.defaultJacksonConfig(ObjectMapper())
        return objectMapper.writeValueAsString(this)
    }

    companion object {

        @JvmStatic
        private fun conditionSerialize(
            obj: Any,
            clazz: KClass<*>,
            includeFields: Set<String> = setOf(),
            excludeFields: Set<String> = setOf()
        ): String {
            if (includeFields.isNotEmpty() || excludeFields.isNotEmpty()) {
                if (includeFields.isNotEmpty() && excludeFields.isNotEmpty()) {
                    throw ServiceException("you cannot set includeFields and excludeFields at the same time")
                }
                return JacksonSerializeUtils.serializeFieldsFilter(obj, clazz, includeFields.toList(), excludeFields.toList())
            }
            throw ServiceException("includeFields or excludeFields list not found")
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
        fun <T> ok(body: T, message: String? = null): HttpResult<T> {
            return HttpResult(
                code = HttpStatus.OK.value(),
                message = message ?: HttpStatus.OK.reasonPhrase,
                body = body
            )
        }

        @JvmOverloads
        @JvmStatic
        fun <T : Any> ok(
            body: T,
            clazz: KClass<*>,
            includeFields: Set<String> = setOf(),
            excludeFields: Set<String> = setOf()
        ): String {
            val httpResult = HttpResult(
                code = HttpStatus.OK.value(),
                message = HttpStatus.OK.reasonPhrase,
                body = body
            )
            return conditionSerialize(httpResult, clazz, includeFields, excludeFields)
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
            return HttpResult(
                code = code,
                message = message,
                body = body
            )
        }

        @JvmOverloads
        @JvmStatic
        fun <T> page(page: Page<T>, message: String? = null): HttpResult<PageResult<T>> {
            return HttpResult(
                code = HttpStatus.OK.value(),
                message = message ?: HttpStatus.OK.reasonPhrase,
                body = PageResult(
                    content = page.content,
                    totalElements = page.totalElements,
                    totalPages = page.totalPages,
                    currentPage = page.number + 1
                )
            )
        }

        @JvmOverloads
        @JvmStatic
        fun <T> page(page: Page<T>, clazz: KClass<*>, includeFields: Set<String> = setOf(), excludeFields: Set<String> = setOf()): String {
            return conditionSerialize(page(page), clazz, includeFields, excludeFields)
        }
    }
}
