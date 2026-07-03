package microarch.delivery.core.domain.services

import libs.errs.DomainInvariantException
import libs.errs.GeneralErrors
import microarch.delivery.core.domain.model.courier.Courier
import microarch.delivery.core.domain.model.order.Order
import microarch.delivery.core.domain.model.order.OrderStatus
import org.springframework.stereotype.Service

@Service
class OrderDispatcherImpl : IOrderDispatcher {

    override fun dispatch(order: Order, couriers: List<Courier>): Pair<Courier, Order> {
        if (order.status != OrderStatus.Created) {
            throw DomainInvariantException(
                GeneralErrors.valueIsInvalid("order.status", order.status)
            )
        }

        val assignedOrder = order.assign()

        val availableCouriers = couriers.filter { it.canTakeOrder(assignedOrder) }

        if (availableCouriers.isEmpty()) {
            throw DomainInvariantException(
                GeneralErrors.collectionIsTooSmall(1, 0)
            )
        }

        val nearest = availableCouriers.minBy { it.location.distanceTo(assignedOrder.location) }

        val courierWithAssignment = nearest.takeOrder(assignedOrder)

        return Pair(courierWithAssignment, assignedOrder)
    }
}
