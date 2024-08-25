package com.example.weatherapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.ui.theme.BlueLight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun MainCard(
    currentDay: MutableState<WeatherModel>,
    onClickSync: (String) -> Unit,
    onClickSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Card(
            backgroundColor = BlueLight,
            modifier = Modifier.fillMaxWidth(),
            elevation = 0.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(
                                top = 8.dp,
                                start = 8.dp
                            ),
                            text = currentDay.value.time,
                            style = TextStyle(fontSize = 15.sp),
                            color = Color.White
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Индекс УФ: ${currentDay.value.uv}",
                                    style = TextStyle(fontSize = 15.sp),
                                    color = Color.White
                                )
                                AsyncImage(
                                    model = "https:${currentDay.value.conditionIcon}",
                                    contentDescription = "img",
                                    modifier = Modifier
                                        .size(35.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = currentDay.value.city,
                        style = TextStyle(fontSize = 24.sp),
                        color = Color.White
                    )

                    Text(
                        text = if (currentDay.value.currentTemp.isNotEmpty())
                            currentDay.value.currentTemp.toFloat().toInt().toString() + "°C"
                        else "${currentDay.value.maxTemp}°C/${currentDay.value.minTemp}°C",
                        style = TextStyle(fontSize = 40.sp),
                        color = Color.White
                    )

                    Text(
                        text = currentDay.value.conditionText,
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.White
                    )


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            modifier = Modifier.align(Alignment.Bottom),
                            onClick = {
                            onClickSearch.invoke()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "src",
                                tint = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentDay.value.maxTemp.toFloat().toInt()}" +
                                        "°C/${currentDay.value.minTemp.toFloat().toInt()}°C",
                                style = TextStyle(fontSize = 16.sp),
                                color = Color.White,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                            Text(
                                text = "Влажность: ${currentDay.value.humidity}%",
                                style = TextStyle(fontSize = 15.sp),
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(
                                text = "Скорость ветра: ${currentDay.value.wind}км/ч",
                                style = TextStyle(fontSize = 15.sp),
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                        }

                        IconButton(
                            modifier = Modifier.align(Alignment.Bottom),
                            onClick = {
                                onClickSync(currentDay.value.city)
                            }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_sync),
                                contentDescription = "sync",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {
    val tabList = listOf("ЧАСЫ", "ДНИ")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(
                start = 5.dp,
                end = 5.dp
            )
            .clip(RoundedCornerShape(10.dp))

    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { pos ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, pos)
                )
            },
            backgroundColor = BlueLight,
            contentColor = Color.White
        ) {
            tabList.forEachIndexed { index, text ->
                Tab(selected = false,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = text,
                            color = Color.White
                        )
                    }
                )
            }
        }
        HorizontalPager(
            count = tabList.size,
            state = pagerState,
            modifier = Modifier.weight(1.0f)
        ) { index ->
            val list = when (index) {
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }
            MainList(list, currentDay)
        }
    }
}

private fun getWeatherByHours(hours: String): List<WeatherModel> {
    if (hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString() + "°",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                "",
                item.getString("uv").toFloat().toInt().toString(),
                item.getString("wind_kph").toFloat().toString(),
                item.getString("humidity").toInt().toString()
            )
        )
    }
    return list
}