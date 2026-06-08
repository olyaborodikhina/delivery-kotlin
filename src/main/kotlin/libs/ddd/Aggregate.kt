package libs.ddd

import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient

@MappedSuperclass
abstract class Aggregate<TId : Comparable<TId>> : BaseEntity<TId>, AggregateRoot<TId> {

    @Transient
    private val domainEvents: MutableList<DomainEvent> = mutableListOf()

    protected constructor() : super()

    protected constructor(id: TId) : super(id)

    override fun getDomainEvents(): List<DomainEvent> = domainEvents.toList()

    override fun clearDomainEvents() = domainEvents.clear()

    fun raiseDomainEvent(domainEvent: DomainEvent) {
        domainEvents.add(domainEvent)
    }
}
