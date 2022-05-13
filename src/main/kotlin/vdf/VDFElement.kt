package dev.nycode.omsilauncher.vdf

sealed class VDFElement

data class VDFObject(private val content: Map<String, VDFElement>) : VDFElement(), Map<String, VDFElement> by content

sealed class VDFPrimitive : VDFElement() {
    abstract val isString: Boolean
    abstract val content: String

    override fun toString(): String = content
}

fun VDFPrimitive(string: String): VDFPrimitive = VDFString(string)

data class VDFString(override val content: String) : VDFPrimitive() {
    override val isString: Boolean = true
    override fun toString(): String = content
}

val VDFElement.vdfObject: VDFObject
    get() = this as? VDFObject ?: error("Cannot cast ${this::class::simpleName} to object")

val VDFElement.vdfPrimitive: VDFPrimitive
    get() = this as? VDFPrimitive ?: error("Cannot cast ${this::class::simpleName} to primitive")

val VDFElement.vdfString: VDFString
    get() = this as? VDFString ?: error("Cannot cast ${this::class::simpleName} to string")
