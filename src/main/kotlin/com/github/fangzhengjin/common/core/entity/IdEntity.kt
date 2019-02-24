package com.github.fangzhengjin.common.core.entity

import com.alibaba.fastjson.annotation.JSONField
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import java.io.Serializable
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

/**
 * @version V1.0
 * @title: IdEntity
 * @package com.github.fangzhengjin.common.core.entity
 * @description: id基类
 * @author fangzhengjin
 * @date 2019/1/28 14:52
 */
@MappedSuperclass
abstract class IdEntity : Serializable {

    //
    /*
     * 注解放在id 属性上会引起hibernate OneToMany 的异常
     * org.hibernate.MappingException: Could not determine type for: java.util.List, at table:
     AbstractEntity must not be annotated with @Entity and @Inheritance. It must be annotated with @MappedSuperclass. Indeed, this inheritance is only used to inherit common attributes, and that's what MappedSuperclass is for.
     查询后方知
     The exception you get is caused by the lack of coherence in the position of your mapping annotations. The base superclass annotated the getters, and the subclasses annotate the fields. Hibernate uses the position of the Id annotation to determine the access type of the entity. Since @Id is on a getter, it only considers the annotations placed on getters, and ignores those placed on fields. Put all your annotations either on fields (which I would recommend) or on getters.
     */
    @Id
    //序列 数字主键
    //@GeneratedValue(strategy = GenerationType.AUTO)
    //UUID 文本主键
    @ApiModelProperty(readOnly = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @GeneratedValue(generator = "system-uuid")
    @JSONField(name = "id")
    @Access(value = AccessType.PROPERTY)
    @XmlTransient
    var id: String? = null

    @ApiModelProperty(hidden = true, readOnly = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @JSONField(serialize = false)
    @Version
    @XmlTransient
    var version: Int = 0

}
