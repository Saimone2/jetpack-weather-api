package com.saimone.jetpack_weather_app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.saimone.jetpack_weather_app.R
import com.saimone.jetpack_weather_app.data.WeatherModel
import com.saimone.jetpack_weather_app.ui.theme.Blue40
import com.saimone.jetpack_weather_app.ui.theme.Blue80
import com.saimone.jetpack_weather_app.ui.theme.TransparentBlue40
import com.saimone.jetpack_weather_app.ui.theme.TransparentBlue80
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MainCard(
    currentDay: MutableState<WeatherModel>,
    onClickSync: () -> Unit,
    onClickSearch: () -> Unit
) {
    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    val fontName = GoogleFont("Signika Negative")

    val fontFamily = FontFamily(
        Font(googleFont = fontName, fontProvider = provider)
    )
    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .shadow(elevation = 0.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(if (isSystemInDarkTheme()) TransparentBlue80 else TransparentBlue40),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(top = 10.dp, start = 8.dp),
                        text = currentDay.value.time,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily
                        ),
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:" + currentDay.value.icon,
                        contentDescription = "Weather Icon",
                        modifier = Modifier
                            .size(45.dp)
                    )
                }
                Text(
                    text = currentDay.value.city,
                    style = TextStyle(fontSize = 24.sp, fontFamily = fontFamily),
                    color = Color.White,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center // Center content vertically
                ) {
                    Text(
                        text = currentDay.value.currentTemp.ifEmpty {
                            "${currentDay.value.maxTemp} / ${currentDay.value.minTemp}"
                        },
                        style = TextStyle(
                            fontSize = if (currentDay.value.currentTemp.isEmpty()) {
                                54.sp
                            } else {
                                66.sp
                            },
                            fontFamily = FontFamily.Default
                        ),
                        color = Color.White,
                    )
                }
                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 20.sp, fontFamily = fontFamily),
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onClickSearch) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search Icon",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onClickSync) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = "Sync Icon",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {
    val tabList = listOf("HOURS", "DAYS")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        TabRow(
            contentColor = Color.White,
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.White)
                )
            }) {
            tabList.forEachIndexed { index, title ->
                Tab(modifier = Modifier.background(if (isSystemInDarkTheme()) Blue80 else Blue40),
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(text = title)
                    })
            }
        }
        HorizontalPager(
            count = tabList.size, state = pagerState, modifier = Modifier.weight(1.0f)
        ) { index ->
            val list = when (index) {
                0 -> getWeatherByHours(currentDay.value.hours, currentDay)
                1 -> daysList.value
                else -> daysList.value
            }
            MainList(list = list, currentDay = currentDay)
        }
    }
}

private fun getWeatherByHours(hours: String, currentDay: MutableState<WeatherModel>): List<WeatherModel> {
    if (hours.isEmpty()) {
        return listOf()
    }
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray[i] as JSONObject

        val currentTime = currentDay.value.time.substringBefore(":") + ":00"

        if(item.getString("time") == currentTime) {
            list.clear()
            continue
        }
        val model = WeatherModel(
            "",
            item.getString("time"),
            item.getString("temp_c").toFloat().toInt().toString() + "°C",
            item.getJSONObject("condition").getString("text"),
            item.getJSONObject("condition").getString("icon"),
            "",
            "",
            ""
        )
        list.add(model)
    }
    return list
}