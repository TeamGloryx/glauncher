package net.gloryx.glauncher.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import cat.async.await
import com.microsoft.aad.msal4j.DeviceCode
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters
import com.microsoft.aad.msal4j.IAuthenticationResult
import com.microsoft.aad.msal4j.ITokenCacheAccessAspect
import com.microsoft.aad.msal4j.ITokenCacheAccessContext
import com.microsoft.aad.msal4j.MsalException
import com.microsoft.aad.msal4j.PublicClientApplication
import com.microsoft.aad.msal4j.SilentParameters
import me.nullicorn.msmca.minecraft.MinecraftAuth
import me.nullicorn.msmca.minecraft.MinecraftToken
import net.gloryx.glauncher.util.*
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.URI

@Suppress("nothing_to_inline")
object Microsoft {
    const val authority = "https://login.microsoftonline.com/consumers/"
    var authCode: String? by mutableStateOf(null)
    var code: DeviceCode? by mutableStateOf(null)

    @Composable
    inline fun dialog() {
        var state by remember { mutableStateOf(true) }
        val di = rememberDialogState(size = DpSize(300.dp, 300.dp))

        if (state) Dialog({ state = false }, title = "Log in to your Microsoft account!", state = di) {
            MaterialTheme(Static.colors) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                    Column {
                        if (authCode == null) {
                            Suspense(
                                mutableStateOf<IAuthenticationResult?>(null).also { cat.ui.Async { acquireToken(it).let { authCode = it.accessToken() } } },
                                {
                                    Text(
                                        "Please wait......",
                                        color = MaterialTheme.colors.onBackground
                                    ); println("fallback")
                                }) {
                                val dc = code!!
                                SelectionContainer {
                                    Text(dc.userCode(), textAlign = TextAlign.Center)
                                }
                                Button({
                                    val ss = StringSelection(dc.userCode())
                                    Toolkit.getDefaultToolkit().systemClipboard.setContents(ss, ss)

                                    Desktop.getDesktop().browse(URI(dc.verificationUri()))
                                }) {
                                    Text("Copy the code and log in")
                                }
                            }
                        } else state = false

                    }
                }
            }
        }
    }


    @PublishedApi
    internal suspend inline fun acquireToken(state: MutableState<IAuthenticationResult?>): IAuthenticationResult {
        val cli = PublicClientApplication.builder(Secret.clientId)
            .authority(authority)
            .setTokenCacheAccessAspect(Cache)
            .build()
        val cached = cli.accounts.await()
        val account = cached.firstOrNull()

        val result: IAuthenticationResult = try {
            val sp = SilentParameters.builder(Secret.SCOPE, account).forceRefresh(true).authorityUrl(authority).build()

            cli.acquireTokenSilently(sp).await()
        } catch (rethrow: Exception) {
            if (rethrow.cause is MsalException || account == null) {
                val consume: (DeviceCode) -> Unit = { Static.out.println(it.userCode()); Static.out.println(it.verificationUri()); code = it }
                val dfp = DeviceCodeFlowParameters.builder(Secret.SCOPE, consume).build()

                cli.acquireToken(dfp).await()
            } else {
                throw rethrow
            }
        }

        state.value = result



        return result
    }

    inline fun accessToken(): MinecraftToken {
        val ms = authCode!!
        val mc = MinecraftAuth()

        return mc.loginWithMicrosoft(ms)
    }

    object Cache : ITokenCacheAccessAspect {
        private val file = Static.root.resolve(".strangeness/.inliner/.sh").also(File::mkdirs).resolve(".d.snow")
            .also(File::createNewFile)
        var data by this.file

        override fun afterCacheAccess(ac: ITokenCacheAccessContext) {
            ac.tokenCache().deserialize(data)
            println("cache hit - write")
        }

        override fun beforeCacheAccess(ac: ITokenCacheAccessContext) {
            data = ac.tokenCache().serialize()
            println("cache hit - read")
        }
    }
}