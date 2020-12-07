package com.sk.cloversdk

data class COMM_RESPONSE(
    val data:String="{}",
    val code: Int = 1000,
    val message: String = "success"
)

data class COPY(
    val data: Data,
    val code: Int = 20000,
    val message: String = "success"
) {
    data class Data(
        val url: String,
        val forced: String
    )
}

data class OS_VERSION(
    val data: Data,
    val code: Int = 20000,
    val message: String = "success"
) {
    data class Data(
        val inner: String,
        val outer: String,
        val sdk: String,
        val apk: String
    )
}

data class INSTALL_APP(
    val code: Int = 20000,
    val data: INSTALL_APP.Data,
    val message: String = "success"
) {
    data class Data(
        val process: Float,
        val speedKB:Long,
        var openUrl:String =""
    )
}



