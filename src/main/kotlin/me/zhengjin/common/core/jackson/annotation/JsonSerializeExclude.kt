package me.zhengjin.common.core.jackson.annotation

import kotlin.reflect.KClass

/**
 * 排除列出的字段 然后序列化
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonSerializeExclude(
    val type: KClass<*>,
    val value: Array<String>
)

/**
 * 排除列出的字段 然后序列化
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonSerializeExcludes(
    val value: Array<JsonSerializeExclude>
)
