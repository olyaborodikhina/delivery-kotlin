package microarch.delivery.core.domain.model.courier

import libs.errs.DomainInvariantException
import libs.errs.GeneralErrors
import microarch.delivery.core.domain.model.Location
import microarch.delivery.core.domain.model.Volume
import microarch.delivery.core.domain.model.delivery.Assignment
import microarch.delivery.core.domain.model.order.Order
import java.util.UUID

class Courier(
    val id: UUID,
    val name: String,
    val location: Location,
    val maxVolume: Volume,
    val assignments: List<Assignment>
) {

    fun canTakeOrder(order: Order): Boolean {
        val currentVolume = assignments.sumOf { it.volume.value }
        return currentVolume + order.volume.value <= maxVolume.value
    }

    fun takeOrder(order: Order): Pair<Courier, Order> {
        if (!canTakeOrder(order)) {
            throw DomainInvariantException(
                GeneralErrors.valueIsInvalid("order.volume", order.volume)
            )
        }
        val assignment = Assignment.create(order.id, order.volume, order.location)
        val updatedCourier = Courier(id, name, location, maxVolume, assignments + assignment)
        val assignedOrder = order.assign()
        return Pair(updatedCourier, assignedOrder)
    }

    fun completeAssignment(assignmentId: UUID): Courier {
        val assignment = assignments.find { it.id == assignmentId }
            ?: throw DomainInvariantException(
                GeneralErrors.notFound("assignment", assignmentId)
            )
        val completed = assignment.complete(location)
        val updatedAssignments = assignments.map { if (it.id == assignmentId) completed else it }
        return Courier(id, name, location, maxVolume, updatedAssignments)
    }

    fun moveTo(newLocation: Location): Courier {
        return Courier(id, name, newLocation, maxVolume, assignments)
    }

    companion object {
        fun create(name: String, location: Location): Courier {
            return Courier(
                id = UUID.randomUUID(),
                name = name,
                location = location,
                maxVolume = Volume.MAX_VOLUME,
                assignments = emptyList()
            )
        }
    }
}
