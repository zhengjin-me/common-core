package me.zhengjin.common.core.repository

import me.zhengjin.common.core.entity.BaseEntity
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

/**
 * @version V1.0
 * @title: QuerydslBaseRepository
 * @package me.zhengjin.common.core.repository
 * @description: querydsl repository基类
 * @author fangzhengjin
 * @date 2019/1/28 14:53
 */
@NoRepositoryBean
interface QuerydslBaseRepository<T : BaseEntity, ID : Serializable> : BaseRepository<T, ID>, QuerydslPredicateExecutor<T>
