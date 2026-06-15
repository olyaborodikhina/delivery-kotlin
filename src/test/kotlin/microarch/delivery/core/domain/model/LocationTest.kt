package microarch.delivery.core.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LocationTest {

    @Test
    fun `should create location with valid coordinates`() {
        val location = Location(1, 1)
        assertEquals(1, location.x)
        assertEquals(1, location.y)
    }

    @Test
    fun `should throw when x is less than 1`() {
        assertThrows<IllegalArgumentException> { Location(0, 5) }
    }

    @Test
    fun `should throw when x is greater than 10`() {
        assertThrows<IllegalArgumentException> { Location(11, 5) }
    }

    @Test
    fun `should throw when y is less than 1`() {
        assertThrows<IllegalArgumentException> { Location(5, 0) }
    }

    @Test
    fun `should throw when y is greater than 10`() {
        assertThrows<IllegalArgumentException> { Location(5, 11) }
    }

    @Test
    fun `should throw when both coordinates are out of range`() {
        assertThrows<IllegalArgumentException> { Location(-1, 11) }
    }

    @Test
    fun `should be equal when x and y are equal`() {
        val a = Location(3, 7)
        val b = Location(3, 7)
        assertEquals(a, b)
    }

    @Test
    fun `should not be equal when x differs`() {
        val a = Location(3, 7)
        val b = Location(4, 7)
        assertNotEquals(a, b)
    }

    @Test
    fun `should not be equal when y differs`() {
        val a = Location(3, 7)
        val b = Location(3, 8)
        assertNotEquals(a, b)
    }

    @Test
    fun `should have same hashCode for equal locations`() {
        val a = Location(3, 7)
        val b = Location(3, 7)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `should calculate distance as sum of absolute differences`() {
        val courier = Location(3, 3)
        val order = Location(5, 6)
        assertEquals(5, courier.distanceTo(order))
    }

    @Test
    fun `should calculate zero distance for same location`() {
        val location = Location(5, 5)
        assertEquals(0, location.distanceTo(location))
    }

    @Test
    fun `distance should be symmetric`() {
        val a = Location(2, 4)
        val b = Location(7, 1)
        assertEquals(a.distanceTo(b), b.distanceTo(a))
    }

    @Test
    fun `should calculate distance along single axis`() {
        val a = Location(1, 5)
        val b = Location(10, 5)
        assertEquals(9, a.distanceTo(b))
    }
}
