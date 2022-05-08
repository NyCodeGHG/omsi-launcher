package dev.nycode.omsilauncher.serialization

import dev.schlaubi.stdx.serialization.UUIDStringSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

typealias SerializableUUID = @Serializable(with = UUIDStringSerializer::class) UUID
