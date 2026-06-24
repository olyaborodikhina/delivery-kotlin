package microarch.delivery.core.domain.model.delivery

import libs.errs.DomainInvariantException
import microarch.delivery.core.domain.model.Location
import microarch.delivery.core.domain.model.Volume
import microarch.delivery.core.domain.model.delivery.AssignmentStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class AssignmentTest {

    private val orderId = UUID.randomUUID()
    private val volume = Volume(1.0)
    private val location = Location(5, 5)

    @Test
    fun `should create assignment with valid parameters`() {
        val assignment = Assignment.create(orderId, volume, location)

        assertNotNull(assignment.id)
        assertEquals(orderId, assignment.orderId)
        assertEquals(volume, assignment.volume)
        assertEquals(location, assignment.location)
        assertEquals(AssignmentStatus.Assigned, assignment.status)
    }

    @Test
    fun `should generate unique ids for different assignments`() {
        val a = Assignment.create(orderId, volume, location)
        val b = Assignment.create(orderId, volume, location)

        assert(a.id != b.id)
    }

    @Test
    fun `should complete when courier is at same location`() {
        val assignment = Assignment.create(orderId, volume, location)

        val completed = assignment.complete(location)

        assertEquals(AssignmentStatus.Completed, completed.status)
    }

    @Test
    fun `should complete when courier is exactly 1 cell away`() {
        val assignment = Assignment.create(orderId, volume, location)
        val courierLocation = Location(5, 6)

        val completed = assignment.complete(courierLocation)

        assertEquals(AssignmentStatus.Completed, completed.status)
    }

    @Test
    fun `should throw when courier is more than 1 cell away`() {
        val assignment = Assignment.create(orderId, volume, location)
        val farCourierLocation = Location(1, 1)

        assertThrows<DomainInvariantException> {
            assignment.complete(farCourierLocation)
        }
    }

    @Test
    fun `should throw when completing not assigned assignment`() {
        val completed = Assignment.create(orderId, volume, location).complete(location)

        assertThrows<DomainInvariantException> {
            completed.complete(location)
        }
    }

    @Test
    fun `should keep original status when complete throws`() {
        val assignment = Assignment.create(orderId, volume, location)
        val farCourierLocation = Location(1, 1)

        runCatching { assignment.complete(farCourierLocation) }

        assertEquals(AssignmentStatus.Assigned, assignment.status)
    }

    @Test
    fun `should throw when volume is zero`() {
        assertThrows<IllegalArgumentException> { Volume(0.0) }
    }

    @Test
    fun `should throw when volume is negative`() {
        assertThrows<IllegalArgumentException> { Volume(-1.0) }
    }

    @Test
    fun `should create volume with positive value`() {
        val v = Volume(2.5)
        assertEquals(2.5, v.value)
    }
}
