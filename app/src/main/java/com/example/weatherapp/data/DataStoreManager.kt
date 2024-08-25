package com.example.weatherapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data_store")

class DataStoreManager(private val context: Context) {

    suspend fun saveCityName(cityName: CityName){
        context.dataStore.edit {pref ->
            pref[stringPreferencesKey(KEY_CITY_NAME)] = cityName.name
        }
    }

    fun getCityName() = context.dataStore.data.map {pref ->
        return@map CityName(
            pref[stringPreferencesKey(KEY_CITY_NAME)] ?: "Ростов-на-Дону"
        )
    }

    companion object{
        private const val KEY_CITY_NAME = "city_name"
    }
}