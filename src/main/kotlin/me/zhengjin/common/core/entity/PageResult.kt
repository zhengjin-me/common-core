package me.zhengjin.common.core.entity

/**
 * 分页数据集
 */
class PageResult<T>(
    /**
     * 查询内容
     */
    val content: List<T>? = null,
    /**
     * 总记录数
     */
    val totalElements: Long? = null,
    /**
     * 总页数
     */
    val totalPages: Int? = null,
    /**
     * 当前页码
     */
    val currentPage: Int? = null
)
