package com.github.fangzhengjin.common.core.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("分页数据集")
class PageResult<T>(
        @ApiModelProperty("查询内容")
        val content: List<T>,
        @ApiModelProperty("总记录数")
        val totalElements: Long,
        @ApiModelProperty("总页数")
        val totalPages: Int,
        @ApiModelProperty("当前页码")
        val currentPage: Int
)