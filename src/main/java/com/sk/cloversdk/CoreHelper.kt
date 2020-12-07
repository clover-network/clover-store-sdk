package com.sk.cloversdk

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.sk.cloversdk.Constant.H5_OS_FILE_NAME
import java.io.File

const val DEFAULT_PROJECT_VERSION = "0.0.0";

object CoreHelper : BaseHelper() {

    private fun getTxtFromAssets(content: Context, fileName: String): String {
        return content.assets.open(fileName).use {
            it.reader().readText()
        }
    }

    fun projectExist(project: String): Boolean {
        return File(Constant.ROOT_SERVER_PATH + "/$project/index.html").exists()
    }

    fun initOS() {
        val path = Constant.ROOT_SERVER_PATH + "/${Constant.CLOVER_OS_ROOT_NAME}"
        if (!File(path).exists()) {
            // copy from asset
            ctx.assets.open(H5_OS_FILE_NAME).use {
                FileUtils.copyFromAsset(it, "$path.zip")
                Decompress(
                    "$path.zip",
                    Constant.ROOT_SERVER_PATH,
                    Constant.CLOVER_OS_ROOT_NAME
                ).unzip()
            }
        }
    }

    fun uninstallProject(
        project: String,
        callback: ((code: BridgeUninstallCode) -> Unit)? = null
    ) {
        val result = FileUtils.deleteDirectory(Constant.ROOT_SERVER_PATH + "/$project")
        if (result) {
            callback?.invoke(BridgeUninstallCode.SUCCESS)
        } else {
            callback?.invoke(BridgeUninstallCode.NOT_FOUND)
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getUUID(): String {
        val tel = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                Settings.System.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                tel.imei
            }
            else -> {
                tel.deviceId
            }
        }
    }

}