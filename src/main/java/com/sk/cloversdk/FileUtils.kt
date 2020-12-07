package com.sk.cloversdk

import java.io.*

object FileUtils {;

    fun copyDirectory(src: File, dest: File): Boolean {
        if (!src.isDirectory) {
            return false
        }
        if (!dest.isDirectory && !dest.mkdirs()) {
            return false
        }
        val files: Array<File> = src.listFiles()
        for (file in files) {
            val destFile = File(dest, file.name)
            if (file.isFile) {
                if (!copyFile(file, destFile)) {
                    return false
                }
            } else if (file.isDirectory) {
                if (!copyDirectory(file, destFile)) {
                    return false
                }
            }
        }
        return true
    }

    private fun copyFile(src: File, des: File): Boolean {
        if (!src.exists()) {
            return false
        }
        if (!des.parentFile.isDirectory && !des.parentFile.mkdirs()) {
            return false
        }
        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            bis = BufferedInputStream(FileInputStream(src))
            bos = BufferedOutputStream(FileOutputStream(des))
            val buffer = ByteArray(4 * 1024)
            var count: Int = 0
            while (bis.read(buffer, 0, buffer.size).also { count = it } != -1) {
                if (count > 0) {
                    bos.write(buffer, 0, count)
                }
            }
            bos.flush()
            return true
        } catch (e: Exception) {
        } finally {
            if (bis != null) {
                try {
                    bis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (bos != null) {
                try {
                    bos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }

    fun copyFromAsset(src: InputStream, des: String): Boolean {
        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            bis = BufferedInputStream(src)
            bos = BufferedOutputStream(FileOutputStream(des))
            val buffer = ByteArray(4 * 1024)
            var count: Int = 0
            while (bis.read(buffer, 0, buffer.size).also { count = it } != -1) {
                if (count > 0) {
                    bos.write(buffer, 0, count)
                }
            }
            bos.flush()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (bis != null) {
                try {
                    bis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (bos != null) {
                try {
                    bos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }


    fun deleteDirectory(filePath: String): Boolean {
        var filePath = filePath
        var flag = false
        if (!filePath.endsWith(File.separator)) {
            filePath += File.separator
        }
        val dirFile = File(filePath)
        if (!dirFile.exists() || !dirFile.isDirectory) {
            return false
        }
        flag = true
        val files = dirFile.listFiles()
        for (i in files.indices) {
            if (files[i].isFile) {
                flag = deleteFile(files[i].absolutePath)
                if (!flag) break
            } else {
                flag = deleteDirectory(files[i].absolutePath)
                if (!flag) break
            }
        }
        return if (!flag) false else dirFile.delete()
    }


    private fun deleteFile(filePath: String?): Boolean {
        val file = File(filePath)
        return if (file.isFile && file.exists()) {
            file.delete()
        } else false
    }

}