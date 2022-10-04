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

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSetter
import me.zhengjin.common.core.jpa.comment.annotation.JpaComment
import me.zhengjin.common.core.utils.IdEncryptionUtils
import java.io.Serializable
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
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

    /**
     * 实际ID, 数据库存储为明文
     */
    @JsonIgnore
    @XmlTransient
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Access(value = AccessType.PROPERTY)
    @Column(name = "id")
    @JpaComment("主键")
    var id: Long? = null

    @JsonIgnore
    @XmlTransient
    @Version
    @Column(name = "version")
    @JpaComment("版本")
    var version: Int = 0

    @JsonGetter("id")
    fun getEncryptId(): String? {
        if (id == null || id == 0L) {
            return null
        }
        return encryptId(id!!)
    }

    @JsonSetter("id")
    fun setEncryptId(value: String?) {
        if (!value.isNullOrBlank()) {
            id = decryptId(value)
        }
    }

    fun decryptId(id: String): Long? = IdEncryptionUtils.decryptIgnoreError(id)

    fun encryptId(id: Long): String? = IdEncryptionUtils.encryptIgnoreError(id)
}
