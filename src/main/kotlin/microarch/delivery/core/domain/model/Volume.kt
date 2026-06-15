package microarch.delivery.core.domain.model

data class Volume(val value: Double) {

    companion object {
        private const val MIN_VALUE = 0.0
    }

    init {
        require(value > MIN_VALUE) { "volume must be greater than $MIN_VALUE, but was $value" }
    }
}
