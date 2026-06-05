package microarch.delivery.core.domain.model

import kotlin.math.abs

data class Location(
    val x: Int,
    val y: Int
) {
    init {
        require(x in 1..10) { "x must be in range [1..10], but was $x" }
        require(y in 1..10) { "y must be in range [1..10], but was $y" }
    }

    fun distanceTo(other: Location): Int =
        abs(x - other.x) + abs(y - other.y)
}
