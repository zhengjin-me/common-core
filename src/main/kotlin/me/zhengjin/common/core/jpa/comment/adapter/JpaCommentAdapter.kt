package me.zhengjin.common.core.jpa.comment.adapter

interface JpaCommentAdapter {

    /**
     * 修改表注释
     *
     * @param tableName    表名称
     * @param tableComment 表注释
     */
    fun alterTableComment(tableName: String, tableComment: String)

    /**
     * 修改表字段注释
     *
     * @param tableName     表名称
     * @param columnName    字段名称
     * @param columnComment 字段注释
     */
    fun alterColumnComment(tableName: String, columnName: String, columnComment: String)
}
