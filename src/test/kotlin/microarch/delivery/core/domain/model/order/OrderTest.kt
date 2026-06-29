package microarch.delivery.core.domain.model.order

import libs.errs.DomainInvariantException
import microarch.delivery.core.domain.model.Location
import microarch.delivery.core.domain.model.Volume
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class OrderTest {

    private val id = UUID.randomUUID()
    private val location = Location(5, 5)
    private val volume = Volume(3.0)

    @Test
    fun `should create order with status Created`() {
        val order = Order.create(id, location, volume)

        assertEquals(id, order.id)
        assertEquals(location, order.location)
        assertEquals(volume, order.volume)
        assertEquals(OrderStatus.Created, order.status)
    }

    @Test
    fun `should assign order when status is Created`() {
        val order = Order.create(id, location, volume)

        val assigned = order.assign()

        assertEquals(OrderStatus.Assigned, assigned.status)
    }

    @Test
    fun `should throw when assigning order with status Assigned`() {
        val order = Order.create(id, location, volume).assign()

        assertThrows<DomainInvariantException> {
            order.assign()
        }
    }

    @Test
    fun `should throw when assigning order with status Completed`() {
        val order = Order.create(id, location, volume).assign().complete()

        assertThrows<DomainInvariantException> {
            order.assign()
        }
    }

    @Test
    fun `should complete order when status is Assigned`() {
        val order = Order.create(id, location, volume).assign()

        val completed = order.complete()

        assertEquals(OrderStatus.Completed, completed.status)
    }

    @Test
    fun `should throw when completing order with status Created`() {
        val order = Order.create(id, location, volume)

        assertThrows<DomainInvariantException> {
            order.complete()
        }
    }

    @Test
    fun `should throw when completing order with status Completed`() {
        val order = Order.create(id, location, volume).assign().complete()

        assertThrows<DomainInvariantException> {
            order.complete()
        }
    }

    @Test
    fun `should keep original status when assign throws`() {
        val order = Order.create(id, location, volume).assign()

        runCatching { order.assign() }

        assertEquals(OrderStatus.Assigned, order.status)
    }

    @Test
    fun `should preserve id and fields after status transitions`() {
        val order = Order.create(id, location, volume)
        val assigned = order.assign()
        val completed = assigned.complete()

        assertEquals(id, completed.id)
        assertEquals(location, completed.location)
        assertEquals(volume, completed.volume)
    }

    @Test
    fun `should not be null after creation`() {
        val order = Order.create(id, location, volume)

        assertNotNull(order)
    }
}
