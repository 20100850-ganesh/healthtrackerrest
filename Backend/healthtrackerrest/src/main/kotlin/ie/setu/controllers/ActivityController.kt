package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import ie.setu.domain.User
import ie.setu.domain.repository.UserDAO
import io.javalin.http.Context
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Activity
import ie.setu.domain.CaloriesTrackerDC
import ie.setu.domain.UserDTO
import ie.setu.domain.repository.ActivityDAO
import io.javalin.plugin.openapi.annotations.*

object ActivityController {

    private val activityDAO = ActivityDAO();

    fun getAllActivities(ctx: Context) {
        //mapper handles the deserialization of Joda date into a String.
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(mapper.writeValueAsString( activityDAO.getAll() ))
    }

    fun getActivitiesByUserId(ctx: Context) {
        if (HealthTrackerController.userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty()) {
                //mapper handles the deserialization of Joda date into a String.
                val mapper = jacksonObjectMapper()
                    .registerModule(JodaModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                ctx.json(mapper.writeValueAsString(activities))
            }
        }
    }

    fun addActivity(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val activity : Activity = mapper.readValue<Activity>(ctx.body())
        activityDAO.save(activity)
        ctx.json(activity)
    }

    fun getActivitiesByActivityId(ctx: Context) {
        val activity = activityDAO.findByActivityId((ctx.pathParam("activity-id").toInt()))
        if (activity != null){
            val mapper = jacksonObjectMapper()
                .registerModule(JodaModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            ctx.json(mapper.writeValueAsString(activity))
        }
    }

    fun deleteActivityByActivityId(ctx: Context){
        activityDAO.deleteByActivityId(ctx.pathParam("activity-id").toInt())
    }

    fun deleteActivityByUserId(ctx: Context){
        activityDAO.deleteByUserId(ctx.pathParam("user-id").toInt())
    }

    fun updateActivity(ctx: Context){
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val activity : Activity = mapper.readValue<Activity>(ctx.body())
        activityDAO.updateByActivityId(
            activityId = ctx.pathParam("activity-id").toInt(),
            activityDTO=activity)
    }
}