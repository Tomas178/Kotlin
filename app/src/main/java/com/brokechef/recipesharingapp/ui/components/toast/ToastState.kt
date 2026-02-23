package com.brokechef.recipesharingapp.ui.components.toast

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ToastType { SUCCESS, ERROR, INFO, LOADING }

data class ToastData(
    val message: String,
    val type: ToastType = ToastType.INFO,
)

object ToastState {
    var current by mutableStateOf<ToastData?>(null)
        private set

    fun show(
        message: String,
        type: ToastType = ToastType.INFO,
    ) {
        current = ToastData(message, type)
    }

    fun success(message: String) = show(message, ToastType.SUCCESS)

    fun error(message: String) = show(message, ToastType.ERROR)

    fun loading(message: String) = show(message, ToastType.LOADING)

    fun dismiss() {
        current = null
    }
}
