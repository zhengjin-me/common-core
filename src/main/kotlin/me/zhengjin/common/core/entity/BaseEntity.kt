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

package me.zhengjin.common.core.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import me.zhengjin.common.core.jpa.comment.annotation.JpaComment
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.Date
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass
import javax.persistence.OrderBy
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlTransient

/**
 * @version V1.0
 * @title: BaseEntity
 * @package me.zhengjin.common.core.entity
 * @description: 实体类基类
 * @author fangzhengjin
 * @date 2019/1/28 14:52
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
@XmlAccessorType(XmlAccessType.NONE)
abstract class BaseEntity : IdEntity() {

    @XmlTransient
    @OrderBy("created_time desc")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_time")
    @JpaComment("创建时间")
    var createdTime: Date? = null

    @XmlTransient
    @CreatedBy
    @Column(name = "created_user", length = 50)
    @JpaComment("创建人")
    var createdUser: String? = null

    @XmlTransient
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "modified_time")
    @JpaComment("修改时间")
    var modifiedTime: Date? = null

    @XmlTransient
    @LastModifiedBy
    @Column(name = "modified_user", length = 50)
    @JpaComment("修改人")
    var modifiedUser: String? = null

    @XmlTransient
    @JsonIgnore
    @Column(name = "is_delete")
    @JpaComment("删除标记")
    var delete = false
}
