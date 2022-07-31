package me.zhengjin.common.core.validation

import cn.hutool.core.util.IdcardUtil
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [IdentValidated::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class IdCard(
    val message: String = "身份证号码无效",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class IdentValidated : ConstraintValidator<IdCard, String> {

    override fun initialize(constraintAnnotation: IdCard) {}
    override fun isValid(value: String, context: ConstraintValidatorContext) =
        IdcardUtil.isValidCard18(value, false)
}
