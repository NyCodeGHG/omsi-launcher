package dev.nycode.omsilauncher.vdf

import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder

interface VDFEncoder : Encoder, CompositeEncoder
