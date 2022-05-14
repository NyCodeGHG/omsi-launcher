package dev.nycode.omsilauncher.util

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.platform.win32.WinReg
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.div

fun getSteamInstallPath(): Path = getSteamInstallPathOrNull() ?: error("Steam is not installed")
fun getSteamInstallPathOrNull(): Path? =
    getRegistryPathOrNull("""SOFTWARE\WOW6432Node\Valve\Steam""", "InstallPath")

fun getSteamExecutable(): Path = getSteamInstallPath() / "steam.exe"

fun getOmsiInstallPath(): Path = getOmsiInstallPathOrNull() ?: error("Omsi is not installed")
fun getOmsiInstallPathOrNull(): Path? =
    getRegistryPathOrNull("""SOFTWARE\WOW6432Node\aerosoft\OMSI 2""", "Product_Path")

fun getOmsiSteamLibrary(): Path = getOmsiInstallPath().parent

private fun getRegistryPathOrNull(key: String, value: String) = try {
    Path(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, key, value)).absolute()
} catch (e: Win32Exception) {
    null
}
