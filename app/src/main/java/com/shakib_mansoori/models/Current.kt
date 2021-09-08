package com.shakib_mansoori.models

data class Current(
    val feels_like: Double,
    val humidity: Int,
    val temp: Double,
    val weather: List<Weather>,
    val wind_speed: Double
)