package net.gloryx.glauncher.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.gloryx.glauncher.logic.auth.NotAuthenticatedException
import net.gloryx.glauncher.util.*
import net.gloryx.glauncher.util.db.DB
import net.gloryx.glauncher.util.db.sql.AuthTable
import net.gloryx.glauncher.util.res.lang.L
import net.gloryx.glauncher.util.state.AuthState
import net.gloryx.glauncher.util.state.MainScreen
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URL
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

@Composable
fun RealAuth() {
    var currIgn by remember { mutableStateOf(AuthState.ign) }
    var pwd by remember { mutableStateOf("") }

    currIgn = "NothinGG_"

    val (isPrem, setPrem) = remember { mutableStateOf(false) }

    MaterialTheme(Static.colors) {
        Box(Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
            Column {
                Text("Authenticate here.")
                TextField(currIgn ?: "", {
                    currIgn = if (it.isNotBlank())
                        it.take(64)
                    else null
                }, singleLine = true, isError = currIgn.isNullOrBlank(), label = { Text("IGN (In-game nickname)") })
                TextField(pwd, {
                    pwd = it
                }, singleLine = true, isError = pwd.isBlank(), label = { Text("Password") })

                Column {
                    Text("I am premium")
                    Checkbox(isPrem, setPrem, enabled = pwd.isEmpty())

                    if (isPrem) {
                        Microsoft.dialog()
                    }
                }

                fun getCanRegister() = transaction(DB.Sql.db) { AuthTable.select(AuthTable.nickname eq currIgn!!).empty() }
                var canRegister by remember { mutableStateOf(true) }

                Button({
                    if (Static.doAuth) {
                        val pr = transaction(DB.Sql.db) {
                            AuthTable.select(AuthTable.premiumUuid neq null and (AuthTable.nickname eq currIgn!!))
                                .firstOrNull()
                        }
                        if (isPrem && pr != null) {
                            val at = Microsoft.accessToken()
                            coro.launch {
                                val resp =
                                    ConfigFactory.parseString(fetch("https://api.minecraftservices.com/minecraft/profile") {
                                        header(
                                            "Authorization",
                                            "Bearer ${at.value}"
                                        )
                                    }.json())

                                if (resp.hasPath("error")) throw NotAuthenticatedException()

                                if (resp.getString("id") != pr[AuthTable.premiumUuid].toString()
                                        .replace("-", "")
                                ) println(pr)
                                println(resp)
                            }
                        } else {
                            val hash = AuthState.hasher.hashToString(10, pwd.toCharArray())
                            val currHash = transaction(DB.Sql.db) {
                                AuthTable.select((AuthTable.nickname eq currIgn!!)).toList().also(::println)
                                    .firstOrNull()
                                    ?.get(AuthTable.hash).takeUnless { it.isNullOrBlank() }
                            } ?: return@Button println("Nope")
                            if (AuthState.verifier.verify(pwd.toCharArray(), currHash.toCharArray()).verified) {
                                AuthState.hash = hash
                            } else return@Button println("Nope")
                        }
                    }
                    AuthState.ign = currIgn
                    AuthState.authDialog = false
                    snackbar("Successfully logged in!")
                }, enabled = currIgn != null && (pwd.isNotBlank() || isPrem) && !canRegister) {
                    Text("Log in")
                }

                Button({
                    if (!getCanRegister()) return@Button run { canRegister = false }
                    transaction(DB.Sql.db) {
                        AuthTable.insert {
                            it[nickname] = currIgn!!
                            it[lowercaseNickname] = currIgn!!.lowercase()
                            it[hash] = AuthState.hasher.hashToString(10, pwd.toCharArray())
                            it[uuid] = AuthState.getUUID(currIgn!!)
                            it[registrationDate] = Instant.now().toEpochMilli()
                            coro.launch {
                                it[ip] =
                                    fetch("https://api.myip.com").json().let(ConfigFactory::parseString).getString("ip")
                            }
                        }
                    }
                    AuthState.ign = currIgn
                    AuthState.authDialog = false
                    snackbar("Successfully registered!")
                }, enabled = currIgn != null && pwd.isNotBlank() && !isPrem) {
                    Text("Register")
                }
            }
        }
    }
}