package microarch.delivery.core.domain.model

data class Volume(val value: Double) : Comparable<Volume> {

    companion object {
        private const val MIN_VALUE = 0.0
        private const val MAX_VALUE = 20.0
        val MAX_VOLUME = Volume(MAX_VALUE)
    }

    init {
        require(value > MIN_VALUE) { "volume must be greater than $MIN_VALUE, but was $value" }
    }

    fun add(volume: Volume): Volume = Volume(this.value + volume.value)

    override fun compareTo(other: Volume): Int = this.value.compareTo(other.value)
}
