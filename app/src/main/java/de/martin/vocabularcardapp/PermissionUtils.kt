package de.martin.vocabularcardapp

import android.Manifest
import android.app.AppOpsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1
const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 2
const val MANAGE_EXTERNAL_STORAGE_PERMISSION = "android:manage_external_storage"
const val NOT_APPLICABLE = "N/A"

fun getStoragePermissionName(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        MANAGE_EXTERNAL_STORAGE_PERMISSION
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
}

fun getLegacyStorageStatus(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Environment.isExternalStorageLegacy().toString()
    } else {
        NOT_APPLICABLE
    }
}

fun getPermissionStatus(activity: AppCompatActivity): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        checkStoragePermissionApi30(activity).toString()
    } else {
        checkStoragePermissionApi19(activity).toString()
    }
}

fun checkStoragePermission(activity: AppCompatActivity): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        checkStoragePermissionApi30(activity)
    } else {
        checkStoragePermissionApi19(activity)
    }
}

fun requestStoragePermission(activity: AppCompatActivity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        requestStoragePermissionApi30(activity)
    }
    // If you want to see the default storage behavior on Android Q once the permission is granted
    // Set the "requestLegacyExternalStorage" flag in the AndroidManifest.xml file to false
    else {
        requestStoragePermissionApi19(activity)
    }
}

@RequiresApi(30)
fun checkStoragePermissionApi30(activity: AppCompatActivity): Boolean {
    val appOps = activity.getSystemService(AppOpsManager::class.java)
    val mode = appOps.unsafeCheckOpNoThrow(
        MANAGE_EXTERNAL_STORAGE_PERMISSION,
        activity.applicationInfo.uid,
        activity.packageName
    )

    return mode == AppOpsManager.MODE_ALLOWED
}

@RequiresApi(30)
fun requestStoragePermissionApi30(activity: AppCompatActivity) {
    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)

    activity.startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST)
}

@RequiresApi(19)
fun checkStoragePermissionApi19(activity: AppCompatActivity): Boolean {
    val status =
        ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)

    return status == PackageManager.PERMISSION_GRANTED
}

@RequiresApi(19)
fun requestStoragePermissionApi19(activity: AppCompatActivity) {
    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    ActivityCompat.requestPermissions(
        activity,
        permissions,
        READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
    )
}