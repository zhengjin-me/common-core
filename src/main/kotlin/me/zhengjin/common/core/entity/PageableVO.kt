package me.zhengjin.common.core.entity

import com.fasterxml.jackson.annotation.JsonProperty
import me.zhengjin.common.core.jpa.JpaHelper
import org.springframework.data.domain.Pageable
import java.io.Serializable

abstract class PageableVO : Serializable {

    /**
     * 当前页码
     */
    @JsonProperty("page")
    var page: Int = 1

    /**
     * 分页内容数量
     */
    @JsonProperty("size")
    var size: Int = 15

    fun getPageable(): Pageable = JpaHelper.getPageable(page, size)
}
