package com.brokechef.recipesharingapp.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(
    context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit { putString("session_token", token) }
    }

    fun getToken(): String? = prefs.getString("session_token", null)

    fun clearToken() {
        prefs.edit { remove("session_token") }
    }
}
