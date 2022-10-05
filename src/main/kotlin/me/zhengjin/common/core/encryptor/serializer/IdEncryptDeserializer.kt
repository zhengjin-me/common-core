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
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import me.zhengjin.common.core.encryptor.annotation.IdEncryptor
import me.zhengjin.common.core.utils.IdEncryptionUtils

/**
 *
 * @author fangzhengjin
 * @create 2022-10-06 00:00
 **/
class IdEncryptDeserializer(
    private val annotation: IdEncryptor? = null
) : JsonDeserializer<Long>(), ContextualDeserializer {

    override fun createContextual(ctxt: DeserializationContext?, property: BeanProperty?): JsonDeserializer<*> {
        val annotation = property?.getAnnotation(IdEncryptor::class.java)
        return IdEncryptDeserializer(annotation)
    }

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Long? {
        if (annotation?.deserialize == true) {
            return if (p?.valueAsString.isNullOrBlank()) {
                null
            } else {
                IdEncryptionUtils.decrypt(p!!.valueAsString)
            }
        }
        return p?.longValue
    }
}
