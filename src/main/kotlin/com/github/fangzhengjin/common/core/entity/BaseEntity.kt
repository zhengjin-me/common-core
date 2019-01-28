package com.github.fangzhengjin.common.core.entity

import com.alibaba.fastjson.annotation.JSONField
import io.swagger.annotations.ApiModelProperty
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

    @ApiModelProperty(hidden = true, readOnly = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @XmlTransient
    @JSONField(name = "createdTime")
    @OrderBy("created_time desc")
    @Column(name = "created_time", columnDefinition = "date", nullable = false)
    open var createdTime: Date? = null

    /**
     * 如有需要可按如下方式对时间进行格式化, Spring默认按jackson默认方式转换成时间戳
     * a { @Link com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS }
     *
     * @JsonSerialize(contentAs = ) 会损失时区, 采用JsonFormat合适
     * @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
     */
    @ApiModelProperty(hidden = true, readOnly = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @XmlTransient
    @JSONField(name = "updatedTime")
    @Column(name = "updated_time", columnDefinition = "date", nullable = false)
    open var updatedTime: Date? = null


    @ApiModelProperty(hidden = true, readOnly = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @XmlTransient
    @JSONField(serialize = false)
    @Column(name = "is_delete", columnDefinition = "char(1) default '0'")
    var delete = false

    @PrePersist
    fun onPersist() {
        createdTime = if (Objects.isNull(createdTime)) Date() else createdTime
        updatedTime = if (Objects.isNull(updatedTime)) Date() else updatedTime
    }

    @PreUpdate
    fun onUpdate() {
        updatedTime = Date()
    }

}
