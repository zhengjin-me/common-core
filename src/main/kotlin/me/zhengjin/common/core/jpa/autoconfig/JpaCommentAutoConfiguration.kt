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

package me.zhengjin.common.core.jpa.autoconfig

import me.zhengjin.common.core.exception.ServiceException
import me.zhengjin.common.core.jpa.comment.adapter.JpaCommentAdapter
import me.zhengjin.common.core.jpa.comment.adapter.MySQLCommentAdapter
import me.zhengjin.common.core.jpa.comment.adapter.OracleCommentAdapter
import me.zhengjin.common.core.jpa.comment.adapter.PostgreSQLCommentAdapter
import me.zhengjin.common.core.jpa.comment.service.JpaCommentService
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.jdbc.DatabaseDriver
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.JdbcUtils
import org.springframework.jdbc.support.MetaDataAccessException
import java.sql.DatabaseMetaData
import javax.persistence.EntityManager
import javax.sql.DataSource

@AutoConfiguration
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).hasText('\${customize.common.jpa.comment.alterTableNames}')")
@ConditionalOnClass(EntityManager::class)
class JpaCommentAutoConfiguration {

    /**
     * Get the current database type
     */
    private fun getDatabaseName(dataSource: DataSource): String? {
        return try {
            val productName = JdbcUtils.commonDatabaseName(
                JdbcUtils.extractDatabaseMetaData(dataSource) { obj: DatabaseMetaData -> obj.databaseProductName }.toString()
            )
            val databaseDriver = DatabaseDriver.fromProductName(productName)
            check(databaseDriver !== DatabaseDriver.UNKNOWN) { "Unable to detect database type" }
            databaseDriver.id
        } catch (ex: MetaDataAccessException) {
            throw IllegalStateException("Unable to detect database type", ex)
        }
    }

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    fun jpaCommentAdapter(
        dataSource: ObjectProvider<DataSource>,
        jdbcTemplate: JdbcTemplate,
        /**
         * MySQL:   库名
         * Oracle:  模式名
         */
        @Value("\${customize.common.jpa.comment.schema}") configSchema: String? = null
    ): JpaCommentAdapter {
        val ds = dataSource.ifAvailable ?: throw RuntimeException("Can't find dataSource")
        val schema = configSchema ?: ds.connection.catalog ?: throw RuntimeException("Can't find dataSource schema")
        return when (val databaseName = getDatabaseName(ds)) {
            "mysql", "h2" -> MySQLCommentAdapter(schema, jdbcTemplate)
            "postgresql" -> PostgreSQLCommentAdapter(schema, jdbcTemplate)
            "oracle" -> OracleCommentAdapter(schema, jdbcTemplate)
            else -> throw ServiceException("Can't find $databaseName jdbc adapter")
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun jpaCommentService(
        entityManager: EntityManager,
        jpaCommentAdapter: JpaCommentAdapter,
        /**
         * all 所有表
         * 指定表名, 多表名用英文逗号分割
         */
        @Value("\${customize.common.jpa.comment.alterTableNames}") alterTableNames: String? = null
    ): JpaCommentService {
        val jpaCommentService = JpaCommentService(entityManager, jpaCommentAdapter)
        jpaCommentService.init()
        if (!alterTableNames.isNullOrBlank()) {
            when (alterTableNames) {
                "all" -> jpaCommentService.alterAllTableAndColumn()
                else -> {
                    alterTableNames.split(",").forEach {
                        jpaCommentService.alterTableAndColumn(it)
                    }
                }
            }
        }
        return jpaCommentService
    }
}
