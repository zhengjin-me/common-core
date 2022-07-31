package me.zhengjin.common.core.jpa.querydsl.types

import com.querydsl.core.types.Expression
import com.querydsl.core.types.FactoryExpressionBase
import com.querydsl.core.types.Visitor

@Suppress("UNCHECKED_CAST")
class QMapResult(
    private vararg val exprs: Expression<*>
) : FactoryExpressionBase<Map<String, *>>(Map::class.java as Class<out Map<String, *>>) {

    override fun <R, C> accept(v: Visitor<R, C>, context: C?): R? = v.visit(this, context)

    override fun getArgs(): List<Expression<*>> = exprs.toList()

    override fun newInstance(vararg args: Any): Map<String, *> {
        val map = HashMap<String, Any>(args.size)
        args.forEachIndexed { i, _ ->
            var key = getArgs()[i].toString()
            key = if (key.contains(" as ")) {
                key.split(" as ").last()
            } else {
                key.split(".").last()
            }
            map[key] = args[i]
        }
        return map
    }
}
