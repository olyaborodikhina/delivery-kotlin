package libs.ddd

import java.math.BigDecimal

abstract class ValueObject<T : ValueObject<T>> : Comparable<T> {

    protected abstract fun equalityComponents(): Iterable<Any?>

    private fun toList(iterable: Iterable<Any?>): List<Any?> = iterable.toList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ValueObject<*>
        val thisComponents = toList(equalityComponents())
        val thatComponents = toList(that.equalityComponents())
        if (thisComponents.size != thatComponents.size) return false
        return thisComponents.zip(thatComponents).all { (a, b) -> a == b }
    }

    override fun hashCode(): Int = toList(equalityComponents()).toTypedArray().contentHashCode()

    override fun compareTo(other: T): Int {
        val thisComponents = toList(equalityComponents())
        val otherComponents = toList(other.equalityComponents())
        for (i in 0 until minOf(thisComponents.size, otherComponents.size)) {
            val result = safeCompare(thisComponents[i], otherComponents[i])
            if (result != 0) return result
        }
        return thisComponents.size.compareTo(otherComponents.size)
    }

    override fun toString(): String {
        val components = toList(equalityComponents())
        return "${javaClass.simpleName}[${components.joinToString(", ")}]"
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <V> safeCompare(a: V?, b: V?): Int {
            if (a === b) return 0
            if (a == null) return -1
            if (b == null) return 1
            if (a is BigDecimal && b is BigDecimal) return a.compareTo(b)
            require(a is Comparable<*> && b is Comparable<*>) { "Fields must be Comparable" }
            return (a as Comparable<V>).compareTo(b)
        }
    }
}
