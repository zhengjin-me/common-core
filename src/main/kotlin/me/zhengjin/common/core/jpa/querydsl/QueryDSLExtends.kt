package me.zhengjin.common.core.jpa.querydsl

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.JPQLQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mapping.PropertyPath
import org.springframework.data.querydsl.QSort

/**
 * 查询返回Spring Data Page对象
 *
 * @return page data [Page].
 */
fun <T> JPQLQuery<T>.fetchPage(): Page<T> {
    val totalElements = fetchCount()
    val result = if (totalElements > 0) {
        fetch()
    } else {
        emptyList()
    }
    return PageImpl(
        result,
        PageRequest.of(
            (metadata.modifiers.limitAsInteger?.let { metadata.modifiers.offsetAsInteger?.div(it) }) ?: 0,
            metadata.modifiers.limitAsInteger ?: 10
        ),
        totalElements
    )
}

/**
 * Applies the given [Pageable] to the given [JPQLQuery].
 *
 * @param pageable must not be null.
 * @return the Querydsl [JPQLQuery].
 */
fun <T> JPQLQuery<T>.applyPagination(pageable: Pageable): JPQLQuery<T> {
    offset(pageable.offset)
    limit(pageable.pageSize.toLong())
    return applySorting(pageable.sort)
}

/**
 * Applies the given [Pageable] to the given [JPQLQuery].
 *
 * @param sort must not be null.
 * @return the Querydsl [JPQLQuery].
 */
fun <T> JPQLQuery<T>.applySorting(sort: Sort): JPQLQuery<T> {
    if (sort.isUnsorted) {
        return this
    }

    return if (sort is QSort) {
        addOrderByFrom(sort)
    } else addOrderByFrom(sort)
}

/**
 * Applies the given [OrderSpecifier]s to the given [JPQLQuery]. Potentially transforms the given
 * `OrderSpecifier`s to be able to injection potentially necessary left-joins.
 *
 * @param qsort must not be null.
 */
fun <T> JPQLQuery<T>.addOrderByFrom(qsort: QSort): JPQLQuery<T> {
    val orderSpecifiers = qsort.orderSpecifiers
    return orderBy(*orderSpecifiers.toTypedArray())
}

/**
 * Converts the [Order] items of the given [Sort] into [OrderSpecifier] and attaches those to the
 * given [JPQLQuery].
 *
 * @param sort must not be null.
 * @return
 */
fun <T> JPQLQuery<T>.addOrderByFrom(sort: Sort): JPQLQuery<T> {
    for (order in sort) {
        orderBy(toOrderSpecifier(order))
    }
    return this
}

/**
 * Transforms a plain [Order] into a QueryDsl specific [OrderSpecifier].
 *
 * @param order must not be null.
 * @return buildOrderPropertyPathFrom(order)
 */
private fun <T> JPQLQuery<T>.toOrderSpecifier(order: Sort.Order): OrderSpecifier<*> {
    return OrderSpecifier(
        if (order.isAscending) Order.ASC else Order.DESC,
        buildOrderPropertyPathFrom(order) as Expression<out Comparable<*>>,
        toQueryDslNullHandling(order.nullHandling)
    )
}

/**
 * Converts the given [org.springframework.data.domain.Sort.NullHandling] to the appropriate Querydsl [NullHandling].
 *
 * @param nullHandling must not be null.
 * @return
 * @since 1.6
 */
private fun toQueryDslNullHandling(nullHandling: Sort.NullHandling): OrderSpecifier.NullHandling {
    return when (nullHandling) {
        Sort.NullHandling.NULLS_FIRST -> OrderSpecifier.NullHandling.NullsFirst
        Sort.NullHandling.NULLS_LAST -> OrderSpecifier.NullHandling.NullsLast
        Sort.NullHandling.NATIVE -> OrderSpecifier.NullHandling.Default
        else -> OrderSpecifier.NullHandling.Default
    }
}

/**
 * Creates an [Expression] for the given [Order] property.
 *
 * @param order must not be null.
 * @return
 */
private fun <T> JPQLQuery<T>.buildOrderPropertyPathFrom(order: Sort.Order): Expression<*>? {
    var path: PropertyPath? = PropertyPath.from(order.property, type)
    var sortPropertyExpression: Expression<*>? = PathBuilder(type, metadata.projection.toString())
    while (path !== null) {
        sortPropertyExpression = if (!path.hasNext() && order.isIgnoreCase) {
            Expressions.stringPath(sortPropertyExpression as Path<*>?, path.segment).lower()
        } else {
            Expressions.path(path.type, sortPropertyExpression as Path<*>?, path.segment)
        }
        path = path.next()
    }
    return sortPropertyExpression
}
