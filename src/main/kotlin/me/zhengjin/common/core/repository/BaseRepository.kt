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
