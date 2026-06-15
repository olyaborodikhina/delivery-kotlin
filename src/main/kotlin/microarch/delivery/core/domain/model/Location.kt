package microarch.delivery.core.domain.model

import kotlin.math.abs

data class Location(
    val x: Int,
    val y: Int
) {
    companion object {
        private const val MIN_COORDINATE = 1
        private const val MAX_COORDINATE = 10
        private val VALID_RANGE = MIN_COORDINATE..MAX_COORDINATE
    }

    init {
        require(x in VALID_RANGE) { "x must be in range [$MIN_COORDINATE..$MAX_COORDINATE], but was $x" }
        require(y in VALID_RANGE) { "y must be in range [$MIN_COORDINATE..$MAX_COORDINATE], but was $y" }
    }

    fun distanceTo(other: Location): Int =
        abs(x - other.x) + abs(y - other.y)
}
