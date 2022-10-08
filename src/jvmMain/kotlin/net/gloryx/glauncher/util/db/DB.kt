package net.gloryx.glauncher.util.db

import net.gloryx.glauncher.util.db.sql.AuthTable
import net.gloryx.glauncher.util.db.sql.AuthTable.hash
import net.gloryx.glauncher.util.db.sql.AuthTable.ip
import net.gloryx.glauncher.util.db.sql.AuthTable.nickname
import net.gloryx.glauncher.util.db.sql.AuthTable.uuid
import net.gloryx.glauncher.util.db.sql.AuthUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DB {
    object Sql {
        val db = Database.connect("jdbc:mysql://88.198.32.250:3306/s1_auth", user = "u1_O4iRUkk7YG", password = "kP+lTz+OSH8nHENdT+7PpFDW")
    }

    val users get() = transaction(Sql.db) {
        AuthTable.selectAll().map { AuthUser(it[uuid], it[ip], it[hash], it[nickname]) }
    }
}