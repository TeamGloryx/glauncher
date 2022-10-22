package net.gloryx.glauncher.util.state

import androidx.compose.runtime.derivedStateOf
import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.BCrypt.Hasher
import cat.ui.dlg.*
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.launch
import net.gloryx.glauncher.logic.auth.NotAuthenticatedException
import net.gloryx.glauncher.ui.auth.Microsoft
import net.gloryx.glauncher.util.db.DB
import net.gloryx.glauncher.util.db.sql.AuthTable
import net.gloryx.glauncher.util.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object AuthState {
    var authDialog by State(false)

    var ign by MaybeState<String>()

    var hash by MaybeState<String>()

    var premium by State(false)

    var accessToken by MaybeState<String>()

    val isAuthenticated by derivedStateOf { ign != null }

    val hasher: Hasher = BCrypt.withDefaults()
    val verifier = BCrypt.verifyer()

    fun getUUID(ign: String? = AuthState.ign): UUID {
        if (!isAuthenticated) throw NotAuthenticatedException()
        var id = UUID.randomUUID()
        val uuids = DB.users.associate { it.nickname to it.uuid }

        if (ign != null && uuids.containsKey(ign)) {
            return uuids[ign]!!
        }

        while (uuids.containsValue(id)) {
            id = UUID.randomUUID()
        }

        return id
    }

    fun logIn(isPrem: Boolean, pwd: String, currIgn: String?) {
        if (Static.doAuth) {
            val pr = transaction(DB.Sql.db) {
                AuthTable.select(AuthTable.premiumUuid neq null and (AuthTable.nickname eq currIgn!!))
                    .firstOrNull()
            }
            if (isPrem) {
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

                    accessToken = at.value
                }
            } else {
                if (pr != null) throw NotAuthenticatedException("Log in as premium!")
                val hash = hasher.hashToString(10, pwd.toCharArray())
                val currHash = transaction(DB.Sql.db) {
                    AuthTable.select((AuthTable.nickname eq currIgn!!)).toList().also(::println)
                        .firstOrNull()
                        ?.get(AuthTable.hash).takeUnless { it.isNullOrBlank() }
                } ?: throw NotAuthenticatedException("Account \"$currIgn\" does not exist!")
                if (verifier.verify(pwd.toCharArray(), currHash.toCharArray()).verified) {
                    AuthState.hash = hash
                } else throw NotAuthenticatedException("Incorrect password!")
            }
        }
        ign = currIgn
        authDialog = false
        snackbar("Successfully logged in!")
    }
}