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

package me.zhengjin.common.core.encryptor.resolver

import cn.hutool.core.lang.ParameterizedTypeImpl
import me.zhengjin.common.core.encryptor.annotation.IdDecrypt
import me.zhengjin.common.core.exception.ServiceException
import me.zhengjin.common.core.utils.IdEncryptionUtils
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor
import java.lang.reflect.Type

class CustomizeMethodParameter(
    parameter: MethodParameter,
    private val rawClass: Class<*>,
    private vararg val typeArguments: Type,
) : MethodParameter(parameter) {
    override fun getNestedGenericParameterType(): Type {
        return ParameterizedTypeImpl(typeArguments, null, rawClass)
    }
}

/**
 * id自动解密,仅用于请求参数处理,需搭配spring原生注解使用
 * 支持: RequestBody/RequestParam/PathVariable/RequestHeader注解
 * @author fangzhengjin
 * @create 2022-10-09 16:00
 **/

class IdDecryptProcessResolver(
    private val adapter: RequestMappingHandlerAdapter
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(IdDecrypt::class.java)
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any {
        val resolver: HandlerMethodArgumentResolver = if (parameter.hasParameterAnnotation(RequestBody::class.java)) {
            adapter.argumentResolvers!!.filterIsInstance<RequestResponseBodyMethodProcessor>().first()
        } else if (parameter.hasParameterAnnotation(RequestParam::class.java)) {
            adapter.argumentResolvers!!.filterIsInstance<RequestParamMethodArgumentResolver>().first()
        } else if (parameter.hasParameterAnnotation(PathVariable::class.java)) {
            adapter.argumentResolvers!!.filterIsInstance<PathVariableMethodArgumentResolver>().first()
        } else if (parameter.hasParameterAnnotation(RequestHeader::class.java)) {
            adapter.argumentResolvers!!.filterIsInstance<RequestHeaderMethodArgumentResolver>().first()
        } else throw ServiceException("未找到可处理的resolver")
        when (val typeName = parameter.nestedGenericParameterType.typeName) {
            "java.lang.Long", "java.lang.String" -> {
                val arg = resolver.resolveArgument(CustomizeMethodParameter(parameter, String::class.java), mavContainer, webRequest, binderFactory)
                val result = IdEncryptionUtils.decrypt(arg as String)
                if (typeName === "java.lang.Long") {
                    return result
                }
                return result.toString()
            }

            "java.util.List<java.lang.Long>", "java.util.List<java.lang.String>" -> {
                val arg = resolver.resolveArgument(CustomizeMethodParameter(parameter, List::class.java, String::class.java), mavContainer, webRequest, binderFactory)
                val result = IdEncryptionUtils.decryptIds(arg as List<String>)
                if (typeName == "java.util.List<java.lang.Long>") {
                    return result
                }
                return result.map { it.toString() }.toList()
            }

            else -> throw ServiceException("不支持自动处理的类型")
        }
    }
}
