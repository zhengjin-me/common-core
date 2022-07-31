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
