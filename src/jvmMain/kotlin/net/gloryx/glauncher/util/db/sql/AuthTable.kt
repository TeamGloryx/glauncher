package net.gloryx.glauncher.util.db.sql

import org.jetbrains.exposed.sql.Table

object AuthTable : Table("AUTH") {
    val nickname = varchar("NICKNAME", 64)
    val lowercaseNickname = varchar("LOWERCASENICKNAME", 64)
    val hash = varchar("HASH", 80)
    val ip = varchar("IP", 16)
    val registrationDate = long("REGDATE")
    val uuid = uuid("UUID")
    val premiumUuid = uuid("PREMIUMUUID").nullable()
}