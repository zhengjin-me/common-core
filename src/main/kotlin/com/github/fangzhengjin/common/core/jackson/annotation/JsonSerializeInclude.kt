package com.github.fangzhengjin.common.core.jackson.annotation

import kotlin.reflect.KClass

/**
 * 仅序列化列出的字段
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonSerializeInclude(
        val value: Array<String>,
        val type: KClass<*>
)

/**
 * 仅序列化列出的字段
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonSerializeIncludes(
        val value: Array<JsonSerializeInclude>
)
