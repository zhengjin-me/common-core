package me.zhengjin.common.core.jpa.comment.adapter

import org.springframework.jdbc.core.JdbcTemplate

class MySQLCommentAdapter(
    private val schema: String,
    private val jdbcTemplate: JdbcTemplate
) : JpaCommentAdapter {
    companion object {
        /**
         * 1数据库名称 2表名称 3注释
         */
        const val UPDATE_TABLE_COMMENT_SQL = " ALTER TABLE `%s`.`%s` COMMENT = ? "

        /**
         * 1数据库名称 2表名称 3字段名称
         */
        const val GET_UPDATE_COLUMN_COMMENT_SQL = """
SELECT
	CONCAT(
		'ALTER TABLE `',
		a.TABLE_SCHEMA,
		'`.`',
		a.TABLE_NAME,
		'` MODIFY COLUMN `',
		a.COLUMN_NAME,
		'` ',
		a.COLUMN_TYPE,
		( CASE WHEN a.IS_NULLABLE = 'NO' THEN ' NOT NULL ' ELSE '' END ),
		( CASE WHEN a.COLUMN_DEFAULT IS NOT NULL THEN CONCAT( ' DEFAULT \'', a.COLUMN_DEFAULT, '\' ' ) ELSE '' END ),
		' COMMENT ?' 
	) ALTER_SQL 
FROM
	information_schema.`COLUMNS` a 
WHERE
	a.TABLE_SCHEMA = ? 
	AND a.TABLE_NAME = ? 
	AND a.COLUMN_NAME = ?
        """
    }

    override fun alterTableComment(tableName: String, tableComment: String) {
        jdbcTemplate.update(String.format(UPDATE_TABLE_COMMENT_SQL, schema, tableName), tableComment)
    }

    override fun alterColumnComment(tableName: String, columnName: String, columnComment: String) {
        val updateColumnComment = jdbcTemplate.queryForObject(GET_UPDATE_COLUMN_COMMENT_SQL, String::class.java, schema, tableName, columnName)
        if (updateColumnComment.isNotBlank()) {
            jdbcTemplate.update(updateColumnComment, columnComment)
        }
    }
}
