package net.gloryx.glauncher.util.db.sql

import org.jetbrains.exposed.sql.Table

object AuthTable : Table() {
    val nickname = varchar("nickname", 64)
    val lowercaseNickname = varchar("lowercasenickname", 64)
    val hash = varchar("hash", 64)
    val ip = varchar("ip", 16)
    val registrationDate = long("regdate")
    val uuid = uuid("uuid")
}