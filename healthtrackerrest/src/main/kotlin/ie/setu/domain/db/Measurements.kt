package ie.setu.domain.db

import ie.setu.domain.db.Users.autoIncrement
import ie.setu.domain.db.Users.primaryKey
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Measurements : Table("measurements") {

    val userid = integer("userid").autoIncrement().primaryKey()
    val weight = double("weight")
    val height = double("height")
    val bmi = double("bmi")
}