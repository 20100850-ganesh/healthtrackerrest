package ie.setu.utils

import ie.setu.domain.*
import ie.setu.domain.db.*
import org.jetbrains.exposed.sql.ResultRow

fun mapToUser(it: ResultRow) = User(
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email]
)

fun mapToMeasurementDTO(it: ResultRow) = MeasurementDTO(
    userid = it[Measurements.userid],
    weight = it[Measurements.weight],
    height = it[Measurements.height],
    bmi = it[Measurements.bmi]
)

fun mapToCaloriesTracker(it: ResultRow) = CaloriesTrackerDC(
    id = it[CaloriesTracker.id],
    userid = it[CaloriesTracker.userid],
    //date = it[CaloriesTracker.date],
    activity = it[CaloriesTracker.activity],
    duration = it[CaloriesTracker.duration],
    caloriesBurnt = it[CaloriesTracker.caloriesBurnt]
)


fun mapToActivity(it: ResultRow) = Activity(
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    started = it[Activities.started],
    calories = it[Activities.calories],
    userId = it[Activities.userId]
)