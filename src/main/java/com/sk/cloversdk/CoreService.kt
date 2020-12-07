package com.sk.cloversdk

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Nullable
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import java.net.InetAddress
import java.util.concurrent.TimeUnit


class CoreService : Service() {
    private var mServer: Server? = null
    override fun onCreate() {
        createService()
    }

    private fun createService() {
        mServer = AndServer.webServer(this)
            .port(Constant.INNER_SERVER_PORT)
            .timeout(20, TimeUnit.SECONDS)
            .listener(object : Server.ServerListener {
                override fun onStarted() {
//                    try {
//                        val address: InetAddress = IPUtils.getLocalIPAddress()
//                        GlobalVar.ipAddress = address.hostAddress
//                    } catch (e: java.lang.Exception) {
                    GlobalVar.ipAddress = Constant.INNER_SERVER_IP
//                    }
                }

                override fun onStopped() {
                    GlobalVar.ipAddress = ""
                }

                override fun onException(e: Exception?) {
                    GlobalVar.ipAddress = ""
                }
            })
            .build()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startServer()
        return START_STICKY
    }

    override fun onDestroy() {
        stopServer()
        super.onDestroy()
    }


    private fun startServer() {
        mServer?.startup()
    }

    private fun stopServer() {
        mServer?.shutdown()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}