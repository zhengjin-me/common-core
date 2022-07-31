package me.zhengjin.common.core.repository

import me.zhengjin.common.core.entity.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.Param
import java.io.Serializable

/**
 * @version V1.0
 * @title: BaseRepository
 * @package me.zhengjin.common.core.repository
 * @description: repository基类
 * @author fangzhengjin
 * @date 2019/1/28 14:53
 */
@NoRepositoryBean
interface BaseRepository<T : BaseEntity, ID : Serializable> : JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    @Query("select x from #{#entityName} x where x.delete <> true and x.id = :id")
    fun findActiveOne(@Param("id") id: ID): T?

    @Query("select x from #{#entityName} x where x.delete <> true")
    fun findActiveAll(): List<T>

    @Modifying
    @Query("update #{#entityName} a set a.delete = true where a.id = :id")
    fun softDelete(@Param("id") id: ID)

    @Modifying
    @Query("update #{#entityName} a set a.delete = true where a.id in :ids")
    fun softDelete(@Param("ids") ids: List<ID>)
}
