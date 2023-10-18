package com.ludwiglarsson.antiplanner.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import java.util.UUID
import javax.inject.Inject


class SharedPreferencesHelper @Inject constructor(
    context: Context
) {

    private val sharedPreferences: SharedPreferences
    private val editor: Editor

    init {
        sharedPreferences = context.getSharedPreferences("states", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        if (!sharedPreferences.contains("UID")) {
            editor.putString("UID", UUID.randomUUID().toString())
            editor.apply()
        }

        if (!sharedPreferences.contains("token")) {
            putToken("no_token")
        }

        if(!sharedPreferences.contains("mode")){
            editor.putString("mode", "system")
            editor.apply()
        }


        Constants.phoneId = getPhoneID()
    }

    fun putRevision(revision: Int) {
        editor.putInt("REVISION", revision)
        editor.apply()
    }

    fun getToken(): String {
        return sharedPreferences.getString("token", "unaffordable")!!
    }

    fun getTokenForResponse():String{
        return if (getToken() == "unaffordable") {
            "Bearer unaffordable"
        } else {
            "OAuth ${getToken()}"
        }
    }

    private fun getPhoneID():String = sharedPreferences.getString("UID", "uid").toString()
    fun putToken(token: String) {
        editor.putString("token", token)
        editor.apply()
    }

    fun getLastRevision(): Int = sharedPreferences.getInt("REVISION", 1)

}