package com.ludwiglarsson.antiplanner.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.checkPermissions(permissions: List<String>): Boolean {
    for (permission in permissions) {
        val result = ContextCompat.checkSelfPermission(this, permission)
        if (result != PackageManager.PERMISSION_GRANTED) return false
    }
    return true
}