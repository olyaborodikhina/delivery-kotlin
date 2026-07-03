package microarch.delivery.core.domain.services

import libs.errs.DomainInvariantException
import microarch.delivery.core.domain.model.Location
import microarch.delivery.core.domain.model.Volume
import microarch.delivery.core.domain.model.courier.Courier
import microarch.delivery.core.domain.model.delivery.AssignmentStatus
import microarch.delivery.core.domain.model.order.Order
import microarch.delivery.core.domain.model.order.OrderStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class OrderDispatcherTest {

    private val dispatcher: IOrderDispatcher = OrderDispatcherImpl()

    private fun makeOrder(
        volume: Double = 3.0,
        location: Location = Location(5, 5),
        status: OrderStatus = OrderStatus.Created
    ): Order {
        val base = Order.create(UUID.randomUUID(), location, Volume(volume))
        return when (status) {
            OrderStatus.Created -> base
            OrderStatus.Assigned -> base.assign()
            OrderStatus.Completed -> base.assign().complete()
        }
    }

    private fun makeCourier(location: Location = Location(1, 1)): Courier =
        Courier.create("Courier", location)

    @Test
    fun `should assign nearest courier to order`() {
        val order = makeOrder(location = Location(5, 5))
        val near = makeCourier(Location(4, 5))
        val far = makeCourier(Location(1, 1))

        val (courier, _) = dispatcher.dispatch(order, listOf(far, near))

        assertEquals(near.id, courier.id)
    }

    @Test
    fun `should assign courier at same location as order`() {
        val order = makeOrder(location = Location(3, 3))
        val onSpot = makeCourier(Location(3, 3))
        val nearby = makeCourier(Location(3, 4))

        val (courier, _) = dispatcher.dispatch(order, listOf(nearby, onSpot))

        assertEquals(onSpot.id, courier.id)
    }

    @Test
    fun `should return courier with new assignment after dispatch`() {
        val order = makeOrder(location = Location(2, 2))
        val courier = makeCourier(Location(1, 1))

        val (result, _) = dispatcher.dispatch(order, listOf(courier))

        assertEquals(1, result.assignments.size)
        assertEquals(order.id, result.assignments.first().orderId)
        assertEquals(AssignmentStatus.Assigned, result.assignments.first().status)
    }

    @Test
    fun `should return order with Assigned status after dispatch`() {
        val order = makeOrder(location = Location(2, 2))
        val courier = makeCourier(Location(1, 1))

        val (_, updatedOrder) = dispatcher.dispatch(order, listOf(courier))

        assertEquals(OrderStatus.Assigned, updatedOrder.status)
    }

    @Test
    fun `should throw when order is not in Created status`() {
        val assignedOrder = makeOrder(status = OrderStatus.Assigned)
        val courier = makeCourier()

        assertThrows<DomainInvariantException> {
            dispatcher.dispatch(assignedOrder, listOf(courier))
        }
    }

    @Test
    fun `should throw when order is Completed`() {
        val completedOrder = makeOrder(status = OrderStatus.Completed)
        val courier = makeCourier()

        assertThrows<DomainInvariantException> {
            dispatcher.dispatch(completedOrder, listOf(courier))
        }
    }

    @Test
    fun `should throw when courier list is empty`() {
        val order = makeOrder()

        assertThrows<DomainInvariantException> {
            dispatcher.dispatch(order, emptyList())
        }
    }

    @Test
    fun `should throw when all couriers are full`() {
        val order = makeOrder(volume = 3.0)
        val fullCourier = makeCourier().takeOrder(makeOrder(volume = 19.0))

        assertThrows<DomainInvariantException> {
            dispatcher.dispatch(order, listOf(fullCourier))
        }
    }

    @Test
    fun `should skip full couriers and assign to available one`() {
        val order = makeOrder(volume = 3.0, location = Location(5, 5))
        val fullCourier = makeCourier(Location(4, 5)).takeOrder(makeOrder(volume = 19.0))
        val availableCourier = makeCourier(Location(1, 1))

        val (courier, _) = dispatcher.dispatch(order, listOf(fullCourier, availableCourier))

        assertEquals(availableCourier.id, courier.id)
    }

    @Test
    fun `should not mutate input courier when dispatching`() {
        val order = makeOrder()
        val courier = makeCourier()

        dispatcher.dispatch(order, listOf(courier))

        assertEquals(0, courier.assignments.size)
    }

    @Test
    fun `should not mutate input order when dispatching`() {
        val order = makeOrder()

        dispatcher.dispatch(order, listOf(makeCourier()))

        assertEquals(OrderStatus.Created, order.status)
    }

    @Test
    fun `should pick closer courier when two are equidistant by choosing first by minBy`() {
        val order = makeOrder(location = Location(5, 5))
        val courierA = makeCourier(Location(3, 5))
        val courierB = makeCourier(Location(7, 5))

        val (courier, _) = dispatcher.dispatch(order, listOf(courierA, courierB))

        assertEquals(courierA.id, courier.id)
    }
}