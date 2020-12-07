package com.sk.cloversdk

import android.text.TextUtils
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import com.sk.cloversdk.CoreHelper.projectExist
import org.json.JSONException
import org.json.JSONObject
import java.io.File

//  val str = Gson().toJson(COPY(data = COPY.Data("11", "222")))
// val copy: COPY = fromJson(str)
inline fun <reified T : Any> fromJson(json: String): T {
    return Gson().fromJson(json, T::class.java)
}

class JsBridge(view: WebView) : BaseHelper() {
    var cloverOSBridge: WebView = view

    fun callJs(methodName: String, data: String) {
        val js = "javascript:${methodName}(\'${data}\')"
        cloverOSBridge.post {
            cloverOSBridge.loadUrl(js)
        }
    }

    @JavascriptInterface
    fun callMessage(methodName: String, params: String, callbackName: String) {
        Log.d("receiver msg", "$params---$callbackName")
        val obj: JSONObject
        try {
            obj = JSONObject(params)
        } catch (e: JSONException) {
            callJs(
                callbackName,
                Gson().toJson(COMM_RESPONSE(code = 100, message = "json params parse error"))
            )
            return
        }
        when (methodName) {
            "copy" -> handleCopy(obj, callbackName)
            "getClipboard" -> handleClipboard(obj, callbackName)
            "checkApp" -> handleCheckApp(obj, callbackName)
            "getUUID" -> handleUUID(obj, callbackName)
            "installApp" -> handleInstallApp(obj, callbackName)
            "uninstall" -> handleUninstall(obj, callbackName)
            "checkServe" -> handleCheckServe(obj, callbackName)
            "OSRestart" -> handleOSRestart(obj, callbackName)
            "openBrowserPage" -> handleOpenBrowserPage(obj, callbackName)
            "customIPFSUrl" -> {
                var base = obj.getString("url")
                if (base.endsWith("/")) base = base.substring(0, -1)
                NetUtils.getIPFSFileLength(Constant.OS_BASE_RESOURCE, base) {
                    if (it > 0L) {
                        Constant.IFPS_BASE_URL = base
                        SpHelper.setStr(SpHelper.KEY_IPFS_URL, base)
                    }
                }
            }

        }
    }

    fun handleOSRestart(obj: JSONObject, callbackName: String) {
        activity.runOnUiThread {
//            cloverOSBridge.loadUrl("http://10.10.41.48:3003/index.html")
            cloverOSBridge.loadUrl(
                Constant.INNER_SERVER_IP + ":"
                        + Constant.INNER_SERVER_PORT
                        + File.separator
                        + Constant.CLOVER_OS_ROOT_NAME + "/index.html"
            )
        }
    }

    private fun handleOpenBrowserPage(obj: JSONObject, callbackName: String) {
        val path = obj.getString("path")
        CommHelper.openBrowser(path)
    }

    private fun handleCheckServe(obj: JSONObject, callbackName: String) {
        val code = if (TextUtils.isEmpty(GlobalVar.ipAddress)) 1002 else 1001
        callJs(callbackName, Gson().toJson(COMM_RESPONSE(code = code)))
    }

    private fun handleUUID(obj: JSONObject, callbackName: String) {
        callJs(
            callbackName,
            Gson().toJson(COMM_RESPONSE(code = 601, data = CoreHelper.getUUID()))
        )

    }

    private fun handleCheckApp(obj: JSONObject, callbackName: String) {
        val project = obj.getString("hashId")
        if (projectExist(project)) {
            callJs(callbackName, Gson().toJson(COMM_RESPONSE(code = 901)))
        } else {
            callJs(
                callbackName, Gson().toJson(
                    COMM_RESPONSE(
                        message = "not found $project",
                        code = 902
                    )
                )
            )
        }
    }

    private fun handleClipboard(obj: JSONObject, callbackName: String) {
        callJs(callbackName, Gson().toJson(COMM_RESPONSE(code = 1, data = ClipBoardHelper.paste())))
    }

    private fun handleCopy(obj: JSONObject, callbackName: String) {
        val value = obj.getString("value")
        ClipBoardHelper.copy(value)
        callJs(callbackName, Gson().toJson(COMM_RESPONSE(code = 701)))
    }

    private fun handleUninstall(obj: JSONObject, callbackName: String) {
        val project = obj.getString("hashId")
        CoreHelper.uninstallProject(project) {
            callJs(callbackName, Gson().toJson(COMM_RESPONSE(code = it.code)))
        }
    }

    private fun handleInstallApp(obj: JSONObject, callbackName: String) {
        val project = obj.getString("hashId")
        val cid = obj.getString("sourceId")
        val file = Constant.ROOT_SERVER_PATH + "/${cid}.zip"
        NetUtils.downIPFSFile(cid, file) { err: String?, progress: String, speed: Long ->
            if (err == null) {
                callJs(
                    callbackName, Gson().toJson(
                        INSTALL_APP(
                            code = 101,
                            message = "downing",
                            data = INSTALL_APP.Data(progress.toFloat(), speed)
                        )
                    )
                )
                if (progress.toFloat() == 1.0000f) {
                    callJs(
                        callbackName, Gson().toJson(
                            INSTALL_APP(
                                code = 102,
                                message = "download success",
                                data = INSTALL_APP.Data(progress.toFloat(), speed)
                            )
                        )
                    )
                    callJs(
                        callbackName, Gson().toJson(
                            INSTALL_APP(
                                code = 201,
                                message = "start install application",
                                data = INSTALL_APP.Data(progress.toFloat(), speed)
                            )
                        )
                    )
                    Decompress(file, Constant.ROOT_SERVER_PATH, project).unzip() {
                        if (it) {
                            callJs(
                                callbackName, Gson().toJson(
                                    INSTALL_APP(
                                        code = 203,
                                        message = "install application success",
                                        data = INSTALL_APP.Data(
                                            progress.toFloat(),
                                            speed,
                                            openUrl = "${Constant.INNER_SERVER_IP}:${Constant.INNER_SERVER_PORT}/$project/"
                                        )
                                    )
                                )
                            )
                        } else {
                            callJs(
                                callbackName, Gson().toJson(
                                    INSTALL_APP(
                                        code = 202,
                                        message = "install application fail",
                                        data = INSTALL_APP.Data(progress.toFloat(), speed)
                                    )
                                )
                            )
                        }
                    }
                }
            } else {
                callJs(
                    callbackName, Gson().toJson(
                        INSTALL_APP(
                            code = 103,
                            message = err,
                            data = INSTALL_APP.Data(0.0f, speed)
                        )
                    )
                )
            }
        }
    }
}