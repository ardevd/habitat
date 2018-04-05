package no.aegisdynamics.habitat.data.weather

/**
 * Model class for a weather condition
 */

class Weather(val conditionCode: Int,
              val condition: String?,
              val conditionIcon: String,
              val temperature: Double,
              val temperatureMax: Double,
              val temperatureMin: Double)
