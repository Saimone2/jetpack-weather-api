package com.saimone.jetpack_weather_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.saimone.jetpack_weather_app.screens.MainCard
import com.saimone.jetpack_weather_app.screens.TabLayout
import com.saimone.jetpack_weather_app.ui.theme.JetpackweatherappTheme
import org.json.JSONObject

const val API_KEY = "15b2a15e17c554f4925a66f101302ca3"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackweatherappTheme {
                Image(
                    painter = painterResource(id = R.drawable.sky),
                    contentDescription = "background",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.7f),
                    contentScale = ContentScale.FillBounds
                )
                Column {
                    MainCard()
                    TabLayout()
                }
            }
        }
    }
}

@Composable
fun Greeting(city: String, context: Context) {
    val state = remember {
        mutableStateOf("Unknown")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Temperature in $city: ${state.value} °C")
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Button(onClick = {
                getResult(city, state, context)
            }, modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()) {
                Text(text = "Refresh")
            }
        }
    }
}
private fun getResult(city: String, state: MutableState<String>, context: Context) {
    val url = "https://api.openweathermap.org/data/2.5/forecast?id=524901&appid=$API_KEY&q=$city"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val obj = JSONObject(response)
            val forecastList = obj.getJSONArray("list")

            val forecast = forecastList.getJSONObject(0)
            val temperature = forecast.getJSONObject("main").getDouble("temp")
            state.value = (temperature - 273.15).round(1).toString()
        },
        { error -> Log.d("MyLog", "Error: $error")}
    )
    queue.add(stringRequest)
}

private fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}