package ie.setu.config

import ie.setu.controllers.*
import ie.setu.controllers.MeasurementsController.getAllMeasurements
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.ReDocOptions
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.info.Info

class JavalinConfig {

    fun startJavalinService(): Javalin {

        val app = Javalin.create{
            it.registerPlugin(getConfiguredOpenApiPlugin())
            it.defaultContentType = "application/json"
        }.apply {
            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
        }.start(getHerokuAssignedPort())

        registerRoutes(app)
        return app
    }

    fun getConfiguredOpenApiPlugin() = OpenApiPlugin(
        OpenApiOptions(
            Info().apply {
                title("Health Tracker App")
                version("1.0")
                description("Health Tracker API")
            }
        ).apply {
            path("/swagger-docs") // endpoint for OpenAPI json
            swagger(SwaggerOptions("/swagger-ui")) // endpoint for swagger-ui
            reDoc(ReDocOptions("/redoc")) // endpoint for redoc
        }
    )

    private fun getHerokuAssignedPort(): Int {
        val herokuPort = System.getenv("PORT")
        return if (herokuPort != null) {
            Integer.parseInt(herokuPort)
        } else 7000
    }

    private fun registerRoutes(app: Javalin) {
        app.routes {
            path("/api/users") {
                get(HealthTrackerController::getAllUsers)
                post(HealthTrackerController::addUser)
                path("{user-id}"){
                    get(HealthTrackerController::getUserByUserId)
                    delete(HealthTrackerController::deleteUser)
                    patch(HealthTrackerController::updateUser)
                }
                path("activities"){
                    get(ActivityController::getActivitiesByUserId)
                    delete(ActivityController::deleteActivityByUserId)
                }
                path("/email/{email}"){
                    get(HealthTrackerController::getUserByEmail)
                }
            }
            path("/api/activities") {
                get(ActivityController::getAllActivities)
                post(ActivityController::addActivity)
                path("{activity-id}") {
                    get(ActivityController::getActivitiesByActivityId)
                    delete(ActivityController::deleteActivityByActivityId)
                    patch(ActivityController::updateActivity)
                }
            }

            path("/api/measurements"){
                get(MeasurementsController::getAllMeasurements)
                post(MeasurementsController::addMeasurements)
                path("{userid}"){
                    get(MeasurementsController::getMeasurementsByUserId)
                    delete(MeasurementsController::deleteMeasurements)
                    patch(MeasurementsController::updateMeasurements)
                }
            }

            path("/api/caloriestracker"){
                get(CaloriesTrackerController::getExistingData)
                post(CaloriesTrackerController::addEntry)
                path("{userid}"){
                    get(CaloriesTrackerController::getDataByUserId)
                    delete(CaloriesTrackerController::deleteData)
                    patch(CaloriesTrackerController::updateData)
                }
            }

        }
    }
}