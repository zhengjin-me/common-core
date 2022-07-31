package me.zhengjin.common.core.jpa.autoconfig

import me.zhengjin.common.core.exception.ServiceException
import me.zhengjin.common.core.jpa.comment.adapter.JpaCommentAdapter
import me.zhengjin.common.core.jpa.comment.adapter.MySQLCommentAdapter
import me.zhengjin.common.core.jpa.comment.adapter.OracleCommentAdapter
import me.zhengjin.common.core.jpa.comment.adapter.PostgreSQLCommentAdapter
import me.zhengjin.common.core.jpa.comment.service.JpaCommentService
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.jdbc.DatabaseDriver
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.JdbcUtils
import org.springframework.jdbc.support.MetaDataAccessException
import java.sql.DatabaseMetaData
import javax.persistence.EntityManager
import javax.sql.DataSource

@AutoConfiguration
@EnableConfigurationProperties(JpaCommentProperties::class)
@ConditionalOnProperty(prefix = "customize.common.jpa.comment", name = ["enable"], havingValue = "true")
@ConditionalOnClass(EntityManager::class)
class JpaCommentAutoConfiguration(
    private val jpaCommentProperties: JpaCommentProperties
) {

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
    fun jpaCommentAdapter(dataSource: ObjectProvider<DataSource>, jdbcTemplate: JdbcTemplate): JpaCommentAdapter {
        val ds = dataSource.ifAvailable ?: throw RuntimeException("Can't find dataSource")
        val schema = jpaCommentProperties.schema ?: ds.connection.catalog ?: throw RuntimeException("Can't find dataSource schema")
        return when (val databaseName = getDatabaseName(ds)) {
            "mysql", "h2" -> MySQLCommentAdapter(schema, jdbcTemplate)
            "postgresql" -> PostgreSQLCommentAdapter(schema, jdbcTemplate)
            "oracle" -> OracleCommentAdapter(schema, jdbcTemplate)
            else -> throw ServiceException("Can't find $databaseName jdbc adapter")
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun jpaCommentService(entityManager: EntityManager, jpaCommentAdapter: JpaCommentAdapter): JpaCommentService {
        val jpaCommentService = JpaCommentService(entityManager, jpaCommentAdapter)
        jpaCommentService.init()
        if (!jpaCommentProperties.alterTableNames.isNullOrBlank()) {
            when (jpaCommentProperties.alterTableNames) {
                "all" -> jpaCommentService.alterAllTableAndColumn()
                else -> {
                    jpaCommentProperties.alterTableNames?.split(",")?.forEach {
                        jpaCommentService.alterTableAndColumn(it)
                    }
                }
            }
        }
        return jpaCommentService
    }
}
