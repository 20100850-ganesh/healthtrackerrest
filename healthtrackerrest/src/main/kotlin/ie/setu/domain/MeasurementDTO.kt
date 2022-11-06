package ie.setu.domain

import org.joda.time.DateTime

data class MeasurementDTO (var userid: Int,
                           var weight:Double,
                            var height:Double,
                           var bmi:Double)