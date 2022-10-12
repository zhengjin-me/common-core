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

package me.zhengjin.common.core.resolver.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import me.zhengjin.common.core.encryptor.handler.SkipIdEncryptAnnotationHandler
import me.zhengjin.common.core.encryptor.resolver.IdDecryptProcessResolver
import me.zhengjin.common.core.jackson.handler.JacksonJsonCustomizeSerializeAnnotationHandler
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

/**
 *
 * @author fangzhengjin
 * @create 2022-10-10 10:28
 **/
@AutoConfiguration
class CustomizeBeanPostProcessConfiguration(
    private val objectMapper: ObjectMapper?
) : BeanPostProcessor {
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        // 初始化之前不改变，原bean返回
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (bean !is RequestMappingHandlerAdapter) {
            return bean
        }

        bean.argumentResolvers?.let { argumentResolvers ->
            val newArgumentResolvers = ArrayList<HandlerMethodArgumentResolver>(argumentResolvers.size + 1)
            newArgumentResolvers.add(0, IdDecryptProcessResolver(bean))
            newArgumentResolvers.addAll(argumentResolvers)
            bean.argumentResolvers = newArgumentResolvers
        }

        bean.returnValueHandlers?.let { returnValueHandlers ->
            val newReturnValueHandlers = ArrayList<HandlerMethodReturnValueHandler>(returnValueHandlers.size + 2)
            newReturnValueHandlers.add(0, SkipIdEncryptAnnotationHandler())
            newReturnValueHandlers.add(1, JacksonJsonCustomizeSerializeAnnotationHandler(objectMapper))
            newReturnValueHandlers.addAll(returnValueHandlers)
            bean.returnValueHandlers = newReturnValueHandlers
        }
        return bean
    }
}
