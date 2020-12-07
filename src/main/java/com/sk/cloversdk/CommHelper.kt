package com.sk.cloversdk

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

object CommHelper : BaseHelper() {

    fun getAppVersionName(): String {
        val packageInfo = ctx.applicationContext
            .packageManager
            .getPackageInfo(ctx.packageName, 0)
        return packageInfo.versionName
    }

    fun openBrowser(url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse(url)
        ctx.startActivity(intent)
    }

    fun isAppInstalled(name: String): Boolean {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = ctx.packageManager.getPackageInfo(name, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return packageInfo != null
    }

    fun downLoadApk(url: String) {
        val intent = Intent()
        with(intent) {
            action = "android.intent.action.VIEW"
            setData(Uri.parse(url))
        }
        ctx.startActivity(intent)
    }

    private fun installAPK(path: String) {
        val apkFile = File(path)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        val fileUri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri =
                FileProvider.getUriForFile(
                    ctx,
                    BuildConfig.LIBRARY_PACKAGE_NAME + ".provider",
                    apkFile
                )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            fileUri = Uri.fromFile(apkFile)
        }
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
        ctx.startActivity(intent)
    }

    fun getLocation() {
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val permissionGPS =
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionGPS == PackageManager.PERMISSION_GRANTED) {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val provider = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                val provider = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        }
    }

    fun setIntervalTask(call: () -> Unit, delay: Long = 1000L, interval: Long = 1000L): Timer {
        val task = timerTask {
            call()
        }
        val timer = Timer()
        timer.schedule(task, delay, interval)
        return timer
    }

    fun setTimeoutTask(call: () -> Unit, timeout: Long = 1000L) {
        val task = timerTask {
            call()
        }
        val timer = Timer()
        timer.schedule(task, timeout)
    }
}