package me.zhengjin.common.core.jpa.autoconfig

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "customize.common.jpa.comment")
class JpaCommentProperties {
    var enable: Boolean = false

    /**
     * MySQL:   库名
     * Oracle:  模式名
     */
    var schema: String? = null

    /**
     * all 所有表
     * 指定表名, 多表名用英文逗号分割
     */
    var alterTableNames: String? = null
}
