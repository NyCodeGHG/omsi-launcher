package dev.nycode.omsilauncher.vdf

import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder

interface VDFDecoder : Decoder, CompositeDecoder
