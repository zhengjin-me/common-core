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
