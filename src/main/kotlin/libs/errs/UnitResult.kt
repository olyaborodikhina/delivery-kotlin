package libs.errs

import java.util.function.Consumer
import java.util.function.Function

class UnitResult<E : Error> private constructor(
    val isSuccess: Boolean,
    private val error: E?,
) {
    val isFailure: Boolean get() = !isSuccess

    fun getError(): E {
        check(!isSuccess) { "Cannot get error from success" }
        @Suppress("UNCHECKED_CAST")
        return error as E
    }

    fun onSuccess(handler: Runnable): UnitResult<E> {
        if (isSuccess) handler.run()
        return this
    }

    fun onFailure(handler: Consumer<in E>): UnitResult<E> {
        if (isFailure) handler.accept(getError())
        return this
    }

    fun <U> fold(onSuccess: Function<Void?, out U>, onFailure: Function<in E, out U>): U =
        if (isSuccess) onSuccess.apply(null) else onFailure.apply(getError())

    fun merge(other: UnitResult<E>): UnitResult<E> = when {
        isFailure -> this
        other.isFailure -> other
        else -> success()
    }

    fun toResult(): Result<Void?, E> =
        if (isFailure) Result.failure(getError()) else Result.success()

    fun getOrElseThrow(exceptionMapper: Function<in E, out RuntimeException>) {
        if (!isSuccess) throw exceptionMapper.apply(getError())
    }

    fun getOrElseThrow() {
        if (!isSuccess) throw DomainInvariantException(getError())
    }

    override fun toString(): String = if (isSuccess) "Success" else "Failure($error)"

    companion object {
        fun <E : Error> success(): UnitResult<E> = UnitResult(true, null)

        fun <E : Error> failure(error: E): UnitResult<E> {
            requireNotNull(error) { "Error must not be null on failure" }
            return UnitResult(false, error)
        }

        fun <E : Error> from(result: Result<Void?, E>): UnitResult<E> =
            if (result.isSuccess) success() else failure(result.getError())
    }
}
