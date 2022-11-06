package ie.setu.repository

import ie.setu.controllers.CaloriesTrackerController.calculateCalories
import ie.setu.domain.CaloriesTrackerDC
import ie.setu.domain.db.CaloriesTracker
import ie.setu.domain.repository.CaloriesTrackerDAO
import ie.setu.helpers.caloriestrackers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

val caloriestracker1 = caloriestrackers.get(0)
val caloriestracker2 = caloriestrackers.get(1)
val caloriestracker3 = caloriestrackers.get(2)

 internal fun populateCaloriesTrackerTable(): CaloriesTrackerDAO {
    SchemaUtils.create(CaloriesTracker)
    val caloriestrackerdao = CaloriesTrackerDAO()
    caloriestrackerdao.save(caloriestracker1,calculateCalories(caloriestracker1))
    caloriestrackerdao.save(caloriestracker2, calculateCalories(caloriestracker2))
    caloriestrackerdao.save(caloriestracker3, calculateCalories(caloriestracker3))
    return caloriestrackerdao
    }

class CaloriesTrackerDaoTest {

   companion object {

      //Make a connection to a local, in memory H2 database.
      @BeforeAll
      @JvmStatic
      internal fun setupInMemoryDatabaseConnection() {
         Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
      }
   }

   @Nested
   inner class ReadCaloriesTracker{

      @Test
      fun `getting all calories Tracked from a populated table returns all rows`(){
         transaction {
            val caloriestrackerdao = populateCaloriesTrackerTable()

            assertEquals(3,caloriestrackerdao.getAll().size)
         }
      }

      @Test
      fun `get calories tracked by id that doesn't exist, results in no calories tracked returned`(){
         transaction {
            val caloriestrackerdao = populateCaloriesTrackerTable()
            assertEquals(null,caloriestrackerdao.findByUserID(4))
         }
      }

      @Test
      fun `get calories tracked by id that exists, results in a correct calories tracked returned`(){
         transaction {
            SchemaUtils.create(CaloriesTracker)
            val caloriestrackerdao = CaloriesTrackerDAO()
            caloriestrackerdao.save(caloriestracker1, calculateCalories(caloriestracker1))
            caloriestrackerdao.save(caloriestracker2, calculateCalories(caloriestracker2))
            caloriestrackerdao.save(caloriestracker3, calculateCalories(caloriestracker3))

            assertEquals(null,caloriestrackerdao.findByUserID(4))

         }
      }

      @Test
      fun `get all calories tracked over empty table returns none`(){
         transaction {
            SchemaUtils.create(CaloriesTracker)
            val caloriestrackerdao = CaloriesTrackerDAO()

            assertEquals(0,caloriestrackerdao.getAll().size)
         }
      }
   }

   @Nested
   inner class CreateCaloriesTracker{

      @Test
      fun `multiple calories tracked added to table can be retrieved successfully`(){
         transaction {
            val caloriestrackerdao = populateCaloriesTrackerTable()

            assertEquals(3,caloriestrackerdao.getAll().size)
            assertEquals(caloriestracker1,caloriestrackerdao.findByUserID(1))
            assertEquals(caloriestracker2,caloriestrackerdao.findByUserID(2))
            assertEquals(caloriestracker3,caloriestrackerdao.findByUserID(3))
         }
      }
   }

   @Nested
   inner class DeleteCaloriesTracked{

      @Test
      fun `deleting a non-existent calories tracked in table results in no deletion`(){
         transaction {
            val caloriestrackerdao = populateCaloriesTrackerTable()
            assertEquals(3,caloriestrackerdao.getAll().size)
            caloriestrackerdao.delete(4)
            assertEquals(3,caloriestrackerdao.getAll().size)
         }
      }

      @Test
      fun `deleting an existing calories tracked in table results in record being deleted`(){
         transaction {
            val caloriestrackeddao = populateCaloriesTrackerTable()
            assertEquals(3,caloriestrackeddao.getAll().size)
            caloriestrackeddao.delete(3)
            assertEquals(2,caloriestrackeddao.getAll().size)
         }
      }
   }

   @Nested
   inner class UpdateCaloriesTracked{

      @Test
      fun `updating existing calories tracked in table results in successful update`(){
         transaction {
            val caloriestrackerdao = populateCaloriesTrackerTable()

            val caloriestracker3updated = CaloriesTrackerDC(3,3, DateTime.now(),"Cycling",15,99.9)
            caloriestrackerdao.update(3,caloriestracker3updated)
            assertEquals(caloriestracker3updated,caloriestrackerdao.findByUserID(3))
         }
      }

      @Test
      fun `updating non-existent calories tracked in table results in no updates`(){
         transaction {
            val caloriestrackerdao = populateCaloriesTrackerTable()

            val caloriestracker4updated = CaloriesTrackerDC(4,4, DateTime.now(),"Cycling",15,234.0)
            caloriestrackerdao.update(4,caloriestracker4updated)

            assertEquals(null,caloriestrackerdao.findByUserID(4))
            assertEquals(3,caloriestrackerdao.getAll().size)
         }
      }
   }

}