package net.gloryx.glauncher.util.db

import net.gloryx.glauncher.util.db.sql.AuthTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DB {
    object Sql {
        val db = Database.connect("jdbc:mysql://u1_O4iRUkk7YG:kP+lTz+OSH8nHENdT+7PpFDW@88.198.32.250:3306/s1_auth")
    }
}