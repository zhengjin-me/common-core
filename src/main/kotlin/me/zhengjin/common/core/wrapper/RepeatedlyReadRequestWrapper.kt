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
