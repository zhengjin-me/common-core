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

package me.zhengjin.common.core.wrapper

import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * @version V1.0
 * title: 可重复读取流包装
 * package
 * description:
 * @author fangzhengjin
 * cate 2018-8-14 16:13
 */
class RepeatedlyReadRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    companion object {
        private val logger = LoggerFactory.getLogger(RepeatedlyReadRequestWrapper::class.java)
    }

    private var byteArrayBody: ByteArray? = null

    init {
        var inputStream: InputStream? = null
        try {
            inputStream = request.inputStream
        } catch (e: IOException) {
            logger.error("Error reading the request body", e)
        }

        if (inputStream != null) {
            try {
                inputStream.use {
                    byteArrayBody = it.readBytes()
                }
            } catch (e: IOException) {
                logger.error("Fail to read input stream", e)
            }
        }
    }

    override fun getRequest() = this

    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(byteArrayBody)
        return DelegatingServletInputStream(byteArrayInputStream)
    }
}
