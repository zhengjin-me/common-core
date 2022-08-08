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
