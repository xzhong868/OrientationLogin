package com.example.orientationlogin

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest

class SharedPreferenceManager(val context: Context) {
    private val client = "sharedpref"
    val sharedPref: SharedPreferences = context.getSharedPreferences(client, Context.MODE_PRIVATE)

//    fun saveClient(client: SupabaseClient) {
//        sharedPref.edit().putString("client", Gson().toJson(client)).apply()
//    }
//
//    fun getClient(): SupabaseClient? {
//        val data = sharedPref.getString("client", null)
//        if (data == null) {
//            return null
//        }
//        return Gson().fromJson(data, SupabaseClient::class.java)
//    }

}