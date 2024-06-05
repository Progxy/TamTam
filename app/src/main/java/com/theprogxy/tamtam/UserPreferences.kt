package com.theprogxy.tamtam

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences {
    fun getValue(id: String, context: Context) : String? = runBlocking {
        val hashId = stringPreferencesKey(id)
        try {
            val res: Flow<String?> = context.dataStore.data
                .map { preferences -> preferences[hashId] }
            return@runBlocking res.first()
        } catch (e:Exception) {
            print("An error occurred while trying to access '${id}': ${e}\n")
            return@runBlocking null
        }
    }

    fun setValue(id: String, value: String, context: Context) : Boolean = runBlocking {
        val hashId = stringPreferencesKey(id)
        try {
            context.dataStore.edit { settings -> settings[hashId] = value }
            return@runBlocking true
        } catch (e: Exception) {
            print("An error occurred while trying to store '${id}': ${e}\n")
            return@runBlocking false
        }
    }
}