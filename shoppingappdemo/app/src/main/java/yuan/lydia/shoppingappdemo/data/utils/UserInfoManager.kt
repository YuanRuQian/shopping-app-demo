package yuan.lydia.shoppingappdemo.data.utils

import android.content.Context
import android.content.SharedPreferences

class UserInfoManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserInfoPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USERNAME = "username"

        // Singleton instance
        @Volatile
        private var instance: UserInfoManager? = null

        // Create or return the existing instance
        fun getInstance(context: Context): UserInfoManager {
            return instance ?: synchronized(this) {
                instance ?: UserInfoManager(context).also { instance = it }
            }
        }
    }

    fun saveToken(token: String) {
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

    fun saveUsername(username: String) {
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun clearUsername() {
        sharedPreferences.edit().remove(KEY_USERNAME).apply()
    }
}

