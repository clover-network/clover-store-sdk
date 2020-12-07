package com.sk.cloversdk

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


object NetUtils {

    private fun getConn(url: String): HttpURLConnection {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = 10 * 1000
        conn.readTimeout = 10 * 1000
        return conn
    }

    fun getStr(url: String, cb: ((err: String?, str: String) -> Unit)? = null) {
        GlobalScope.launch {
            val conn = getConn(url)
            if (conn.responseCode == 200 || conn.responseCode == 201) {
                val text =
                    BufferedReader(InputStreamReader(conn.inputStream)).buffered().readText()
                cb?.invoke(null, text)
            } else {
                cb?.invoke(conn.responseMessage.toString(), "")
            }
            conn.disconnect()
        }
    }

    fun getIPFSFileLength(
        cid: String,
        base: String = Constant.IFPS_BASE_URL,
        cb: ((length: Long) -> Unit)
    ) {
        val url = "${base}/api/v0/file/ls?arg=${cid}"
        GlobalScope.launch {
            var length = 0L
            val conn = getConn(url)
            if (conn.responseCode == 200 || conn.responseCode == 201) {
                try {
                    val text =
                        BufferedReader(InputStreamReader(conn.inputStream)).buffered().readText()
                    val jsonObject = JSONObject(text)
                    val key =
                        jsonObject.getJSONObject("Objects").getJSONObject(cid).getString("Hash")
                    val type =
                        jsonObject.getJSONObject("Objects").getJSONObject(cid).getString("Type")
                    if (key == cid && type == "File") {
                        length = jsonObject.getJSONObject("Objects").getJSONObject(cid)
                            .getLong("Size")
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            conn.disconnect()
            cb(length)
        }

    }

    fun downIPFSFile(
        cid: String,
        path: String,
        base: String = Constant.IFPS_BASE_URL,
        callback: ((err: String?, progress: String,s: Long) -> Unit)? = null
    ) {
        val url = "${base}/api/v0/cat?arg=${cid}"
        getIPFSFileLength(cid) { length ->
            if (length == 0L) {
                callback?.invoke("file not found", "",0L)
            } else {
                GlobalScope.launch {
                    val conn = getConn(url)
                    if (conn.responseCode == 200 || conn.responseCode == 201) {
                        var inputS: InputStream? = null
                        var fos: FileOutputStream? = null
                        val buff = ByteArray(4096)
                        var len: Int = 0
                        var flag: Boolean = true
                        var process = 0f
                        var current: Long = 0
                        var speedKB = 0L
                        var lastCurrent = current
                        val timer = CommHelper.setIntervalTask({
                            speedKB = (current - lastCurrent) / 1024
                            lastCurrent = current
                        })
                        try {
                            inputS = conn.inputStream
                            val file = File(path)
                            if (file.exists()) file.delete()
                            fos = FileOutputStream(file)
                            while (flag) {
                                val startTime = System.currentTimeMillis()
                                len = inputS.read(buff)
                                flag = len != -1
                                if (flag) {
                                    current += len.toLong()
                                    fos.write(buff, 0, len)
                                }
                                process = current.toFloat() / length.toFloat()
                                val endTime = System.currentTimeMillis()
                                var pStr = String.format("%.4f", process)
                                if (process > 0.0001f) pStr =
                                    String.format("%.4f", process - 0.0001f)
                                callback?.invoke(null, pStr,speedKB)
                            }
                            fos.flush()
                            callback?.invoke(null, "1.0000",speedKB)
                        } catch (e: Exception) {
                            File(path).delete()
                            e.printStackTrace()
                            callback?.invoke(conn.responseMessage.toString(), "",speedKB)
                        } finally {;
                            try {
                                inputS?.close()
                                fos?.close()
                                timer.cancel()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        callback?.invoke(conn.responseMessage.toString(), "",0L)
                    }
                    conn.disconnect()
                }
            }
        }
    }
}

