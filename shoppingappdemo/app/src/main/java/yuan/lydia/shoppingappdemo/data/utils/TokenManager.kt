package yuan.lydia.shoppingappdemo.data.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class TokenManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "token"

        // Singleton instance
        @Volatile
        private var instance: TokenManager? = null

        // Create or return the existing instance
        fun getInstance(context: Context): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager(context).also { instance = it }
            }
        }
    }

    fun saveToken(token: String) {
        Log.d("TokenManager", "saveToken: $token")
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }

    fun isTokenExist(): Boolean {
        return sharedPreferences.contains(KEY_TOKEN)
    }
}
