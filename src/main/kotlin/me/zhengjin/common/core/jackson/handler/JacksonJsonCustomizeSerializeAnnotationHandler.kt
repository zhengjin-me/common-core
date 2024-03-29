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

package me.zhengjin.common.core.jackson.handler

import com.fasterxml.jackson.databind.ObjectMapper
import me.zhengjin.common.core.jackson.annotation.JsonSerializeExclude
import me.zhengjin.common.core.jackson.annotation.JsonSerializeExcludes
import me.zhengjin.common.core.jackson.annotation.JsonSerializeInclude
import me.zhengjin.common.core.jackson.annotation.JsonSerializeIncludes
import me.zhengjin.common.core.jackson.filter.JacksonJsonCustomizeSerializeFieldsFilter
import me.zhengjin.common.core.utils.JacksonSerializeUtils
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.method.support.ModelAndViewContainer
import javax.servlet.http.HttpServletResponse

/**
 * 对于使用自定义Jackson注解对spring返回值进行处理
 */
class JacksonJsonCustomizeSerializeAnnotationHandler(
    private val defaultObjectMapper: ObjectMapper?
) : HandlerMethodReturnValueHandler {
    override fun supportsReturnType(returnType: MethodParameter): Boolean {
        // 如果有我们自定义的 JSON 注解 就用我们这个Handler 来处理
        return returnType.hasMethodAnnotation(JsonSerializeInclude::class.java) ||
            returnType.hasMethodAnnotation(JsonSerializeIncludes::class.java) ||
            returnType.hasMethodAnnotation(JsonSerializeExclude::class.java) ||
            returnType.hasMethodAnnotation(JsonSerializeExcludes::class.java)
    }

    override fun handleReturnValue(returnValue: Any?, returnType: MethodParameter, mavContainer: ModelAndViewContainer, webRequest: NativeWebRequest) {
        // 设置这个就是最终的处理类了，处理完不再去找下一个类进行处理
        mavContainer.isRequestHandled = true
        // 获得注解并执行filter方法 最后返回
        val nativeResponse = webRequest.getNativeResponse(HttpServletResponse::class.java)!!
        val methodAnnotations = returnType.methodAnnotations
        nativeResponse.contentType = MediaType.APPLICATION_JSON_UTF8_VALUE

        val objectMapper =
            if (defaultObjectMapper != null) {
                // 如果项目中已经定义了 就使用项目里的参数创建一个新的
                defaultObjectMapper.copy()
            } else {
                JacksonSerializeUtils.defaultJacksonConfig(ObjectMapper())
            }

        val jacksonJsonCustomizeSerializeFieldsFilter = JacksonJsonCustomizeSerializeFieldsFilter()
        methodAnnotations.forEach {
            when (it) {
                is JsonSerializeInclude -> {
                    if (it.value.isNotEmpty()) {
                        jacksonJsonCustomizeSerializeFieldsFilter.include(it.type, *it.value)
                        objectMapper.addMixIn(it.type.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
                    }
                }
                is JsonSerializeIncludes -> {
                    if (it.value.isNotEmpty()) {
                        it.value.forEach { include ->
                            if (include.value.isNotEmpty()) {
                                jacksonJsonCustomizeSerializeFieldsFilter.include(include.type, *include.value)
                                objectMapper.addMixIn(include.type.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
                            }
                        }
                    }
                }
                is JsonSerializeExclude -> {
                    if (it.value.isNotEmpty()) {
                        jacksonJsonCustomizeSerializeFieldsFilter.exclude(it.type, *it.value)
                        objectMapper.addMixIn(it.type.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
                    }
                }
                is JsonSerializeExcludes -> {
                    if (it.value.isNotEmpty()) {
                        it.value.forEach { exclude ->
                            if (exclude.value.isNotEmpty()) {
                                jacksonJsonCustomizeSerializeFieldsFilter.exclude(exclude.type, *exclude.value)
                                objectMapper.addMixIn(exclude.type.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
                            }
                        }
                    }
                }
            }
        }
        objectMapper.setFilterProvider(jacksonJsonCustomizeSerializeFieldsFilter)
        nativeResponse.writer.write(objectMapper.writeValueAsString(returnValue))
    }
}
