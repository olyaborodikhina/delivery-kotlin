package libs.ddd

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity<TId : Comparable<TId>> : Comparable<BaseEntity<TId>> {

    @Id
    @Column(name = "id")
    protected var id: TId? = null

    protected constructor()

    protected constructor(id: TId) {
        this.id = id
    }

    protected fun isTransient(): Boolean = id == null || id == defaultValue()

    protected open fun defaultValue(): TId? = null

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is BaseEntity<*>) return false
        if (this.javaClass != other.javaClass) return false
        if (this.isTransient() || other.isTransient()) return false
        return id == other.id
    }

    override fun hashCode(): Int = (javaClass.name + (id?.toString() ?: "")).hashCode()

    override fun compareTo(other: BaseEntity<TId>): Int {
        if (other === this) return 0
        return id!!.compareTo(other.id!!)
    }
}
