package com.sk.cloversdk

import android.os.Environment
import android.util.Log
import java.io.File


object LogUtils : BaseHelper() {

    private var logSwitch = true
    private var log2FileSwitch = false
    private var logFilter = 'v'
    private var tag = "TAG"
    private var dir: String? = null

    fun init(logSwitch: Boolean, logFilter: Char, tag: String) {
        dir = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            ctx.externalCacheDir?.path + File.separator
        } else {
            ctx.cacheDir.path + File.separator
        }
        LogUtils.logSwitch = logSwitch
        LogUtils.logFilter = logFilter
        LogUtils.tag = tag
    }

    fun getBuilder(): Builder? {
        dir = if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            ctx.externalCacheDir
                ?.path + File.separator.toString() + "log" + File.separator
        } else {
            ctx.cacheDir
                .path + File.separator.toString() + "log" + File.separator
        }
        return Builder()
    }

    class Builder {
        private var logSwitch = true
        private var log2FileSwitch = false
        private var logFilter = 'v'
        private var tag = "TAG"
        fun setLogSwitch(logSwitch: Boolean): Builder {
            this.logSwitch = logSwitch
            return this
        }

        fun setLog2FileSwitch(log2FileSwitch: Boolean): Builder {
            this.log2FileSwitch = log2FileSwitch
            return this
        }

        fun setLogFilter(logFilter: Char): Builder {
            this.logFilter = logFilter
            return this
        }

        fun setTag(tag: String): Builder {
            this.tag = tag
            return this
        }

        fun create() {
            LogUtils.logSwitch = logSwitch
            LogUtils.log2FileSwitch = log2FileSwitch
            LogUtils.logFilter = logFilter
            LogUtils.tag = tag
        }
    }

    fun v(msg: Any) {
        v(tag, msg)
    }

    fun v(tag: String, msg: Any) {
        v(tag, msg, null)
    }

    fun v(tag: String, msg: Any, tr: Throwable?) {
        log(tag, msg.toString(), tr, 'v')
    }

    fun d(msg: Any) {
        d(tag, msg)
    }

    fun d(tag: String, msg: Any) { // 调试信息
        d(tag, msg, null)
    }

    fun d(tag: String, msg: Any, tr: Throwable?) {
        log(tag, msg.toString(), tr, 'd')
    }

    fun i(msg: Any) {
        i(tag, msg)
    }


    fun i(tag: String, msg: Any) {
        i(tag, msg, null)
    }

    fun i(tag: String, msg: Any, tr: Throwable?) {
        log(tag, msg.toString(), tr, 'i')
    }

    fun w(msg: Any) {
        w(tag, msg)
    }

    fun w(tag: String, msg: Any) {
        w(tag, msg, null)
    }

    fun w(tag: String, msg: Any, tr: Throwable?) {
        log(tag, msg.toString(), tr, 'w')
    }

    fun e(msg: Any) {
        e(tag, msg)
    }

    fun e(tag: String, msg: Any) {
        e(tag, msg, null)
    }

    fun e(tag: String, msg: Any, tr: Throwable?) {
        log(tag, msg.toString(), tr, 'e')
    }

    private fun log(tag: String, msg: String, tr: Throwable?, type: Char) {
        if (logSwitch) {
            if ('e' == type && ('e' == logFilter || 'v' == logFilter)) {
                Log.e(tag, msg, tr)
            } else if ('w' == type && ('w' == logFilter || 'v' == logFilter)) {
                Log.w(tag, msg, tr)
            } else if ('d' == type && ('d' == logFilter || 'v' == logFilter)) {
                Log.d(tag, msg, tr)
            } else if ('i' == type && ('d' == logFilter || 'v' == logFilter)) {
                Log.i(tag, msg, tr)
            }
        }
    }


}