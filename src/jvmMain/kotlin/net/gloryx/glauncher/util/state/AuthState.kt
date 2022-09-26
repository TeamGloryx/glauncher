package net.gloryx.glauncher.util.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.BCrypt.Hasher

object AuthState {
    var authDialog by mutableStateOf(false)

    var ign by mutableStateOf<String?>(null)

    var hash by mutableStateOf<String?>(null)

    val isAuthenticated by derivedStateOf { ign != null }

    val hasher: Hasher = BCrypt.withDefaults()
    val verifier = BCrypt.verifyer()
}