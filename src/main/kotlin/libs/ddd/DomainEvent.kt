package libs.ddd

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.context.ApplicationEvent
import java.time.Instant
import java.util.UUID

abstract class DomainEvent : ApplicationEvent {
    val eventId: UUID = UUID.randomUUID()
    val occurredOnUtc: Instant = Instant.now()

    constructor(source: Any) : super(source)

    protected constructor() : super("default")

    @JsonIgnore
    override fun getSource(): Any = super.getSource()
}
