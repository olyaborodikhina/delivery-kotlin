package microarch.delivery.core.domain.services

import microarch.delivery.core.domain.model.courier.Courier
import microarch.delivery.core.domain.model.order.Order

interface IOrderDispatcher {
    fun dispatch(order: Order, couriers: List<Courier>): Pair<Courier, Order>
}
