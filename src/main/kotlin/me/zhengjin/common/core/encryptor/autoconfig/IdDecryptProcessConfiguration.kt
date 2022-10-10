package me.zhengjin.common.core.encryptor.autoconfig

import me.zhengjin.common.core.encryptor.resolver.IdDecryptProcessResolver
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import java.util.Collections

/**
 *
 * @author fangzhengjin
 * @create 2022-10-10 10:28
 **/
@AutoConfiguration
class IdDecryptProcessConfiguration : BeanPostProcessor {
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        // 初始化之前不改变，原bean返回
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (bean !is RequestMappingHandlerAdapter) {
            return bean
        }
        val currentResolvers = bean.argumentResolvers ?: throw IllegalStateException(String.format("No HandlerMethodArgumentResolvers found in RequestMappingHandlerAdapter %s", beanName))
        val newResolvers = ArrayList<HandlerMethodArgumentResolver>(currentResolvers.size + 1)
        newResolvers.addAll(currentResolvers)
        newResolvers.add(0, IdDecryptProcessResolver(bean))
        bean.argumentResolvers = Collections.unmodifiableList(newResolvers)
        return bean
    }
}
