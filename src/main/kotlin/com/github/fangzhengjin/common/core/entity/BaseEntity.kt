package com.github.fangzhengjin.common.core.entity

import com.alibaba.fastjson.annotation.JSONField
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

/**
 * @version V1.0
 * @title: BaseEntity
 * @package com.github.fangzhengjin.common.core.entity
 * @description: 实体类基类
 * @author fangzhengjin
 * @date 2019/1/28 14:52
 */
@MappedSuperclass
abstract class BaseEntity : IdEntity() {

    @ApiModelProperty(readOnly = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @XmlTransient
    @JSONField(name = "createdTime")
    @OrderBy("created_time desc")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    open var createdTime: Date? = null

    @ApiModelProperty(readOnly = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @XmlTransient
    @JSONField(name = "createdUser")
    @CreatedBy
    open var createdUser: String? = null

    /**
     * 如有需要可按如下方式对时间进行格式化, Spring默认按jackson默认方式转换成时间戳
     * a { @Link com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS }
     *
     * @JsonSerialize(contentAs = ) 会损失时区, 采用JsonFormat合适
     * @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
     */
    @ApiModelProperty(readOnly = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @XmlTransient
    @JSONField(name = "modifiedTime")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    open var modifiedTime: Date? = null

    @ApiModelProperty(readOnly = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @XmlTransient
    @JSONField(name = "modifiedUser")
    @LastModifiedBy
    open var modifiedUser: String? = null


    @ApiModelProperty(hidden = true, readOnly = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @XmlTransient
    @JSONField(serialize = false)
    @Column(name = "is_delete")
    var delete = false

//    @PrePersist
//    fun onPersist() {
//        createdTime = if (Objects.isNull(createdTime)) Date() else createdTime
//        updatedTime = if (Objects.isNull(updatedTime)) Date() else updatedTime
//    }
//
//    @PreUpdate
//    fun onUpdate() {
//        updatedTime = Date()
//    }

}
