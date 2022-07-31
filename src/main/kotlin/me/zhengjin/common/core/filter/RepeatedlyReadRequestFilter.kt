package me.zhengjin.common.core.filter

import me.zhengjin.common.core.wrapper.RepeatedlyReadRequestWrapper
import org.slf4j.LoggerFactory
import java.io.IOException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * @version V1.0
 * title: 可重复读取流处理
 * package
 * description:
 * @author fangzhengjin
 * cate 2018-8-14 15:56
 */
class RepeatedlyReadRequestFilter : Filter {
    companion object {
        private val logger = LoggerFactory.getLogger(RepeatedlyReadRequestFilter::class.java)
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        var repeatedlyRequest = request
        if (repeatedlyRequest is HttpServletRequest) {
            repeatedlyRequest = RepeatedlyReadRequestWrapper(repeatedlyRequest)
            chain.doFilter(repeatedlyRequest, response)
        } else {
            chain.doFilter(request, response)
        }
    }
}
