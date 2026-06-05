package libs.ddd

interface AggregateRoot<ID> {
    fun getDomainEvents(): List<DomainEvent>
    fun clearDomainEvents()
}
