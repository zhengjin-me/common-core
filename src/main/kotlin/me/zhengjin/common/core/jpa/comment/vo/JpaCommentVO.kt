package me.zhengjin.common.core.jpa.comment.vo

class JpaCommentVO(
    var type: JpaCommentType? = null,
    var name: String? = null,
    var comment: String? = null,
    var items: List<JpaCommentVO>? = null
) {
    enum class JpaCommentType {
        TABLE,
        COLUMN
    }

    companion object {
        fun table(name: String, comment: String? = null) = JpaCommentVO(JpaCommentType.TABLE, name, comment)
        fun column(name: String, comment: String? = null) = JpaCommentVO(JpaCommentType.COLUMN, name, comment)
    }
}
