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

package me.zhengjin.common.core.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import me.zhengjin.common.core.jpa.transformer.AliasToLittleCamelCaseMapResultTransformer
import org.hibernate.query.internal.NativeQueryImpl
import org.hibernate.transform.Transformers
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import javax.persistence.EntityManager

/**
 * @author fangzhengjin
 * date 2018-7-13 16:55
 * @version V1.0
 * title: JpaHelper
 * package me.zhengjin.common.core.jpa
 * description:
 */

object JpaHelper {
    private lateinit var entityManager: EntityManager
    private lateinit var jpaQueryFactory: JPAQueryFactory

    fun init(entityManager: EntityManager): JpaHelper {
        JpaHelper.entityManager = entityManager
        JpaHelper.jpaQueryFactory = JPAQueryFactory(entityManager)
        return this
    }

    @JvmStatic
    fun getPageable(page: Int, size: Int): PageRequest {
        return PageRequest.of(if (page <= 1) 0 else page - 1, size)
    }

    @JvmStatic
    fun getEntityManager() = entityManager

    @JvmStatic
    fun getJPAQueryFactory() = jpaQueryFactory

    /**
     *  本地查询 使用页码分页 每行结果封装至Map 结果集key处理为小驼峰
     */
    @JvmOverloads
    @JvmStatic
    fun nativeQueryTransformLittleCamelCaseMapResultToPage(
        querySql: String,
        page: Int,
        size: Int,
        countSql: String? = null
    ): Page<Any> {
        return nativeQueryTransformLittleCamelCaseMapResultToPage(querySql, getPageable(page, size), countSql)
    }

    /**
     * 本地查询 使用Pageable分页 每行结果封装至Map 结果集key处理为小驼峰
     */
    @JvmOverloads
    @JvmStatic
    fun nativeQueryTransformLittleCamelCaseMapResultToPage(
        querySql: String,
        pageable: Pageable,
        countSql: String? = null
    ): Page<Any> {
        val query = entityManager.createNativeQuery(querySql)
        query.firstResult = pageable.offset.toInt()
        query.maxResults = pageable.pageSize
        query.unwrap(NativeQueryImpl::class.java)
            .setResultTransformer(AliasToLittleCamelCaseMapResultTransformer.INSTANCE)
        val countQuery = nativeQueryNoTransformSingleObjectResult(
            countSql
                ?: "SELECT count(0) from ($querySql) as page"
        )
        return PageImpl<Any>(query.resultList, pageable, countQuery.toString().toLong())
    }

    /**
     *  本地查询 使用页码分页 每行结果封装至指定对象
     */
    @JvmOverloads
    @JvmStatic
    fun <T : Any> nativeQueryTransformCustomizeEntityResultToPage(
        querySql: String,
        page: Int,
        size: Int,
        target: Class<T>,
        countSql: String? = null
    ): Page<T> {
        return nativeQueryTransformCustomizeEntityResultToPage(querySql, getPageable(page, size), target, countSql)
    }

    /**
     * 本地查询 使用Pageable分页 每行结果封装至指定对象
     */
    @JvmOverloads
    @JvmStatic
    fun <T : Any> nativeQueryTransformCustomizeEntityResultToPage(
        querySql: String,
        pageable: Pageable,
        target: Class<T>,
        countSql: String? = null
    ): Page<T> {
        val query = entityManager.createNativeQuery(querySql)
        query.firstResult = pageable.offset.toInt()
        query.maxResults = pageable.pageSize
        query.unwrap(NativeQueryImpl::class.java)
            .setResultTransformer(Transformers.aliasToBean(target))
        val countQuery = nativeQueryNoTransformSingleObjectResult(
            countSql
                ?: "SELECT count(0) from ($querySql) as page"
        )
        @Suppress("UNCHECKED_CAST")
        return PageImpl(query.resultList as MutableList<T>, pageable, countQuery.toString().toLong())
    }

    /**
     * 本地查询 每行结果封装至Map 结果集key处理为小驼峰
     */
    @JvmStatic
    fun nativeQueryTransformLittleCamelCaseMapResult(
        querySql: String
    ): MutableList<MutableMap<String, Any>> {
        val query = entityManager.createNativeQuery(querySql)
        query.unwrap(NativeQueryImpl::class.java)
            .setResultTransformer(AliasToLittleCamelCaseMapResultTransformer.INSTANCE)
        @Suppress("UNCHECKED_CAST")
        return query.resultList as MutableList<MutableMap<String, Any>>
    }

    /**
     * 本地查询 每行结果封装至Map 原生样式 不作处理
     */
    @JvmStatic
    fun nativeQueryNoTransformMapResultToList(
        querySql: String
    ): MutableList<Any?> {
        val query = entityManager.createNativeQuery(querySql)
        query.unwrap(NativeQueryImpl::class.java).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
        return query.resultList
    }

    /**
     * 本地查询 每行结果封装至指定对象
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <T : Any> nativeQueryTransformCustomizeEntityResultToList(
        querySql: String,
        target: Class<T>
    ): MutableList<T> {
        val query = entityManager.createNativeQuery(querySql)
        query.unwrap(NativeQueryImpl::class.java).setResultTransformer(Transformers.aliasToBean(target))
        @Suppress("UNCHECKED_CAST")
        return query.resultList as MutableList<T>
    }

    /**
     * 本地查询 返回原生List
     */
    @JvmStatic
    fun nativeQueryNoTransformObjectToList(
        querySql: String
    ): MutableList<Any?> =
        entityManager.createNativeQuery(querySql).resultList

    /**
     * 本地查询 只返回第一个结果集
     */
    @JvmStatic
    fun nativeQueryNoTransformSingleObjectResult(
        querySql: String
    ): Any? =
        entityManager.createNativeQuery(querySql).singleResult

    /**
     * 执行本地SQL
     */
    @JvmStatic
    fun executeNativeQuery(
        executeSql: String
    ) = entityManager.createNativeQuery(executeSql).executeUpdate()

    /**
     * 执行本地SQL, 如果更新0行则抛出异常
     */
    @JvmStatic
    fun executeNativeQueryOrThrow(
        executeSql: String,
        lazyMessage: () -> String? = { null }
    ) {
        val result = executeNativeQuery(executeSql)
        val message = lazyMessage() ?: "执行失败, 影响[$result]行"
        require(result >= 1) { message }
    }

    @JvmStatic
    fun flush() = entityManager.flush()
}
