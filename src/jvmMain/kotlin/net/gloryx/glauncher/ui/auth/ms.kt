package net.gloryx.glauncher.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import cat.async.await
import cat.ui.Suspend
import cat.ui.Suspense
import cat.ui.dlg.*
import catfish.winder.colors.Green200
import com.microsoft.aad.msal4j.*
import me.nullicorn.msmca.minecraft.MinecraftAuth
import me.nullicorn.msmca.minecraft.MinecraftToken
import net.gloryx.glauncher.util.*
import net.gloryx.glauncher.util.state.AuthState
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.URI
import kotlin.time.Duration.Companion.milliseconds

@Suppress("nothing_to_inline")
object Microsoft {
    const val authority = "https://login.microsoftonline.com/consumers/"
    val throttling = MaybeState<Long>(null)
    var authCode by MaybeState<String>(null)
    val codeState = MaybeState<DeviceCode>(null)
    var code by codeState

    @Composable
    inline fun dialog() {
        var state by useState(true)
        val di = rememberDialogState(size = DpSize(300.dp, 300.dp))

        if (state) Dialog({ state = false }, title = "Log in to your Microsoft account!", state = di) {
            MaterialTheme(Static.colors) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                    Column {
                        if (authCode == null) {
                            Suspend {
                                acquireToken()
                            }
                            Suspense(codeState, {
                                throttling.render {
                                    Text(
                                        """
                                        You're kinda lucky...
                                        Well, you have reached the TIMEOUT😈!
                                        Please wait for ${it.milliseconds}.
                                        """
                                    )
                                } ?: Text(
                                    "Please just wait..."
                                )
                            }) { dc ->
                                Column {
                                    SelectionContainer(
                                        Modifier.align(Alignment.CenterHorizontally).border(3.dp, Green200)
                                    ) {
                                        Text(dc.userCode(), textAlign = TextAlign.Center, fontSize = TextUnit(30))
                                    }
                                    Spacer(5.dp)
                                    Button({
                                        val ss = StringSelection(dc.userCode())
                                        Toolkit.getDefaultToolkit().systemClipboard.setContents(ss, ss)

                                        Desktop.getDesktop().browse(URI(dc.verificationUri()))
                                    }, Modifier.align(Alignment.Start)) {
                                        Text("Copy the code and log in")
                                    }
                                }
                            }
                        } else {
                            state = false
                            AuthState.logIn(true, "", currIgn.value)
                        }
                    }
                }
            }
        }
    }


    @PublishedApi
    internal suspend inline fun acquireToken() {
        val cli = PublicClientApplication.builder(Secret.clientId).authority(authority).setTokenCacheAccessAspect(Cache)
            .build()
        val cached = cli.accounts.await()
        val account = cached.firstOrNull()

        val result: IAuthenticationResult = try {
            val sp = SilentParameters.builder(Secret.SCOPE, account).forceRefresh(true).authorityUrl(authority).build()

            cli.acquireTokenSilently(sp).await()
        } catch (rethrow: Exception) {
            val c = rethrow.cause // no getter for you, dirty throwable
            if (c is MsalThrottlingException) {
                throttling.value = c.retryInMs()
                return
            }
            if (c is MsalException || account == null) {
                val consume: (DeviceCode) -> Unit =
                    { code = it }
                val dfp = DeviceCodeFlowParameters.builder(Secret.SCOPE, consume).build()

                cli.acquireToken(dfp).await()
            } else {
                throw rethrow
            }
        }



        authCode = result.accessToken()
    }

    inline fun accessToken(): MinecraftToken {
        val ms = authCode!!
        val mc = MinecraftAuth()

        return mc.loginWithMicrosoft(ms)
    }

    object Cache : ITokenCacheAccessAspect {
        private val file = Static.root.resolve(".strangeness/.inliner/.sh").also(File::mkdirs).resolve(".d.snow")
            .also(File::createNewFile)
        private var data by this.file

        override fun afterCacheAccess(ac: ITokenCacheAccessContext) {
            ac.tokenCache().deserialize(data)
        }

        override fun beforeCacheAccess(ac: ITokenCacheAccessContext) {
            data = ac.tokenCache().serialize()
        }
    }
}