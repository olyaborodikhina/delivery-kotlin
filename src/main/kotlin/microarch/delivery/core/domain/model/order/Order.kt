package microarch.delivery.core.domain.model.order

import libs.errs.DomainInvariantException
import libs.errs.GeneralErrors
import microarch.delivery.core.domain.model.Location
import microarch.delivery.core.domain.model.Volume
import java.util.UUID

class Order(
    val id: UUID,
    val location: Location,
    val volume: Volume,
    val status: OrderStatus
) {

    fun assign(): Order {
        if (status != OrderStatus.Created) {
            throw DomainInvariantException(
                GeneralErrors.valueIsInvalid("status", status)
            )
        }
        return Order(id, location, volume, OrderStatus.Assigned)
    }

    fun complete(): Order {
        if (status != OrderStatus.Assigned) {
            throw DomainInvariantException(
                GeneralErrors.valueIsInvalid("status", status)
            )
        }
        return Order(id, location, volume, OrderStatus.Completed)
    }

    companion object {
        fun create(id: UUID, location: Location, volume: Volume): Order {
            return Order(id, location, volume, OrderStatus.Created)
        }
    }
}
