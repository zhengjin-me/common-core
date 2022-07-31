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
    @Column(name = "is_delete", columnDefinition = "char(1) NOT NULL DEFAULT 0")
    @JpaComment("删除标记")
    var delete = false
}
