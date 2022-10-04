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

package me.zhengjin.common.core.utils

import cn.hutool.crypto.symmetric.AES
import me.zhengjin.common.core.exception.ServiceException
import org.slf4j.LoggerFactory

/**
 *
 * @author fangzhengjin
 * @create 2022-10-05 00:19
 **/
object IdEncryptionUtils {
    private val logger = LoggerFactory.getLogger(IdEncryptionUtils::class.java)
    private lateinit var aes: AES

    fun init(idEncryptKey: String): IdEncryptionUtils {
        if (idEncryptKey.length != 16) {
            throw ServiceException("密钥长度必须为16位")
        }
        aes = AES(idEncryptKey.toByteArray())
        return this
    }

    fun encrypt(id: Long): String {
        return aes.encryptBase64(id.toString())
    }

    fun decrypt(id: String): Long {
        return aes.decryptStr(id).toLong()
    }

    fun encryptIgnoreError(id: Long): String? {
        return try {
            encrypt(id)
        } catch (ignore: Exception) {
            logger.error("id加密失败", ignore)
            null
        }
    }

    fun decryptIgnoreError(id: String): Long? {
        return try {
            decrypt(id)
        } catch (ignore: Exception) {
            logger.error("id解密失败", ignore)
            null
        }
    }

    fun decryptIds(ids: List<String>): List<Long> {
        return ids.map { decrypt(it) }
    }
}
