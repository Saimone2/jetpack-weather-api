package com.saimone.jetpack_weather_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.saimone.jetpack_weather_app.data.WeatherModel
import com.saimone.jetpack_weather_app.ui.theme.TransparentBlue

@Composable
fun ListItem(item: WeatherModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp),
        colors = CardDefaults.cardColors(TransparentBlue),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(start = 8.dp, top = 5.dp, bottom = 5.dp)
            ) {
                Text(
                    text = item.time,
                    color = Color.White
                )
                Text(
                    text = item.condition,
                    color = Color.White
                )
            }
            Text(
                text = if (item.currentTemp.isNotEmpty()) {
                    "${item.currentTemp.toFloat().toInt()}°С"
                } else {
                    "${item.maxTemp.toFloat().toInt()}°С / ${item.minTemp.toFloat().toInt()}°С"
                },
                color = Color.White,
                style = TextStyle(fontSize = 25.sp)
            )
            AsyncImage(
                model = "https:${item.icon}",
                contentDescription = "image1",
                modifier = Modifier
                    .size(35.dp)
                    .padding(end = 8.dp)
            )
        }
    }
}