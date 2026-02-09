package com.brokechef.recipesharingapp.utils

import android.util.Patterns

const val PASSWORD_MINIMUM_LENGTH = 8

fun validateEmail(email: String): String? =
    when {
        email.isBlank() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email"
        else -> null
    }

fun validatePassword(
    password: String,
    repeatPassword: String,
): String? =
    when {
        password.isBlank() -> "Password is required"
        password.length < PASSWORD_MINIMUM_LENGTH -> "Password must be at least $PASSWORD_MINIMUM_LENGTH characters"
        password != repeatPassword -> "Passwords don't match"
        else -> null
    }

fun validateSignIn(
    email: String,
    password: String,
): String? =
    validateEmail(email) ?: when {
        password.isBlank() -> "Password is required"
        else -> null
    }

fun validateSignUp(
    name: String,
    email: String,
    password: String,
    repeatPassword: String,
): String? =
    when {
        name.isBlank() -> "Username is required"
        else -> validateEmail(email) ?: validatePassword(password, repeatPassword)
    }

fun validateResetPassword(
    password: String,
    repeatPassword: String,
): String? = validatePassword(password, repeatPassword)
