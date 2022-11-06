package ie.setu.config

import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.name

class DbConfig{

    private val logger = KotlinLogging.logger {}

    //NOTE: you need the ?sslmode=require otherwise you get an error complaining about the ssl certificate
    fun getDbConnection() :Database{

        logger.info{"Starting DB Connection..."}

        val dbConfig = Database.connect(
            "jdbc:postgresql://ec2-34-194-40-194.compute-1.amazonaws.com:5432/d2eklhs8fil4r7?sslmode=require",
            driver = "org.postgresql.Driver",
            user = "ishhycbbxrpvyw",
            password = "5bb17faf03a8f03a8d6dd9e598cc9d72721b1471c8b05fa60869d95ccbd50899")

        logger.info{"DbConfig name = " + dbConfig.name}
        logger.info{"DbConfig url = " + dbConfig.url}

        return dbConfig
    }

}