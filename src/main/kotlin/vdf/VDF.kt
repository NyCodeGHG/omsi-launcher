package dev.nycode.omsilauncher.vdf

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

private const val tab = "\t"
private const val delimiter = "\t\t"

open class VDF internal constructor(override val serializersModule: SerializersModule = EmptySerializersModule) :
    StringFormat {

    fun decodeToVDFObject(string: String) = decodeToVDFObject(VDFLineParser(string.lines()), false)

    fun <T> decodeFromVDFObject(deserializer: DeserializationStrategy<T>, element: VDFObject): T {
        val decoder = VDFObjectDecoder(this, element)
        return decoder.decodeSerializableValue(deserializer)
    }

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T =
        decodeFromVDFObject(deserializer, decodeToVDFObject(string))

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String =
        encodeFromVDFElement(encodeToVDFElement(serializer, value))

    fun <T> encodeToVDFElement(serializer: SerializationStrategy<T>, value: T): VDFObject {
        val encoder = VDFObjectEncoder(this)
        encoder.encodeSerializableValue(serializer, value)
        return encoder.toElement()
    }

    fun encodeFromVDFElement(element: VDFElement) = encode(element, 0, true)

    private fun decodeToVDFObject(parser: VDFLineParser, isChildDecode: Boolean): VDFObject {
        @Suppress("RemoveExplicitTypeArguments") // inference doesn't work for some reason
        val map = buildMap<String, VDFElement> {
            while (parser.hasNext()) {
                val line = parser.next().trim { it.isWhitespace() }.split(delimiter)
                if (line[0].startsWith("\"") && line[0].endsWith("\"")) {
                    if (line.size == 2) {
                        put(line[0].removeQuotes(), VDFPrimitive(line[1].removeQuotes()))
                    } else if (line.size == 1) {
                        put(line[0].removeQuotes(), decodeToVDFObject(parser, true))
                    }
                } else if (line[0].contains("}") && isChildDecode) {
                    break // return currentObject
                }
            }
        }

        return VDFObject(map)
    }

    private fun encode(element: VDFElement, indent: Int, isRoot: Boolean): String = buildString {
        when (element) {
            is VDFPrimitive -> appendLine(element.content.enquote())
            is VDFObject -> append(encode(element, indent, isRoot))
        }
    }

    private fun encode(obj: VDFObject, indent: Int, isRoot: Boolean) = buildString {
        appendLine()
        if (!isRoot) {
            appendIndent(indent - 1)
            appendLine('{')
        }
        obj.forEach { key, value ->
            appendIndent(indent)
            append(key.enquote())
            append(delimiter)
            append(encode(value, indent + 1, false))
        }
        if (!isRoot) {
            appendIndent(indent - 1)
            appendLine('}')
        }
    }

    companion object Default : VDF(EmptySerializersModule)
}

private class VDFLineParser(private val lines: List<String>) : Iterator<String> {
    private var index = 0

    override fun next(): String = lines[index++]

    override fun hasNext(): Boolean = index < lines.size
}

private fun String.removeQuotes(): String = trim().run { substring(1, lastIndex) }
private fun String.enquote() = """"$this""""

private fun StringBuilder.appendIndent(amount: Int) = append(tab.repeat(amount))
