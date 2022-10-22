package net.gloryx.glauncher.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import catfish.winder.colors.*
import cat.ui.dlg.*
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.launch
import net.gloryx.glauncher.logic.auth.NotAuthenticatedException
import net.gloryx.glauncher.util.*
import net.gloryx.glauncher.util.db.DB
import net.gloryx.glauncher.util.db.sql.AuthTable
import net.gloryx.glauncher.util.res.lang.L
import net.gloryx.glauncher.util.state.AuthState
import net.kyori.adventure.util.TriState
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

@Composable
fun AuthDialog() {
    if (AuthState.authDialog) {
        Dialog({
            AuthState.authDialog = false
        }, title = "${L.logIn}") {
            RealAuth()
        }
    }
}

var currIgn = State(AuthState.ign)
var nae by MaybeState<NotAuthenticatedException>()
val isSuccessful by derivedStateOf { nae == null }

@Composable
fun RealAuth() {
    var nowIgn by currIgn
    var pwd by useState("")

    val (isPrem, setPrem) = useState(false)

    MaterialTheme(Static.colors) {
        Box(Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
            Column {
                center {
                    Text("Authenticate here.", Modifier.align(Alignment.CenterHorizontally))
                    if (!isSuccessful) {
                        useTextStyle(Red800) {
                            Text("Error: ${nae?.message}", Modifier.align(Alignment.CenterHorizontally))
                        }
                    }
                }
                TextField(nowIgn ?: "", {
                    nowIgn = if (it.isNotBlank())
                        it.take(64)
                    else null
                }, singleLine = true, isError = nowIgn.isNullOrBlank(), label = { Text("IGN (In-game nickname)") })
                PasswordTextField(pwd, {
                    pwd = it
                }, singleLine = true, isError = pwd.isBlank(), label = { Text("Password") })

                Column {
                    Text("I am premium")
                    Checkbox(isPrem, setPrem, enabled = pwd.isEmpty())

                    if (isPrem) {
                        Microsoft.dialog()
                    }
                }

                fun getCanRegister() =
                    transaction(DB.Sql.db) { AuthTable.select(AuthTable.nickname eq nowIgn!!).empty() }

                var canRegister by useState(true)

                Button({
                    nae = runCatching {
                        AuthState.logIn(isPrem, pwd, nowIgn)
                    }.exceptionOrNull() as? NotAuthenticatedException
                }, enabled = nowIgn != null && (pwd.isNotBlank() || isPrem)) {
                    Text("Log in")
                }

                Button({
                    if (!getCanRegister()) return@Button run { canRegister = false }
                    transaction(DB.Sql.db) {
                        AuthTable.insert {
                            it[nickname] = nowIgn!!
                            it[lowercaseNickname] = nowIgn!!.lowercase()
                            it[hash] = AuthState.hasher.hashToString(10, pwd.toCharArray())
                            it[uuid] = AuthState.getUUID(nowIgn!!)
                            it[registrationDate] = Instant.now().toEpochMilli()
                            coro.launch {
                                it[ip] =
                                    fetch("https://api.myip.com").json().let(ConfigFactory::parseString).getString("ip")
                            }
                        }
                    }
                    AuthState.ign = nowIgn
                    AuthState.authDialog = false
                    snackbar("Successfully registered!")
                }, enabled = nowIgn != null && pwd.isNotBlank() && !isPrem) {
                    Text("Register")
                }
            }
        }
    }
}