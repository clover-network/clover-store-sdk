package com.sk.cloversdk

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class Decompress(
    private val _zipFile: String,   // the source path of file
    private val _location: String,  // the unzip root path
    private val _project: String    // the unzip dir
) {
    fun unzip(
        del: Boolean = true,
        callback: ((err: Boolean) -> Unit)? = null
    ) {
        try {
            val fin = FileInputStream(_zipFile)
            val zin = ZipInputStream(fin)
            var ze: ZipEntry? = null
            while (zin.nextEntry.also { ze = it } != null) {
                val newFileName =
                    this._project + ze!!.name.substring(ze!!.name.indexOf("/"), ze!!.name.length)
                if (ze!!.isDirectory) {
                    dirChecker(newFileName)
                } else {
                    val fout = FileOutputStream(this._location + "/" + newFileName)
                    val bufout = BufferedOutputStream(fout)
                    val buffer = ByteArray(4096)
                    var read = 0
                    while (zin.read(buffer).also { read = it } != -1) {
                        bufout.write(buffer, 0, read)
                    }
                    bufout.close()
                    zin.closeEntry()
                    fout.close()
                }
            }
            zin.close()
            if (del) File(_zipFile).delete()
            callback?.invoke(true)
        } catch (e: Exception) {
            e.printStackTrace()
            callback?.invoke(false)
        }
    }

    private fun dirChecker(dir: String) {
        val f = File("$_location/$dir")
        if (!f.exists()) {
            f.mkdirs()
        }
    }

    init {
        dirChecker("")
    }
}