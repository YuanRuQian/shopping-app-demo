package yuan.lydia.shoppingappdemo.network

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser

fun extractErrorBody(e: retrofit2.HttpException): String {
    val errorCode = e.code()
    val errorMessage = e.message()
    val errorBody = e.response()?.errorBody()?.string()

    Log.d("extractErrorBody", "network error: $e")
    Log.d("extractErrorBody", "network error code: $errorCode")
    Log.d("extractErrorBody", "network error message: $errorMessage")
    Log.d("extractErrorBody", "network error body: $errorBody")

    return errorBody?.let {
        try {
            val jsonError = JsonParser.parseString(it) as JsonObject
            jsonError.getAsJsonPrimitive("message")?.asString
        } catch (e: Exception) {
            null
        }
    } ?: "No additional error information available."
}