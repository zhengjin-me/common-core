package me.zhengjin.common.core.jpa.transformer

import org.hibernate.transform.AliasedTupleSubsetResultTransformer
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * @version V1.0
 * title: Hibernate本地查询结果集转换Map处理器
 * package: me.zhengjin.common.core.jpa.transformer
 * description: 当前处理器将Map中Key转换为小驼峰命名方式,针对时间类型使用 yyyy-MM-dd HH:mm:ss 格式化
 * @author fangzhengjin
 * cate 2018-8-22 14:31
 */
class AliasToLittleCamelCaseMapResultTransformer private constructor() : AliasedTupleSubsetResultTransformer() {

    companion object {
        @JvmStatic
        val INSTANCE = AliasToLittleCamelCaseMapResultTransformer()

        @JvmStatic
        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!
    }

    override fun transformTuple(tuple: Array<out Any>?, aliases: Array<out String>?): Any {
        val result = HashMap<Any, Any?>(tuple!!.size)
        for (i in tuple.indices) {
            val alias = aliases?.get(i)
            if (alias != null) {
                val key = alias.lowercase().split(Regex("[_-]")).reduce { s1, s2 ->
                    if (s2.isBlank()) {
                        return@reduce s1
                    }
                    return@reduce s1 + s2.substring(0, 1).uppercase() + s2.substring(1)
                }
                result[key] = tuple[i].run {
                    when (this) {
                        is Date -> dtf.format(LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault()))
                        else -> this
                    }
                }
            }
        }
        return result
    }

    override fun isTransformedValueATupleElement(aliases: Array<out String>?, tupleLength: Int): Boolean = false

    private fun readResolve(): Any {
        return INSTANCE
    }
}
