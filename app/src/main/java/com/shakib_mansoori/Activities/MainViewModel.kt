package com.shakib_mansoori.Activities

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakib_mansoori.models.Current
import com.shakib_mansoori.models.WeatherContainer
import com.shakib_mansoori.repo.repo
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val weatherContainer = MutableLiveData<WeatherContainer>()
    private val currentWeather = MutableLiveData<Current>()
    private val cityName = MutableLiveData<String>()
    private val latitude = MutableLiveData<Double>()
    private val longti = MutableLiveData<Double>()


    fun fetchWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            var apiResponse = repo.fetchWeatherData(lat, lon)
            if (apiResponse.isSuccessful) {
                apiResponse.body()!!.let { it ->

                    weatherContainer.postValue(it)

                    Log.d("Tag", "view model " + it.name + " " + it.timezone + " " + it.id)

                    latitude.postValue(it.lat)
                    longti.postValue(it.lon)
                    currentWeather.postValue(it.current)

                }
            } else {
                Log.d("TAG", apiResponse.errorBody()!!.string())
            }



            apiResponse = repo.getCityName(lat, lon)
            if (apiResponse.isSuccessful) {
                if (apiResponse.body() != null) {
                    apiResponse.body().let {
                        cityName.postValue(it!!.name)
                    }
                }
            } else {
                Log.d("TAG", apiResponse.errorBody()!!.string())
            }
        }
    }


    fun getCurrentWeaterData(): LiveData<Current> {
        return currentWeather
    }


    fun getLatitude(): LiveData<Double> {
        return latitude
    }

    fun getLongtitude(): LiveData<Double> {
        return latitude
    }

    fun getCityName(): LiveData<String> {
        return cityName
    }
}