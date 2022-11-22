package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.CaloriesTrackerDC
import ie.setu.domain.repository.CaloriesTrackerDAO
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import org.jetbrains.exposed.sql.transactions.inTopLevelTransaction

object CaloriesTrackerController {

    private val caloriesTrackerDAO = CaloriesTrackerDAO()

    @OpenApi(
        summary = "Get Existing Data for Calories",
        operationId = "getExistingData",
        tags = ["Calories"],
        path = "/api/caloriestracker",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<CaloriesTrackerDC>::class)])]
    )
    fun getExistingData(ctx: Context) {
        val calories = caloriesTrackerDAO.getAll()
        if(calories.size!=0){
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(calories)
    }

    @OpenApi(
        summary = "Add entry for Calories data",
        operationId = "addEntry",
        tags = ["Calories"],
        path = "/api/caloriestracker",
        method = HttpMethod.POST,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<CaloriesTrackerDC>::class)])]
    )
    fun addEntry(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val acitivityData = mapper.readValue<CaloriesTrackerDC>(ctx.body())
        val caloriesBurnt = calculateCalories(acitivityData)
        caloriesTrackerDAO.save(acitivityData,caloriesBurnt)
        ctx.json(acitivityData)
    }

    fun calculateCalories(acitivityData: CaloriesTrackerDC): Double {
        when(acitivityData.activity ){
            "Walking" -> return (acitivityData.duration *4).toDouble()
            "Jogging" -> return (acitivityData.duration * 13.33)
            "Cycling" -> return (acitivityData.duration * 6.66)
        }
        return 0.0
    }

    @OpenApi(
        summary = "Get Calories data by user id",
        operationId = "getDataByUserId",
        tags = ["Calories"],
        path = "/api/caloriestracker/{userid}",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<CaloriesTrackerDC>::class)])]
    )
    fun getDataByUserId(ctx: Context) {
        val userData = caloriesTrackerDAO.findByUserID(ctx.pathParam("userid").toInt())
        if (userData != null) {
            ctx.json(userData)
        }
    }

    @OpenApi(
        summary = "Delete Calories data",
        operationId = "deleteData",
        tags = ["Calories"],
        path = "/api/caloriestracker/{userid}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("userid", Int::class, "The user ID")],
        responses = [OpenApiResponse("204")]
    )
    fun deleteData(ctx: Context) {
        caloriesTrackerDAO.delete(ctx.pathParam("userid").toInt())

    }

    @OpenApi(
        summary = "Update calories data by user ID",
        operationId = "updateData",
        tags = ["Calories"],
        path = "/api/caloriestracker/{userid}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("userid", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun updateData(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val userDataUpdates = mapper.readValue<CaloriesTrackerDC>(ctx.body())
        caloriesTrackerDAO.update(
            userid = ctx.pathParam("userid").toInt(),
            userData=userDataUpdates)
    }
}