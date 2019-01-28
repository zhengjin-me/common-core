package com.github.fangzhengjin.common.core.entity

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import java.io.Serializable

/**
 * @version V1.0
 * @title: Result
 * @package com.github.fangzhengjin.common.core.entity
 * @description: 响应数据标准类
 * @author fangzhengjin
 * @date 2019/1/28 14:52
 */
class Result<T> @JvmOverloads constructor(
        var code: Int? = 200,
        var message: String? = "",
        @Suppress("UNCHECKED_CAST")
        var body: T? = "" as T
) : Serializable {

    @ApiModel("分页数据集")
    class PageResult<T>(
            @ApiModelProperty("查询内容")
            val content: List<T>,
            @ApiModelProperty("总记录数")
            val totalElements: Long,
            @ApiModelProperty("总页数")
            val totalPages: Int,
            @ApiModelProperty("当前页码")
            val currentPage: Int
    )

    @ApiModel("JQGrid分页数据集")
    class JQGrid<T>(
            @ApiModelProperty("查询内容")
            val rows: List<T>,
            @ApiModelProperty("总记录数")
            val records: Long,
            @ApiModelProperty("总页数")
            val total: Int,
            @ApiModelProperty("当前页码")
            val page: Int
    )

    val isOk: Boolean
        get() {
            return HttpStatus.OK.value() == this.code
        }

    override fun toString(): String {
        return JSON.toJSONString(this)
    }

    companion object {

        @JvmStatic
        fun <T> jqGrid(page: Page<T>): JQGrid<T> {
            return JQGrid(
                    rows = page.content,
                    records = page.totalElements,
                    total = page.totalPages,
                    page = page.number + 1
            )
        }

        @JvmStatic
        fun <T> jqGrid(page: Page<T>, clazz: Class<T>, fields: List<String>): String {
            return JSON.toJSONString(
                JQGrid(
                    rows = page.content,
                    records = page.totalElements,
                    total = page.totalPages,
                    page = page.number + 1
            ), SimplePropertyPreFilter(clazz, *fields.toTypedArray()))
        }

        @JvmStatic
        @JvmOverloads
        fun <T> ok(message: String? = null): Result<T> {
            return Result(
                    code = HttpStatus.OK.value(),
                    message = message ?: HttpStatus.OK.reasonPhrase,
                    body = null
            )
        }

        @JvmStatic
        @JvmOverloads
        fun <T> ok(body: T?, message: String? = null): Result<T> {
            return Result(
                    code = HttpStatus.OK.value(),
                    message = message ?: HttpStatus.OK.reasonPhrase,
                    body = body
            )
        }

        @JvmStatic
        fun <T> ok(body: T?, clazz: Class<T>, fields: List<String>): String {
            return if (body != null) {
                JSON.toJSONString(
                    Result(
                        code = HttpStatus.OK.value(),
                        message = HttpStatus.OK.reasonPhrase,
                        body = body
                ), SimplePropertyPreFilter(clazz, *fields.toTypedArray()))
            } else {
                ""
            }
        }

        @JvmStatic
        @JvmOverloads
        fun <T> ok(page: Page<T>, message: String? = null): Result<PageResult<T>> {
            return Result(
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

        @JvmStatic
        @JvmOverloads
        fun <T> fail(
                message: String? = null,
                code: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        ): Result<T> {
            return Result(
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
        ): Result<T> {
            return Result(
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
        ): Result<T> {
            @Suppress("UNCHECKED_CAST")
            return Result(
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
        ): Result<T> {
            @Suppress("UNCHECKED_CAST")
            return Result(
                    code = code,
                    message = message,
                    body = body
            )
        }
    }
}
