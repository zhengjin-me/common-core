package me.zhengjin.common.core.jpa.comment.annotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JpaComment(
    /**
     * 表/字段注释
     *
     * @return String
     */
    val value: String
)
