package com.shakib_mansoori.repo

import com.shakib_mansoori.models.WeatherContainer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("data/2.5/onecall")
    suspend fun fetchWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String,
        @Query("appid") apiKey: String
    ): Response<WeatherContainer>

    @GET("data/2.5/weather")
    suspend fun getCityName(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherContainer>
}