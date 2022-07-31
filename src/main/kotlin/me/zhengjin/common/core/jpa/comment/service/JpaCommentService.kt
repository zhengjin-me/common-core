package me.zhengjin.common.core.jpa.comment.service

import cn.hutool.core.util.ClassUtil
import me.zhengjin.common.core.jpa.comment.adapter.JpaCommentAdapter
import me.zhengjin.common.core.jpa.comment.annotation.JpaComment
import me.zhengjin.common.core.jpa.comment.vo.JpaCommentVO
import org.hibernate.internal.SessionFactoryImpl
import org.hibernate.persister.entity.SingleTableEntityPersister
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationUtils
import javax.persistence.EntityManager

class JpaCommentService(
    private val entityManager: EntityManager,
    private val jpaCommentAdapter: JpaCommentAdapter
) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private var commentCache: Map<String, JpaCommentVO> = mapOf()
    }

    fun init() {
        commentCache = getAllTableAndColumn()
    }

    /**
     * 获取这个类的所有父类(包含自己)
     * @param clazz
     * @return
     */
    private fun getAllClass(clazz: Class<*>): List<Class<*>> {
        val clazzList: MutableList<Class<*>> = ArrayList()
        clazzList.add(clazz)
        var suCl = clazz.superclass
        while (suCl != null && Any::class.java != suCl) {
            clazzList.add(suCl)
            suCl = suCl.superclass
        }
        return clazzList
    }

    /**
     * 获取表注释
     */
    private fun getTableComment(entity: SingleTableEntityPersister): JpaCommentVO {
        return JpaCommentVO.table(entity.tableName, AnnotationUtils.getAnnotation(entity.mappedClass, JpaComment::class.java)?.value)
    }

    /**
     * 获取字段注释
     */
    private fun getColumnComment(targetClass: Class<*>, propertyName: String, columnName: Array<String>): List<JpaCommentVO> {
        val idColumnComment = AnnotationUtils.getAnnotation(
            ClassUtil.getDeclaredField(targetClass, propertyName), JpaComment::class.java
        )
        return columnName.map { item ->
            val column = JpaCommentVO.column(item)
            if (idColumnComment != null) {
                column.comment = idColumnComment.value
            }
            column
        }
    }

    /**
     * 获取所有被Jpa管理且被JpaComment标注的注释信息
     */
    private fun getAllColumnComment(entity: SingleTableEntityPersister): MutableList<JpaCommentVO> {
        val classList = getAllClass(entity.mappedClass)
        // 获取所有字段名称(不含主键)
        val allColumnField = entity.attributes.map { it.name }.toMutableSet()
        // 添加主键字段名称
        allColumnField.add(entity.identifierPropertyName)

        val alreadyDealField = mutableSetOf<String>()
        val columnCommentList = mutableListOf<JpaCommentVO>()
        classList.forEach { classItem: Class<*> ->
            ClassUtil.getDeclaredFields(classItem).forEach { field ->
                if (allColumnField.contains(field.name)) {
                    // 判断是否已经处理过
                    if (!alreadyDealField.contains(field.name)) {
                        // 对应数据库表中的字段名
                        val columnName = entity.getPropertyColumnNames(field.name)
                        val columnComment = getColumnComment(classItem, field.name, columnName)
                        columnCommentList.addAll(columnComment)
                        alreadyDealField.add(field.name)
                    }
                }
            }
        }
        return columnCommentList
    }

    /**
     * 初始化时获取所有表注释
     */
    private fun getAllTableAndColumn(): Map<String, JpaCommentVO> {
        val entityManagerFactory = entityManager.entityManagerFactory
        val sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl::class.java)
        return sessionFactory.metamodel.entityPersisters().values.map { entity ->
            entity as SingleTableEntityPersister
            // 表注释
            val table = getTableComment(entity)
            // 属性注释
            val columns = getAllColumnComment(entity)
            table.items = columns
            table
        }.associateBy({ it.name!! }, { it })
    }

    /**
     * 更新单个数据库的表注释和字段注释，非空情况下才更新
     *
     * @param tableName 数据库表名称
     */
    fun alterTableAndColumn(tableName: String) {
        val table = commentCache[tableName]
        if (table != null) {
            if (!table.comment.isNullOrBlank()) {
                if (logger.isDebugEnabled) {
                    logger.debug("修改表 {} 的注释为 '{}'", table.name, table.comment)
                }
                jpaCommentAdapter.alterTableComment(table.name!!, table.comment!!)
            }
            table.items?.forEach { item ->
                if (!item.comment.isNullOrBlank()) {
                    if (logger.isDebugEnabled) {
                        logger.debug("修改表 {} 字段 {} 的注释为 '{}'", table.name, item.name, item.comment)
                    }
                    jpaCommentAdapter.alterColumnComment(table.name!!, item.name!!, item.comment!!)
                }
            }
        } else {
            logger.warn("tableName '{}' not find in JPA ", tableName)
        }
    }

    /**
     * 更新所有表和字段的注释
     */
    fun alterAllTableAndColumn() {
        commentCache.keys.forEach {
            try {
                alterTableAndColumn(it)
            } catch (e: Exception) {
                logger.error("tableName '{}' ALTER comment exception ", it)
            }
        }
    }
}
