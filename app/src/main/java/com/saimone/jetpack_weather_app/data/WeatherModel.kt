package com.saimone.jetpack_weather_app.data

data class WeatherModel (
    val city: String,
    val time: String,
    val currentTemp: String,
    val condition: String,
    val icon: String,
    val maxTemp: String,
    val minTemp: String,
    val hours: String
)
