package ie.setu.domain.repository

import ie.setu.controllers.CaloriesTrackerController
import ie.setu.domain.CaloriesTrackerDC
import ie.setu.domain.db.CaloriesTracker
import ie.setu.domain.db.Users
import ie.setu.utils.mapToCaloriesTracker
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CaloriesTrackerDAO {

    fun getAll(): ArrayList<CaloriesTrackerDC> {
        val caloriesBurntList: ArrayList<CaloriesTrackerDC> = arrayListOf()
        transaction {
            CaloriesTracker.selectAll().map {
                caloriesBurntList.add(mapToCaloriesTracker(it))
            }
        }
        return caloriesBurntList
    }

    fun save(acitivityData: CaloriesTrackerDC, calculatedCalories: Double) {
        transaction {
            CaloriesTracker.insert {
                it[userid] = acitivityData.userid
                it[activity] = acitivityData.activity
                //it[date] = acitivityData.date
                it[duration] = acitivityData.duration
                it[caloriesBurnt] = calculatedCalories
            }
        }

    }

    fun findByUserID(userID: Int): CaloriesTrackerDC? {
        return transaction {
            CaloriesTracker.select() {CaloriesTracker.userid eq userID}
                .map{ mapToCaloriesTracker(it) }
                .firstOrNull()
        }
    }

    fun delete(userID: Int) {
        return transaction{
            CaloriesTracker.deleteWhere{
                CaloriesTracker.userid eq userID
            }
        }
    }

    fun update(userid: Int, userData: CaloriesTrackerDC) {
        transaction {
            CaloriesTracker.update ({
                CaloriesTracker.userid eq userid}) {
                it[activity] = userData.activity
                it[duration] = userData.duration
                //it[date] = userData.date
                it[caloriesBurnt] = CaloriesTrackerController.calculateCalories(userData)
            }
        }
    }
}