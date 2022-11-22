package ie.setu.repository

import ie.setu.domain.MeasurementDTO

import ie.setu.domain.db.Measurements

import ie.setu.domain.repository.MeasurementDAO
import ie.setu.helpers.measurements
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

val measurement1 = measurements.get(0)
val measurement2 = measurements.get(1)
val measurement3 = measurements.get(2)

internal fun populateMeasurementssTable(): MeasurementDAO {
    SchemaUtils.create(Measurements)
    val measurementdao = MeasurementDAO()
    measurementdao.save(measurement1)
    measurementdao.save(measurement2)
    measurementdao.save(measurement3)
    return measurementdao
}

class MeasurementDAOTest {

    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class ReadMeasurements{

        @Test
        fun `getting all measurements Tracked from a populated table returns all rows`(){
            transaction {
                val measurementsdao = populateMeasurementssTable()
                assertEquals(3,measurementsdao.getAll().size)
            }
        }

        @Test
        fun `get measurement tracked by id that doesn't exist, results in no measurements tracked returned`(){
            transaction {
                val measurementdao = populateMeasurementssTable()
                assertEquals(null,measurementdao.findByUserId(4))
            }
        }

        @Test
        fun `get measurement tracked by id that exists, results of a correct measurement tracked returned`(){
            transaction {
                val measurementdao = populateMeasurementssTable()
                assertEquals(measurement1,measurementdao.findByUserId(1))
            }
        }

        @Test
        fun `get all measurements tracked over empty table returns none`(){
            transaction {
                SchemaUtils.create(Measurements)
                val measurementdao = MeasurementDAO()
                assertEquals(0,measurementdao.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteMeasurements{

        @Test
        fun `deleting a non-existent measurement in table results in no deletion`(){
            transaction {
                val measurementdao = populateMeasurementssTable()
                assertEquals(3,measurementdao.getAll().size)
                measurementdao.delete(4)
                assertEquals(3,measurementdao.getAll().size)
            }
        }

        @Test
        fun `deleting an existing measurement in table results in record being deleted`(){
            transaction {
                val measurementdao = populateMeasurementssTable()
                assertEquals(3,measurementdao.getAll().size)
                measurementdao.delete(2)
                assertEquals(2,measurementdao.getAll().size)
            }
        }
    }

    @Nested
    inner class UpdateMeasurements{

        @Test
        fun `updating existing measurements in table results in successful update`(){
            transaction {
                val measurementdao = populateMeasurementssTable()
                val measurement2updated = MeasurementDTO(2,75.0,72.0,13.0)
                measurementdao.update(2,measurement2updated)
                measurementdao.findByUserId(2)?.let { assertEquals(72.0, it.height) }
            }
        }

        @Test
        fun `updating non-existent measurements in table results in no updates`(){
            transaction {
                val measurementdao = populateMeasurementssTable()
                val measuremnt4updated = MeasurementDTO(4,75.0,72.0,13.0)
                measurementdao.update(4,measuremnt4updated)
                assertEquals(null,measurementdao.findByUserId(4))
            }
        }

    }

}