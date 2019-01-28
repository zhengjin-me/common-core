package com.github.fangzhengjin.common.core.converter

import com.alibaba.fastjson.JSON
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.support.converter.*
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class RabbitMqFastJsonClassMapper : DefaultClassMapper() {
    /**
     * 构造函数初始化信任所有pakcage
     */
    init {
        setTrustedPackages("*")
    }
}

class RabbitMqFastJsonConverter : AbstractMessageConverter() {
    companion object {
        /**
         * 日志对象实例
         */
        @JvmStatic
        private val logger = LoggerFactory.getLogger(RabbitMqFastJsonConverter::class.java)
        /**
         * 默认字符集
         */
        private const val DEFAULT_CHART_SET = "UTF-8"
        /**
         * 消息类型映射对象
         */
        @JvmStatic
        private val classMapper = RabbitMqFastJsonClassMapper()
        @JvmStatic
        private val jackson2JsonMessageConverter = Jackson2JsonMessageConverter()
        private val javaTypeMapper: Jackson2JavaTypeMapper = DefaultJackson2JavaTypeMapper()
    }

    /**
     * 创建消息
     *
     * @param o                 消息对象
     * @param messageProperties 消息属性
     * @return
     */
    override fun createMessage(o: Any, messageProperties: MessageProperties): Message {
        val bytes: ByteArray?
        try {
            val jsonString = JSON.toJSONString(o)
            bytes = jsonString.toByteArray(Charset.forName(DEFAULT_CHART_SET))
        } catch (e: IOException) {
            throw MessageConversionException("Failed to convert Message content", e)
        }

        messageProperties.contentType = MessageProperties.CONTENT_TYPE_JSON
        messageProperties.contentEncoding = DEFAULT_CHART_SET
        messageProperties.contentLength = bytes.size.toLong()
        classMapper.fromClass(o.javaClass, messageProperties)
        return Message(bytes, messageProperties)
    }

    /**
     * 转换消息为对象
     *
     * @param message 消息对象
     * @return
     * @throws MessageConversionException
     */
    @Throws(MessageConversionException::class)
    override fun fromMessage(message: Message): Any {
        val properties = message.messageProperties
        if (properties != null) {
            val contentType = properties.contentType
            var encoding: String? = properties.contentEncoding
            if (encoding == null) {
                encoding = DEFAULT_CHART_SET
            }
            return if (contentType != null && contentType.contains("json")) {
                try {
                    val targetClass = classMapper.toClass(message.messageProperties)
                    convertBytesToObject(message.body, encoding, targetClass)
                } catch (e: IOException) {
                    throw MessageConversionException("Failed to convert Message content", e)
                }
            } else {
//                val fromMessage = jackson2JsonMessageConverter.fromMessage(message)
//                fromMessage
                String(message.body, Charset.forName(encoding))
                //                logger.warn("Could not convert incoming message with content-type [$contentType]")
            }
        }
        return ""
    }

    /**
     * 将字节数组转换成实例对象
     *
     * @param body     Message对象主体字节数组
     * @param encoding 字符集
     * @param clazz    类型
     * @return
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    private fun convertBytesToObject(body: ByteArray, encoding: String, clazz: Class<*>): Any {
        val contentAsString = String(body, Charset.forName(encoding))
        return JSON.parseObject(contentAsString, clazz)
    }
}