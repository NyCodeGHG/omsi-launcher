package dev.nycode.omsilauncher.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.lyricist.LocalStrings
import com.vdurmont.semver4j.Semver
import dev.nycode.omsilauncher.build.BuildConfig
import dev.nycode.omsilauncher.config.config
import dev.schlaubi.stdx.logging.logger
import dev.schlaubi.stdx.logging.warnInlined
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.awt.Desktop
import java.net.URI

private val REPO_API_URL = Url("https://api.github.com/repos")
private val LOG = logger()

@Serializable
private data class Release(
    @SerialName("html_url")
    val url: String,
    @SerialName("tag_name")
    val tagName: String,
    val assets: List<Asset>,
    val prerelease: Boolean,
    val body: String
) {
    val releaseVersion: String
        get() = tagName.drop(1) // Tags start with a v, so we drop that v to get 1.2.3 instead of v1.2.3

    val installer: Asset
        get() = assets.first { it.name.endsWith(".msi") }

    @Serializable
    data class Asset(
        @SerialName("browser_download_url")
        val url: String,
        val name: String
    )
}

@Composable
private fun rememberHttpClient() = remember {
    HttpClient {
        install(ContentNegotiation) {
            val json = Json {
                ignoreUnknownKeys = true
            }

            json(json)
        }
    }
}

private suspend fun HttpClient.fetchPossiblyNewerRelease(): Release? {
    val response = get(REPO_API_URL) {
        url {
            appendPathSegments(BuildConfig.REPOSITORY.split('/'))
            appendPathSegments("releases")
        }
    }
    if (!response.status.isSuccess()) {
        LOG.warnInlined { "There was an error whilst contacting GitHub API: ${response.bodyAsText()}" }
        return null
    }

    val allReleases = response.body<List<Release>>()
    LOG.debug { "Fetching GitHub Releases: $allReleases" }

    val latestRelease = allReleases.firstOrNull { !it.prerelease } // try fetch stable release
        ?: allReleases.firstOrNull() // if there are no stable releases
        ?: return null // if there is no stable releases

    val currentVersion = Semver(BuildConfig.APP_VERSION)
    val latestVersion = Semver(latestRelease.releaseVersion)

    return latestRelease
        .takeIf { currentVersion < latestVersion }
}

@Composable
fun VersionChecker() {
    if (config.checkForUpdates) {
        val scope = rememberCoroutineScope()
        val client = rememberHttpClient()
        var newRelease by remember { mutableStateOf<Release?>(null) }
        var dismissed by remember { mutableStateOf(false) }
        val strings = LocalStrings.current

        if (newRelease == null) {
            DisposableEffect(Unit) {
                scope.launch {
                    newRelease = client.fetchPossiblyNewerRelease()
                }
                onDispose { client.close() }
            }
        } else if (!dismissed) {
            Dialog({ dismissed = true }, title = strings.newVersionAvailableTitle) {
                val release = newRelease!!
                Box(Modifier.fillMaxSize()) {
                    Column(Modifier.align(Alignment.Center)) {
                        Text(
                            strings.newVersionDescription(release.releaseVersion),
                            Modifier.padding(20.dp),
                            textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold
                        )
                        Button({
                            Desktop.getDesktop().browse(URI(release.url))
                        }, Modifier.align(Alignment.CenterHorizontally)) {
                            Text(strings.viewReleaseOnGithub)
                        }
                    }
                }
            }
        }
    }
}
