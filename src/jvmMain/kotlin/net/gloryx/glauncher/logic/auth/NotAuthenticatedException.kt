package net.gloryx.glauncher.logic.auth

import net.gloryx.glauncher.util.state.AuthState

class NotAuthenticatedException : Exception("User ${AuthState.ign} is not authenticated.")