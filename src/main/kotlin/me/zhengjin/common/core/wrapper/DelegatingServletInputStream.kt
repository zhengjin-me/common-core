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

import org.springframework.util.Assert
import java.io.IOException
import java.io.InputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream

class DelegatingServletInputStream(sourceStream: InputStream) : ServletInputStream() {
    /**
     * Return the underlying source stream (never `null`).
     */
    private val sourceStream: InputStream
    private var finished = false

    /**
     * Create a DelegatingServletInputStream for the given source stream.
     * @param sourceStream the source stream (never `null`)
     */
    init {
        Assert.notNull(sourceStream, "Source InputStream must not be null")
        this.sourceStream = sourceStream
    }

    @Throws(IOException::class)
    override fun read(): Int {
        val data = sourceStream.read()
        if (data == -1) {
            finished = true
        }
        return data
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return sourceStream.available()
    }

    @Throws(IOException::class)
    override fun close() {
        super.close()
        sourceStream.close()
    }

    override fun isFinished(): Boolean {
        return finished
    }

    override fun isReady(): Boolean {
        return false
    }

    override fun setReadListener(readListener: ReadListener) {
        throw UnsupportedOperationException()
    }
}
