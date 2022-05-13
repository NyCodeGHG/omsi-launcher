package dev.nycode.omsilauncher.vdf

import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.NamedValueEncoder
import kotlinx.serialization.modules.SerializersModule

internal class VDFObjectEncoder(private val vdf: VDF, private val parent: VDFObjectEncoder? = null) :
    NamedValueEncoder(), VDFEncoder {
    private val elements = mutableMapOf<String, VDFElement>()

    private var structureEncoder: VDFObjectEncoder? = null

    override val serializersModule: SerializersModule get() = vdf.serializersModule

    override fun encodeNull() = throw SerializationException("VDF does not support null")

    override fun encodeTaggedValue(tag: String, value: Any) {
        elements += tag to VDFPrimitive(value.toString())
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return if (currentTagOrNull == null) {
            super.beginStructure(descriptor)
        } else {
            require(structureEncoder == null) { "Cannot decode two structures at the same time" }
            structureEncoder = VDFObjectEncoder(vdf, this)
            structureEncoder!!
        }
    }

    override fun endEncode(descriptor: SerialDescriptor) {
        super.endEncode(descriptor)
        if (parent != null) {
            parent.structureEncoder = null
            if (parent.currentTagOrNull != null) {
                parent.elements += parent.currentTag to toElement()
                // For some reason the structure tag needs to get removed manually here
                parent.popTag()
            }
        }
    }

    fun toElement() = VDFObject(elements)
}
