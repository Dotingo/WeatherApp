package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
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
import com.example.weatherapp.data.DataStoreManager
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.screens.DialogSearch
import com.example.weatherapp.screens.MainCard
import com.example.weatherapp.screens.TabLayout
import com.example.weatherapp.ui.theme.WeatherAppTheme
import org.json.JSONObject


const val API_KEY = "8dccd30d8074451dbc2104019231607"

class MainActivity : ComponentActivity() {
    private val dataStoreManager = DataStoreManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherAppTheme {

                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                val dialogState = remember {
                    mutableStateOf(false)
                }
                val cityName = remember {
                    mutableStateOf("Ростов-на-Дону")
                }
                val currentDay = remember {
                    mutableStateOf(
                        WeatherModel(
                            "",
                            "",
                            "0.00",
                            "",
                            "",
                            "0.00",
                            "0.00",
                            "",
                            "0",
                            "0.00",
                            "0"
                        )
                    )
                }
                if (dialogState.value) {
                    DialogSearch(dialogState, dataStoreManager, onSubmit = {
                        getData(it, this, daysList, currentDay)
                    })
                }

                LaunchedEffect(key1 = true) {
                    dataStoreManager.getCityName().collect {
                        cityName.value = it.name
                    }
                }

                getData(cityName.value, this@MainActivity, daysList, currentDay)

                Image(
                    painter = painterResource(id = R.drawable.sky_bg),
                    contentDescription = "sky1",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.5f),
                    contentScale = ContentScale.FillBounds
                )
                Column {
                    MainCard(currentDay, onClickSync = { city ->
                        getData(city, this@MainActivity, daysList, currentDay)
                    }, onClickSearch = {
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
    val url = "https://api.weatherapi.com/v1/forecast.json?" +
            "key=$API_KEY" +
            "&q=$city" +
            "&days=3" +
            "&aqi=no" +
            "&alerts=no" +
            "&lang=ru"
    val queue = Volley.newRequestQueue(context)
    Log.d("MyLog", url)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val resp = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
            val list = getWeatherByDays(resp)
            currentDay.value = list[0]
            daysList.value = list
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel> {

    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                item.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                item.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                item.getJSONArray("hour").toString(),
                "",
                "",
                ""

            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt()
            .toString(),
        uv = mainObject.getJSONObject("current").getString("uv").toFloat().toInt().toString(),
        wind = mainObject.getJSONObject("current").getString("wind_kph").toFloat().toString(),
        humidity = mainObject.getJSONObject("current").getString("humidity").toInt().toString()
    )
    return list
}


