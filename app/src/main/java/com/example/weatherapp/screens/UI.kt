package com.example.weatherapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.data.CityName
import com.example.weatherapp.data.DataStoreManager
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.ui.theme.AlertDialogColor
import com.example.weatherapp.ui.theme.BlueLight
import kotlinx.coroutines.launch

@Composable
fun MainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>) {
    LazyColumn(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
        itemsIndexed(
            list
        ) { _, item ->
            ListItem(item, currentDay)
        }
    }
}

@Composable
fun ListItem(weather: WeatherModel, currentDay: MutableState<WeatherModel>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
            .clickable {
                if (weather.hours.isEmpty()) return@clickable
                currentDay.value = weather
            },
        backgroundColor = BlueLight,
        elevation = 0.dp,
        shape = RoundedCornerShape(10.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 3.dp,
                    bottom = 3.dp
                ).weight(0.5f)
            ) {
                val time = weather.time.split(" ")
                Text(text = time.last(), color = Color.White)
                Text(
                    text = weather.conditionText,
                    color = Color.White
                )
            }

            Text(
                text = weather.currentTemp.ifEmpty {"${weather.maxTemp}°/${weather.minTemp}°"} ,
                color = Color.White,
                style = TextStyle(fontSize = 26.sp),
                modifier = Modifier.weight(0.2f)
            )
            AsyncImage(
                model = "https:${weather.conditionIcon}",
                contentDescription = "img5",
                modifier = Modifier
                    .size(45.dp)
                    .padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun DialogSearch(
    dialogState: MutableState<Boolean>,
    dataStore: DataStoreManager,
    onSubmit: (String) -> Unit
) {
    val dialogText = remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    AlertDialog(onDismissRequest = {
        dialogState.value = false
    },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                dialogState.value = false
                scope.launch {
                    dataStore.saveCityName(CityName(dialogText.value))
                }
            }) {
                Text(text = "Ок", color = Color.White)

            }
        },
        dismissButton = {
            TextButton(onClick = { dialogState.value = false }) {
                Text(text = "Отмена", color = Color.White)

            }
        },
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Введите название города:", color = Color.White)
                TextField(value = dialogText.value, colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White
                ), onValueChange = {
                    dialogText.value = it
                })
            }

        }, backgroundColor = AlertDialogColor
    )
}