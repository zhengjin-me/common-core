package me.zhengjin.common.core.exception

/**
 * 服务异常基类
 */
open class ServiceException(
    override val message: String? = null,
    override val cause: Throwable? = null,
    /**
     * 异常类型
     */
    val type: ExceptionType = Exceptions.INTERNAL_SERVER_ERROR,
    /**
     * 是否打印异常栈信息
     */
    val printStack: Boolean? = null,
) : RuntimeException(message, cause) {
    /**
     * 是否打印异常栈
     */
    val enableStack
        get() = printStack ?: type.printStack

    class ExceptionType(
        val code: Int,
        val message: String,
        val printStack: Boolean = false,
    )

    object Exceptions {
        val IDEMPOTENT_CHECK = ExceptionType(200, "数据已存在, 无需重复推送")
        val UNAUTHORIZED = ExceptionType(401, "未进行身份认证")
        val FORBIDDEN = ExceptionType(403, "无权访问")

        @Deprecated("避免直接使用该项")
        val NOT_FOUND = ExceptionType(404, "请求资源未找到")

        @Deprecated("避免直接使用该项")
        val INTERNAL_SERVER_ERROR = ExceptionType(500, "服务内部错误", true)
        val ILLEGAL_ARGUMENT = ExceptionType(10000, "非法参数")
        val DUPLICATE_BATCH_NO = ExceptionType(10001, "批次号已被使用")
        val DUPLICATE_DATA = ExceptionType(10002, "数据重复")
        val OPERATE_TIMEOUT = ExceptionType(10003, "操作超时,请稍后重试")
        val VERIFICATION_WRONG = ExceptionType(10004, "验证码错误")
    }

    companion object {
        /**
         * 不符合预期时抛出异常
         * @param value         为false时抛出异常
         * @param exceptionType 抛出的异常类型
         * @param lazyMessage   异常描述
         */
        fun requireTrue(
            value: Boolean,
            exceptionType: ExceptionType = Exceptions.ILLEGAL_ARGUMENT,
            printStack: Boolean = false,
            lazyMessage: () -> String?
        ) {
            if (!value) {
                throw ServiceException(
                    type = exceptionType,
                    message = lazyMessage() ?: exceptionType.message,
                    printStack = printStack,
                )
            }
        }

        /**
         * 值为null时抛出异常
         * @param value         为null时抛出异常
         * @param exceptionType 抛出的异常类型
         * @param lazyMessage   异常描述
         */
        fun requireNotNull(
            value: Any?,
            exceptionType: ExceptionType = Exceptions.ILLEGAL_ARGUMENT,
            printStack: Boolean = false,
            lazyMessage: () -> String?
        ) {
            if (value == null) {
                throw ServiceException(
                    type = exceptionType,
                    message = lazyMessage() ?: exceptionType.message,
                    printStack = printStack,
                )
            }
        }

        /**
         * 值为null或空字符串时抛出异常
         * @param value         为null或空字符串时抛出异常
         * @param exceptionType 抛出的异常类型
         * @param lazyMessage   异常描述
         */
        fun requireNotNullOrBlank(
            value: String?,
            exceptionType: ExceptionType = Exceptions.ILLEGAL_ARGUMENT,
            printStack: Boolean = false,
            lazyMessage: () -> String?
        ) {
            if (value.isNullOrBlank()) {
                throw ServiceException(
                    type = exceptionType,
                    message = lazyMessage() ?: exceptionType.message,
                    printStack = printStack,
                )
            }
        }

        /**
         * 判断数组值为null或空数组时抛出异常
         * @param value         判断数组为null或空数组时抛出异常
         * @param exceptionType 抛出的异常类型
         * @param lazyMessage   异常描述
         */
        fun requireNotNullOrEmpty(
            value: List<Any>?,
            exceptionType: ExceptionType = Exceptions.ILLEGAL_ARGUMENT,
            printStack: Boolean = false,
            lazyMessage: () -> String?
        ) {
            if (value.isNullOrEmpty()) {
                throw ServiceException(
                    type = exceptionType,
                    message = lazyMessage() ?: exceptionType.message,
                    printStack = printStack,
                )
            }
        }
    }
}
