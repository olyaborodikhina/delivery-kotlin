package microarch.delivery.core.domain.model.delivery

import libs.errs.DomainInvariantException
import libs.errs.GeneralErrors
import microarch.delivery.core.domain.model.Location
import microarch.delivery.core.domain.model.Volume
import java.util.UUID

class Assignment private constructor(
    val id: UUID,
    val orderId: UUID,
    val volume: Volume,
    val location: Location,
    val status: AssignmentStatus
) {

    init {
        requireNotNull(id) { "id must not be null" }
        requireNotNull(orderId) { "orderId must not be null" }
        requireNotNull(volume) { "volume must not be null" }
        requireNotNull(location) { "location must not be null" }
        requireNotNull(status) { "status must not be null" }
    }

    fun complete(courierLocation: Location): Assignment {
        if (status != AssignmentStatus.Assigned) {
            throw DomainInvariantException(
                GeneralErrors.valueIsInvalid("status", status)
            )
        }
        if (courierLocation.distanceTo(location) > MAX_COMPLETION_DISTANCE) {
            throw DomainInvariantException(
                GeneralErrors.valueIsInvalid("courierLocation", courierLocation)
            )
        }
        return Assignment(id, orderId, volume, location, AssignmentStatus.Completed)
    }

    companion object {
        private const val MAX_COMPLETION_DISTANCE = 1

        fun create(orderId: UUID, volume: Volume, location: Location): Assignment {
            return Assignment(
                id = UUID.randomUUID(),
                orderId = orderId,
                volume = volume,
                location = location,
                status = AssignmentStatus.Assigned
            )
        }
    }

}
