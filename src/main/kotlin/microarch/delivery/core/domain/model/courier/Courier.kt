package microarch.delivery.core.domain.model.courier

import libs.errs.DomainInvariantException
import libs.errs.GeneralErrors
import microarch.delivery.core.domain.model.Location
import microarch.delivery.core.domain.model.Volume
import microarch.delivery.core.domain.model.delivery.Assignment
import microarch.delivery.core.domain.model.order.Order
import java.util.UUID

class Courier private constructor(
    val id: UUID,
    val name: String,
    val location: Location,
    val maxVolume: Volume,
    val assignments: List<Assignment>
) {

    init {
        requireNotNull(id) { "id must not be null" }
        require(name.isNotBlank()) { "name must not be blank" }
        requireNotNull(location) { "location must not be null" }
        requireNotNull(maxVolume) { "maxVolume must not be null" }
        requireNotNull(assignments) { "assignments must not be null" }
    }

    fun canTakeOrder(order: Order): Boolean {
        val currentVolume = assignments.map { it.volume }.reduceOrNull { acc, v -> acc.add(v) }
        return if (currentVolume == null) order.volume <= maxVolume
        else currentVolume.add(order.volume) <= maxVolume
    }

    fun takeOrder(order: Order): Courier {
        if (!canTakeOrder(order)) {
            throw DomainInvariantException(
                GeneralErrors.valueIsInvalid("order.volume", order.volume)
            )
        }
        val assignment = Assignment.create(order.id, order.volume, order.location)
        return Courier(id, name, location, maxVolume, assignments + assignment)
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
