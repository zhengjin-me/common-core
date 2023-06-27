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

package me.zhengjin.common.core.encryptor.serializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import me.zhengjin.common.core.exception.ServiceException
import me.zhengjin.common.core.utils.IdEncryptionUtils
import org.springframework.util.ReflectionUtils

/**
 *
 * @author fangzhengjin
 * @create 2022-10-06 00:00
 **/
class IdDecryptDeserializer : JsonDeserializer<Any>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Any? {
        var currentValue = p.currentValue ?: p.parsingContext.parent.currentValue
        if (currentValue == null && p.hasToken(JsonToken.VALUE_STRING)) {
            return IdEncryptionUtils.decrypt(p.valueAsString)
        }
        val clazz = currentValue.javaClass
        val declaredField = ReflectionUtils.findField(clazz, p.currentName)
            ?: throw ServiceException("未在[${clazz.name}]中找到属性[${p.currentName}]")
        return when (declaredField.genericType.typeName) {
            "java.lang.Long", "long" -> IdEncryptionUtils.decrypt(p.valueAsString)
            "java.lang.String", "string" -> IdEncryptionUtils.decryptStr(p.valueAsString)
            "java.util.List<java.lang.Long>" -> {
                val data: List<String> = p.readValueAs(object : TypeReference<List<String>>() {})
                IdEncryptionUtils.decryptIds(data)
            }

            "java.util.List<java.lang.String>" -> {
                val data: List<String> = p.readValueAs(object : TypeReference<List<String>>() {})
                IdEncryptionUtils.decryptIds(data).map { it.toString() }.toList()
            }

            else -> throw ServiceException("不支持反序列化的类型")
        }
    }
}
