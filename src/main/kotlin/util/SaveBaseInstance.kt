package dev.nycode.omsilauncher.util

import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.baseInstance

fun Instance.getBaseInstance(instances: List<Instance>): Instance = baseInstance?.let { id ->
    instances.firstOrNull { it.id == id }
} ?: instances.baseInstance
