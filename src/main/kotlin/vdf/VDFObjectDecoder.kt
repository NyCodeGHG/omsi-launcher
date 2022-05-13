package dev.nycode.omsilauncher.vdf

import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.NamedValueDecoder
import java.util.LinkedList

class VDFObjectDecoder(private val vdf: VDF, private val obj: VDFObject) : NamedValueDecoder(), VDFDecoder {
    private val list = LinkedList(obj.toList())

    private var currentElement: VDFElement? = null

    override fun decodeTaggedValue(tag: String): VDFElement =
        obj[tag]?.let { currentElement ?: it } ?: throw SerializationException("Could not find value for tag: $tag")

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return if (list.isEmpty()) {
            CompositeDecoder.DECODE_DONE
        } else {
            val (name, element) = list.poll()
            currentElement = element
            descriptor.getElementIndex(name)
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        val currentObject = currentElement as? VDFObject ?: return super.beginStructure(descriptor)

        return VDFObjectDecoder(vdf, currentObject)
    }

    override fun decodeTaggedString(tag: String): String = decodeTaggedValue(tag).vdfString.content
    override fun decodeTaggedBoolean(tag: String): Boolean = decodeTaggedString(tag).toBooleanStrict()
    override fun decodeTaggedByte(tag: String): Byte = decodeTaggedString(tag).toByte()
    override fun decodeTaggedDouble(tag: String): Double = decodeTaggedString(tag).toDouble()
    override fun decodeTaggedFloat(tag: String): Float = decodeTaggedString(tag).toFloat()
    override fun decodeTaggedInt(tag: String): Int = decodeTaggedString(tag).toInt()
    override fun decodeTaggedLong(tag: String): Long = decodeTaggedString(tag).toLong()
    override fun decodeTaggedShort(tag: String): Short = decodeTaggedString(tag).toShort()
    override fun decodeTaggedNotNullMark(tag: String): Boolean =
        throw SerializationException("VDF does not support nulls")

    override fun decodeTaggedNull(tag: String): Nothing =
        throw SerializationException("VDF does not support nulls")

    override fun decodeTaggedChar(tag: String): Char {
        val sequence = decodeTaggedString(tag)
        if (sequence.length != 1) {
            throw SerializationException("'$sequence' is not a char")
        }

        return sequence.first()
    }
}
