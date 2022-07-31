package me.zhengjin.common.core.jpa.autoconfig

import me.zhengjin.common.core.jpa.JpaHelper
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.persistence.EntityManager

@AutoConfiguration
@EnableJpaAuditing
@ConditionalOnClass(EntityManager::class)
class JpaHelperAutoConfiguration {

    /**
     * 当项目使用JPA时，如果Spring容器中不存在JpaHelper则自动创建
     */
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean(JpaHelper::class)
    fun jpaHelper(entityManager: EntityManager): JpaHelper {
        return JpaHelper.init(entityManager)
    }
}
