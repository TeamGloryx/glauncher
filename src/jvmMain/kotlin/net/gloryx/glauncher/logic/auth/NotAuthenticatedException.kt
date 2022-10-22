package net.gloryx.glauncher.logic.auth

class NotAuthenticatedException(message: String? = null) : Exception(message ?: "Wrong credentials!")