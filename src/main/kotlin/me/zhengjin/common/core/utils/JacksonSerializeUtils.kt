package me.zhengjin.common.core.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import me.zhengjin.common.core.jackson.filter.JacksonJsonCustomizeSerializeFieldsFilter
import java.util.TimeZone
import kotlin.reflect.KClass

object JacksonSerializeUtils {

    @JvmStatic
    fun defaultJacksonConfig(objectMapper: ObjectMapper = ObjectMapper()): ObjectMapper {
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS)
        // 默认为ISO8601格式时间
        objectMapper.dateFormat = StdDateFormat().withTimeZone(TimeZone.getTimeZone("Asia/Shanghai"))
        // 关闭反序列化出现未定义属性时 报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        // 关闭日期序列化为时间戳的功能
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        // 关闭序列化的时候没有为属性找到getter方法 报错
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        // 允许出现特殊字符和转义符
        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
        // 允许出现单引号
        objectMapper.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true)
        return objectMapper
    }

    @JvmStatic
    fun serializeFieldsFilterInclude(data: Any, type: KClass<*>, vararg fields: String): String {
        val objectMapper = defaultJacksonConfig(ObjectMapper())
        objectMapper.setFilterProvider(JacksonJsonCustomizeSerializeFieldsFilter().include(type, *fields))
        objectMapper.addMixIn(type.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
        return objectMapper.writeValueAsString(data)
    }

    @JvmStatic
    fun serializeFieldsFilterExclude(data: Any, type: KClass<*>, vararg fields: String): String {
        val objectMapper = defaultJacksonConfig(ObjectMapper())
        objectMapper.setFilterProvider(JacksonJsonCustomizeSerializeFieldsFilter().exclude(type, *fields))
        objectMapper.addMixIn(type.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
        return objectMapper.writeValueAsString(data)
    }

    @JvmStatic
    fun serializeFieldsFilter(data: Any, type: KClass<*>, includes: List<String> = listOf(), excludes: List<String> = listOf()): String {
        val objectMapper = defaultJacksonConfig(ObjectMapper())
        objectMapper.setFilterProvider(
            JacksonJsonCustomizeSerializeFieldsFilter()
                .include(type, *includes.toTypedArray())
                .exclude(type, *excludes.toTypedArray())
        )
        objectMapper.addMixIn(type.java, JacksonJsonCustomizeSerializeFieldsFilter::class.java)
        return objectMapper.writeValueAsString(data)
    }
}
