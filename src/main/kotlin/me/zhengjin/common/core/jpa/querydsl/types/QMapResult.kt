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
