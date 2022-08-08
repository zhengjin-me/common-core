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

package me.zhengjin.common.core.jackson.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import me.zhengjin.common.core.jackson.handler.JacksonJsonCustomizeSerializeAnnotationHandler
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import javax.annotation.PostConstruct

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@AutoConfiguration
@ConditionalOnBean(RequestMappingHandlerAdapter::class)
class JacksonJsonCustomizeSerializeAnnotationHandlerAutoConfiguration(
    private val requestMappingHandlerAdapter: RequestMappingHandlerAdapter,
    private val objectMapper: ObjectMapper?
) {

    @PostConstruct
    fun register() {
        requestMappingHandlerAdapter.returnValueHandlers?.let { returnValueHandlers ->
            val newReturnValueHandlers = ArrayList<HandlerMethodReturnValueHandler>(returnValueHandlers.size + 1)
            newReturnValueHandlers.add(0, JacksonJsonCustomizeSerializeAnnotationHandler(objectMapper))
            newReturnValueHandlers.addAll(returnValueHandlers)
            requestMappingHandlerAdapter.returnValueHandlers = newReturnValueHandlers
        }
    }
}
