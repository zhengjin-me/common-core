package me.zhengjin.common.core.jpa.comment.adapter

import org.springframework.jdbc.core.JdbcTemplate

class OracleCommentAdapter(
    private val schema: String,
    private val jdbcTemplate: JdbcTemplate
) : JpaCommentAdapter {
    companion object {
        /**
         * 1schema 2表名称 3注释
         */
        const val UPDATE_TABLE_COMMENT_SQL = "COMMENT ON TABLE %s.%s IS '%s'"

        /**
         * 1schema 2表名称 3字段名称 4注释
         */
        const val GET_UPDATE_COLUMN_COMMENT_SQL = "COMMENT ON COLUMN %s.%s.%s IS '%s'"
    }

    override fun alterTableComment(tableName: String, tableComment: String) {
        jdbcTemplate.update(String.format(UPDATE_TABLE_COMMENT_SQL, schema, tableName.uppercase()), tableComment)
    }

    override fun alterColumnComment(tableName: String, columnName: String, columnComment: String) {
        val updateColumnComment = jdbcTemplate.queryForObject(GET_UPDATE_COLUMN_COMMENT_SQL, String::class.java, schema, tableName.uppercase(), columnName.uppercase())
        if (updateColumnComment.isNotBlank()) {
            jdbcTemplate.update(updateColumnComment, columnComment)
        }
    }
}
