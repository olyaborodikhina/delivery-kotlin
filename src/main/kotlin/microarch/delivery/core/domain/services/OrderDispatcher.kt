package microarch.delivery.core.domain.services

import microarch.delivery.core.domain.model.courier.Courier
import microarch.delivery.core.domain.model.order.Order

class OrderDispatcher {

    fun dispatch(order: Order, courier: Courier): Pair<Courier, Order> {
        val updatedCourier = courier.takeOrder(order)
        val assignedOrder = order.assign()
        return Pair(updatedCourier, assignedOrder)
    }
}
