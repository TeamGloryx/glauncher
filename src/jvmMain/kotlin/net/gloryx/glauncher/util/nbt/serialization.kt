package net.gloryx.glauncher.util.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.overwriteWith
import org.jetbrains.annotations.ApiStatus.Internal
import org.jglrxavpok.hephaistos.nbt.NBTReader
import org.jglrxavpok.hephaistos.nbt.NBTWriter
import org.jglrxavpok.hephaistos.parser.SNBTParser
import cat.b

object NBT {
    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        serializersModule = serializersModule.overwriteWith(SerializersModule {
            contextual(Boolean::class, ByteBoolSerializer)
        })
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }

    @Internal
    fun decode(reader: NBTReader) = json.parseToJsonElement(reader.read().toSNBT())

    @Internal
    fun encode(element: JsonElement) = SNBTParser(element.toString().reader()).use { it.parse() }


    /**
     * **Note** It is caller's responsibility to close the [reader]
     */
    inline fun <reified T> read(reader: NBTReader): T = json.decodeFromJsonElement(decode(reader))

    /**
     * **Note** It is caller's responsibility to close the [writer]
     */
    inline fun <reified T> write(value: T, writer: NBTWriter, name: String = "") = writer.writeNamed(name, encode(json.encodeToJsonElement(value)))

    /**
     * A helper function that automatically closes the [writer].
     */
    inline fun <reified T> useWrite(writer: NBTWriter, value: T, name: String = "") = writer.use { write(value, it, name) }

    /**
     * A helper function that automatically closes the [reader].
     */
    inline fun <reified T> useRead(reader: NBTReader): T = reader.use { read(it) }
}

inline fun <reified T> NBTWriter.write(value: T) = NBT.useWrite(this, value)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(Boolean::class)
object ByteBoolSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("boolean_byte", PrimitiveKind.BYTE)

    override fun deserialize(decoder: Decoder): Boolean = decoder.decodeByte() != 0.b
    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeByte(if (value) 0b1 else 0b0)
    }
}