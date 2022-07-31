package me.zhengjin.common.core.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import me.zhengjin.common.core.jpa.comment.annotation.JpaComment
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import java.io.Serializable
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlTransient

/**
 * @version V1.0
 * @title: IdEntity
 * @package me.zhengjin.common.core.entity
 * @description: id基类
 * @author fangzhengjin
 * @date 2019/1/28 14:52
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
abstract class IdEntity : Serializable {

    /*
     * 注解放在id 属性上会引起hibernate OneToMany 的异常
     * org.hibernate.MappingException: Could not determine type for: java.util.List, at table:
     AbstractEntity must not be annotated with @Entity and @Inheritance. It must be annotated with @MappedSuperclass. Indeed, this inheritance is only used to inherit common attributes, and that's what MappedSuperclass is for.
     查询后方知
     The exception you get is caused by the lack of coherence in the position of your mapping annotations. The base superclass annotated the getters, and the subclasses annotate the fields. Hibernate uses the position of the Id annotation to determine the access type of the entity. Since @Id is on a getter, it only considers the annotations placed on getters, and ignores those placed on fields. Put all your annotations either on fields (which I would recommend) or on getters.
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(
        name = "uuid2",
        strategy = "org.hibernate.id.UUIDGenerator",
        parameters = [
            Parameter(
                name = "uuid_gen_strategy_class",
                value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
            )
        ]
    )
    @Access(value = AccessType.PROPERTY)
    @XmlTransient
    @Column(name = "id", length = 50)
    @JpaComment("主键")
    var id: String? = null

    @JsonIgnore
    @Version
    @XmlTransient
    @Column(name = "version")
    @JpaComment("版本")
    var version: Int = 0
}
