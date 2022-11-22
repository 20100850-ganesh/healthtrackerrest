package ie.setu.domain.repository

import ie.setu.config.DbConfig
import ie.setu.domain.MeasurementDTO
import ie.setu.domain.db.Measurements
import ie.setu.domain.db.Users
import ie.setu.utils.mapToMeasurementDTO
import ie.setu.utils.mapToUser
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class MeasurementDAO {
    private val logger = KotlinLogging.logger {}
    //Get all the Measurement items in the database regardless of user id
    fun getAll(): ArrayList<MeasurementDTO> {
        val measurementsList: ArrayList<MeasurementDTO> = arrayListOf()
        //val dbconf = DbConfig()
        //dbconf.getDbConnection()

        transaction() {

            Measurements.selectAll().map {
                measurementsList.add(mapToMeasurementDTO(it)) }
        }

        return measurementsList
    }

    fun save(measurementData: MeasurementDTO) {
        transaction {
            Measurements.insert {
                it[height] = measurementData.height
                it[weight] = measurementData.weight
                it[bmi] = measurementData.bmi
            }
        }
    }

    fun findByUserId(userId: Int): MeasurementDTO? {
        return transaction {
            Measurements.select() {
                Measurements.userid eq userId}
                .map{ mapToMeasurementDTO(it) }
                .firstOrNull()
        }
    }

    fun delete(userID: Int) {
        return transaction{
            Measurements.deleteWhere{
                Measurements.userid eq userID
            }
        }
    }

    fun update(userid: Int, measurements: MeasurementDTO) {
        transaction {
            Measurements.update ({
                Measurements.userid eq userid}) {
                it[height] = measurements.height
                it[weight] = measurements.weight
                it[bmi] = measurements.bmi
            }
        }
    }

}