package net.gloryx.glauncher.util.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object Auth {
    var authDialog by mutableStateOf(false)

    var ign by mutableStateOf("Steve")

    var isAuthenticated by mutableStateOf(true)
}