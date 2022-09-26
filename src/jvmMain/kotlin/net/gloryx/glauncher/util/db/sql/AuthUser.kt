package net.gloryx.glauncher.util.db.sql

import java.util.UUID

data class AuthUser(val uuid: UUID, val ip: String, val hash: String, val nickname: String)
