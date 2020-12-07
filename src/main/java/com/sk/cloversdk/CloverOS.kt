package com.sk.cloversdk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.sk.cloversdk.Constant.JS_BRIDGE_INS_CONTENT_DIR
import com.sk.cloversdk.Constant.OS_CONTENT_DIR
import org.json.JSONObject
import java.io.File
import java.util.*

object CloverOS {
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var webView: WebView

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initHelper() {
        CoreHelper.ctx = context
        ClipBoardHelper.ctx = context
        CommHelper.ctx = context;
        SpHelper.ctx = context
        LogUtils.ctx = context

        CoreHelper.activity = activity
        ClipBoardHelper.activity = activity
        CommHelper.activity = activity;
        SpHelper.activity = activity
        LogUtils.activity = activity
        LogUtils.init(false, 'v', "clover")
        Constant.ROOT_SERVER_PATH =
            context.applicationContext.dataDir.absolutePath + "/${OS_CONTENT_DIR}"
        val file = File(Constant.ROOT_SERVER_PATH)
        if (!file.exists()) file.mkdirs()
        Constant.IFPS_BASE_URL =
            SpHelper.getStr(SpHelper.KEY_IPFS_URL, SpHelper.KEY_IPFS_URL_DEFAULT)
    }

    fun onStartOS() {
        if (TextUtils.isEmpty(GlobalVar.ipAddress)) {
            context.startService(Intent(context, CoreService::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetJavaScriptEnabled")
    fun initOS(ctx: Context, act: Activity, view: WebView) {
        val bridge = JsBridge(view)
        context = ctx
        activity = act
        webView = view
        bridge.ctx = ctx
        bridge.activity = activity
        initHelper()
        CoreHelper.initOS()
        onStartOS()
        view.addJavascriptInterface(bridge, JS_BRIDGE_INS_CONTENT_DIR)
        view.settings.javaScriptEnabled = true
        view.settings.domStorageEnabled = true
        view.settings.cacheMode = WebSettings.LOAD_DEFAULT
        view.settings.setAppCacheEnabled(true)
        view.settings.allowFileAccessFromFileURLs = true
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request != null) {
                    bridge.callJs(
                        "jumpWeb",
                        Gson().toJson(COMM_RESPONSE(data = request.url.toString()))
                    )
                }
                return true
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                //TODO
                return super.shouldInterceptRequest(view, request)
            }
        }
        var timer: Timer? = null
        timer = CommHelper.setIntervalTask({
            if (!TextUtils.isEmpty(GlobalVar.ipAddress)) {
                bridge.handleOSRestart(JSONObject("{}"), "")
                timer?.cancel()
            }
        }, 200)
    }
}