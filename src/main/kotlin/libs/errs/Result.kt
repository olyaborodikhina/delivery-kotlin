package libs.errs

import java.util.function.Consumer
import java.util.function.Function

class Result<T, E : Error> private constructor(
    private val value: T?,
    private val error: E?,
    val isSuccess: Boolean,
) {
    val isFailure: Boolean get() = !isSuccess

    fun getValue(): T {
        check(isSuccess) { "Cannot get value from failure" }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    fun getError(): E {
        check(!isSuccess) { "Cannot get error from success" }
        @Suppress("UNCHECKED_CAST")
        return error as E
    }

    fun <U> map(mapper: Function<in T, out U>): Result<U, E> =
        if (isSuccess) success(mapper.apply(getValue())) else failure(getError())

    fun <U> flatMap(mapper: Function<in T, Result<U, E>>): Result<U, E> =
        if (isSuccess) mapper.apply(getValue()) else failure(getError())

    fun onSuccess(handler: Consumer<in T>): Result<T, E> {
        if (isSuccess) handler.accept(getValue())
        return this
    }

    fun onFailure(handler: Consumer<in E>): Result<T, E> {
        if (isFailure) handler.accept(getError())
        return this
    }

    fun <U> fold(onSuccess: Function<in T, out U>, onFailure: Function<in E, out U>): U =
        if (isSuccess) onSuccess.apply(getValue()) else onFailure.apply(getError())

    fun <F : Error> mapError(mapper: Function<in E, out F>): Result<T, F> =
        if (isSuccess) success(getValue()) else failure(mapper.apply(getError()))

    fun getValueOrThrow(): T {
        if (isSuccess) return getValue()
        throw DomainInvariantException(getError())
    }

    override fun toString(): String = if (isSuccess) "Success($value)" else "Failure($error)"

    companion object {
        fun <T, E : Error> success(value: T): Result<T, E> {
            requireNotNull(value)
            return Result(value, null, true)
        }

        fun <E : Error> success(): Result<Void?, E> = Result(null, null, true)

        fun <T, E : Error> failure(error: E): Result<T, E> {
            requireNotNull(error)
            return Result(null, error, false)
        }
    }
}
