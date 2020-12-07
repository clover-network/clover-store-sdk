package com.sk.cloversdk

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipBoardHelper : BaseHelper() {

    fun copy(text: String) {
        val cmb = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cmb.setPrimaryClip(ClipData.newPlainText("cloverOS", text))
    }

    fun paste(): String {
        val cmb = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (!cmb.hasPrimaryClip()) return ""
        return cmb.primaryClip?.getItemAt(0)?.text.toString()
    }

}