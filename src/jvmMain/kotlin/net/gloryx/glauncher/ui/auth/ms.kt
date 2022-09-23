package net.gloryx.glauncher.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import cat.reflect.cast
import cat.try_
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView
import net.gloryx.glauncher.util.ComposeJFXPanel
import net.gloryx.glauncher.util.Secret
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile
import net.hycrafthd.minecraft_authenticator.login.Authenticator
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftAuthenticationFile
import netscape.javascript.JSObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object Microsoft {
    val url: URL =
        URL("https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize?client_id=${Secret.clientId}&response_type=code&scope=XboxLive.signin%20offline_access")
    var authCode: String? by mutableStateOf(null)
    var file: MicrosoftAuthenticationFile? by mutableStateOf(null)
    val auth by derivedStateOf {
        val a: Authenticator = (authCode?.let { Authenticator.ofMicrosoft(it) } ?: Authenticator.of(
            File(Secret.FILE).readText().let(AuthenticationFile::readString)
        )).shouldRetrieveXBoxProfile().build()!!
        try {
            a.run()
        } finally {
            try_ { a.resultFile }?.also { file = it.cast() }?.writeCompressed(FileOutputStream(File(Secret.FILE)))
        }
        a
    }

    @Composable
    @Suppress("nothing_to_inline")
    inline fun dialog() {
        var state by remember { mutableStateOf(true) }
        val jfxp = remember { JFXPanel() }
        var wdw = remember<JSObject?> { null }
        val di = rememberDialogState(size = DpSize(300.dp, 300.dp))

        if (state) Dialog({ state = false }, title = "Log in to your Microsoft account!", state = di) {
            Box(Modifier.fillMaxSize().background(Color.White)) {
                ComposeJFXPanel(window, jfxp, onCreate = {
                    Platform.runLater {
                        val root = WebView()
                        val engine = root.engine
                        val scene = Scene(root)
                        engine.loadWorker.stateProperty().addListener { _, _, newState ->
                            if (newState === Worker.State.SUCCEEDED) {
                                wdw = root.engine.executeScript("window") as JSObject
                            }
                        }
                        engine.loadWorker.exceptionProperty().addListener { _, _, newError ->
                            println("page load error : $newError")
                        }
                        jfxp.scene = scene
                        url.toString().let { engine.load(it); println(it) }
                        engine.locationProperty().addListener { _, _, loc ->
                            println(loc)
                            if (!loc.startsWith("https://login.live.com/oauth20_desktop.srf")) return@addListener

                            authCode =
                                loc.removePrefix("https://login.live.com/oauth20_desktop.srf?code=").also(::println)

                            println(file!!.refreshToken)
                            println(auth.user.get().accessToken)

                            state = false

                        }
                        engine.setOnError { error -> println("onError : $error") }
                    }
                }, onDestroy = {
                    Platform.runLater {
                        wdw?.let { _ -> }
                    }
                })
            }
        }
    }
}