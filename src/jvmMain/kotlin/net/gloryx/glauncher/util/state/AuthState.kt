package net.gloryx.glauncher.util.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.BCrypt.Hasher
import net.gloryx.glauncher.logic.auth.NotAuthenticatedException
import net.gloryx.glauncher.util.db.DB
import java.util.*

object AuthState {
    var authDialog by mutableStateOf(false)

    var ign by mutableStateOf<String?>(null)

    var hash by mutableStateOf<String?>(null)

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
}