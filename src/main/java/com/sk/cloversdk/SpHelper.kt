package com.sk.cloversdk

import android.content.Context
import android.content.SharedPreferences

object SpHelper : BaseHelper() {
    const val KEY_IPFS_URL = "key_IPFS_URL"
    const val KEY_IPFS_URL_DEFAULT = "https://ipfs.glpro.io:4010"

    private fun getSp(): SharedPreferences {
        return ctx.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE)
    }

    fun getStr(key: String, default: String = ""): String {
        return getSp().getString(key, default)!!
    }

    fun setStr(key: String, value: String) {
        getSp().edit().putString(key, value).apply()
    }
}