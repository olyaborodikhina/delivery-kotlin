package microarch.delivery.core.domain.model.courier

import libs.errs.DomainInvariantException
import microarch.delivery.core.domain.model.Location
import microarch.delivery.core.domain.model.Volume
import microarch.delivery.core.domain.model.delivery.AssignmentStatus
import microarch.delivery.core.domain.model.order.Order
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class CourierTest {

    private val courierLocation = Location(5, 5)
    private val orderLocation = Location(5, 5)
    private val orderVolume = Volume(3.0)

    private fun makeOrder(volume: Double = 3.0, location: Location = orderLocation): Order =
        Order.create(UUID.randomUUID(), location, Volume(volume))

    @Test
    fun `should create courier with generated id`() {
        val courier = Courier.create("Ivan", courierLocation)

        assertNotNull(courier.id)
        assertEquals("Ivan", courier.name)
        assertEquals(courierLocation, courier.location)
        assertEquals(Volume.MAX_VOLUME, courier.maxVolume)
        assertTrue(courier.assignments.isEmpty())
    }

    @Test
    fun `should generate unique ids for different couriers`() {
        val a = Courier.create("Ivan", courierLocation)
        val b = Courier.create("Ivan", courierLocation)

        assertNotEquals(a.id, b.id)
    }

    @Test
    fun `should return true when courier can take order`() {
        val courier = Courier.create("Ivan", courierLocation)
        val order = makeOrder(3.0)

        assertTrue(courier.canTakeOrder(order))
    }

    @Test
    fun `should return false when order volume exceeds max`() {
        val courier = Courier.create("Ivan", courierLocation)
        val order = makeOrder(21.0)

        assertFalse(courier.canTakeOrder(order))
    }

    @Test
    fun `should return false when cumulative volume exceeds max after existing assignments`() {
        val courier = Courier.create("Ivan", courierLocation)
        val courierWith18 = courier.takeOrder(makeOrder(18.0))

        assertFalse(courierWith18.canTakeOrder(makeOrder(3.0)))
    }

    @Test
    fun `should take order and return updated courier with assignment`() {
        val courier = Courier.create("Ivan", courierLocation)
        val order = makeOrder()

        val updatedCourier = courier.takeOrder(order)

        assertEquals(1, updatedCourier.assignments.size)
        assertEquals(order.id, updatedCourier.assignments.first().orderId)
    }

    @Test
    fun `should set assignment status to Assigned when taking order`() {
        val courier = Courier.create("Ivan", courierLocation)
        val order = makeOrder()

        val updatedCourier = courier.takeOrder(order)

        assertEquals(AssignmentStatus.Assigned, updatedCourier.assignments.first().status)
    }

    @Test
    fun `should throw when taking order that exceeds max volume`() {
        val courier = Courier.create("Ivan", courierLocation)
        val order = makeOrder(21.0)

        assertThrows<DomainInvariantException> {
            courier.takeOrder(order)
        }
    }

    @Test
    fun `should not mutate original courier when taking order`() {
        val courier = Courier.create("Ivan", courierLocation)
        val order = makeOrder()

        courier.takeOrder(order)

        assertTrue(courier.assignments.isEmpty())
    }

    @Test
    fun `should complete assignment when courier is at same location`() {
        val courier = Courier.create("Ivan", courierLocation)
        val order = makeOrder(location = courierLocation)
        val courierWithAssignment = courier.takeOrder(order)
        val assignmentId = courierWithAssignment.assignments.first().id

        val updatedCourier = courierWithAssignment.completeAssignment(assignmentId)

        assertEquals(AssignmentStatus.Completed, updatedCourier.assignments.first().status)
    }

    @Test
    fun `should complete assignment when courier is exactly 1 cell away`() {
        val courier = Courier.create("Ivan", Location(5, 5))
        val order = makeOrder(location = Location(5, 6))
        val courierWithAssignment = courier.takeOrder(order)
        val assignmentId = courierWithAssignment.assignments.first().id

        val updatedCourier = courierWithAssignment.completeAssignment(assignmentId)

        assertEquals(AssignmentStatus.Completed, updatedCourier.assignments.first().status)
    }

    @Test
    fun `should throw when completing assignment courier is too far`() {
        val courier = Courier.create("Ivan", Location(1, 1))
        val order = makeOrder(location = Location(5, 5))
        val courierWithAssignment = courier.takeOrder(order)
        val assignmentId = courierWithAssignment.assignments.first().id

        assertThrows<DomainInvariantException> {
            courierWithAssignment.completeAssignment(assignmentId)
        }
    }

    @Test
    fun `should throw when completing non-existent assignment`() {
        val courier = Courier.create("Ivan", courierLocation)

        assertThrows<DomainInvariantException> {
            courier.completeAssignment(UUID.randomUUID())
        }
    }

    @Test
    fun `should move courier to new location`() {
        val courier = Courier.create("Ivan", courierLocation)
        val newLocation = Location(3, 7)

        val movedCourier = courier.moveTo(newLocation)

        assertEquals(newLocation, movedCourier.location)
    }

    @Test
    fun `should not mutate original courier when moving`() {
        val courier = Courier.create("Ivan", courierLocation)

        courier.moveTo(Location(3, 7))

        assertEquals(courierLocation, courier.location)
    }
}
