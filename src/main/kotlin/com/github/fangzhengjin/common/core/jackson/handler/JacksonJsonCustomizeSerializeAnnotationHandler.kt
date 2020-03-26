package com.github.fangzhengjin.common.core.jackson.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fangzhengjin.common.core.jackson.annotation.JsonSerializeExclude
import com.github.fangzhengjin.common.core.jackson.annotation.JsonSerializeExcludes
import com.github.fangzhengjin.common.core.jackson.annotation.JsonSerializeInclude
import com.github.fangzhengjin.common.core.jackson.annotation.JsonSerializeIncludes
import com.github.fangzhengjin.common.core.jackson.filter.JacksonJsonCustomizeSerializeFieldsFilter
import com.github.fangzhengjin.common.core.jackson.utils.JacksonSerializeUtils
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
                    ObjectMapper()
                            .setConfig(defaultObjectMapper.deserializationConfig)
                            .setConfig(defaultObjectMapper.serializationConfig)
                } else {
                    JacksonSerializeUtils.defaultJacksonConfig(ObjectMapper())
                }

        val jacksonJsonCustomizeSerializeFieldsFilter = JacksonJsonCustomizeSerializeFieldsFilter()
        methodAnnotations.forEach {
            when (it) {
                is JsonSerializeInclude -> {
                    if (it.value.isNotEmpty()) {
                        jacksonJsonCustomizeSerializeFieldsFilter.include(it.type, *it.value)
                        objectMapper.addMixIn(it.type::class.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
                    }
                }
                is JsonSerializeIncludes -> {
                    if (it.value.isNotEmpty()) {
                        it.value.forEach { include ->
                            if (include.value.isNotEmpty()) {
                                jacksonJsonCustomizeSerializeFieldsFilter.include(include.type, *include.value)
                                objectMapper.addMixIn(include.type::class.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
                            }
                        }
                    }
                }
                is JsonSerializeExclude -> {
                    if (it.value.isNotEmpty()) {
                        jacksonJsonCustomizeSerializeFieldsFilter.exclude(it.type, *it.value)
                        objectMapper.addMixIn(it.type::class.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
                    }
                }
                is JsonSerializeExcludes -> {
                    if (it.value.isNotEmpty()) {
                        it.value.forEach { exclude ->
                            if (exclude.value.isNotEmpty()) {
                                jacksonJsonCustomizeSerializeFieldsFilter.exclude(exclude.type, *exclude.value)
                                objectMapper.addMixIn(exclude.type::class.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
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