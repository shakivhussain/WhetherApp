package com.shakib_mansoori.models

data class WeatherContainer(
    val current: Current,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val id: Int,
    val name: String
)