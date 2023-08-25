package com.saimone.jetpack_weather_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.saimone.jetpack_weather_app.data.WeatherModel
import com.saimone.jetpack_weather_app.screens.DialogSearch
import com.saimone.jetpack_weather_app.screens.MainCard
import com.saimone.jetpack_weather_app.screens.TabLayout
import com.saimone.jetpack_weather_app.ui.theme.JetpackweatherappTheme
import org.json.JSONObject

const val API_KEY = "PUT YOUR OWN API KEY HERE"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackweatherappTheme {
                val isDarkTheme = isSystemInDarkTheme()

                val currentCity = remember {
                    mutableStateOf("Kiev")
                }
                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                val currentDay = remember {
                    mutableStateOf(WeatherModel("", "", "0.0", "", "", "0.0", "0.0", ""))
                }
                val dialogState = remember {
                    mutableStateOf(false)
                }
                if (dialogState.value) {
                    DialogSearch(dialogState, onSubmit = {
                        currentCity.value = it
                        getData(currentCity.value, this@MainActivity, daysList, currentDay)
                    })
                }
                getData(currentCity.value, this, daysList, currentDay)

                val background = if (isDarkTheme) {
                    painterResource(id = R.drawable.dark_sky)
                } else {
                    painterResource(id = R.drawable.summer_clouds)
                }
                Image(
                    painter = background,
                    contentDescription = "background",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.9f),
                    contentScale = ContentScale.FillBounds
                )
                Column {
                    MainCard(currentDay,
                        onClickSync = {
                            getData(currentCity.value, this@MainActivity, daysList, currentDay)
                        },
                        onClickSearch = {
                            dialogState.value = true
                        })
                    TabLayout(daysList, currentDay)
                }
            }
        }
    }
}

private fun getData(
    city: String,
    context: Context,
    daysList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>
) {
    val url =
        "https://api.weatherapi.com/v1/forecast.json?key=${API_KEY}&q=${city}&days=3&aqi=no&alerts=no"
    val query = Volley.newRequestQueue(context)
    val request = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val list = getWeatherByDays(response)
            currentDay.value = list[0]
            daysList.value = list
            Log.d("MyLog", daysList.value.toString())
        },
        { error ->
            Log.d("MyLog", "Error: $error")
        }
    )
    query.add(request)
}

private fun getWeatherByDays(response: String): List<WeatherModel> {
    if (response.isEmpty()) {
        return listOf()
    }
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    val list = ArrayList<WeatherModel>()
    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject

        val model = WeatherModel(
            city,
            item.getString("date"),
            "",
            item.getJSONObject("day").getJSONObject("condition").getString("text"),
            item.getJSONObject("day").getJSONObject("condition").getString("icon"),
            item.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString() + "°C",
            item.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString() + "°C",
            item.getJSONArray("hour").toString()
        )
        list.add(model)
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt()
            .toString() + "°C"
    )
    return list
}