package yuan.lydia.shoppingappdemo.data.utils


import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import yuan.lydia.shoppingappdemo.network.ApiServices
import yuan.lydia.shoppingappdemo.network.userAuth.NetworkUserAuthRepository
import yuan.lydia.shoppingappdemo.network.userAuth.UserAuthRepository


// provide the app with access to the ArtworkRepository as a global state
interface AppContainer {
    val userAuthRepository: UserAuthRepository
}

class DefaultAppContainer : AppContainer {
    override val userAuthRepository: UserAuthRepository by lazy {
        NetworkUserAuthRepository(retrofitService)
    }

    // FIXME: import base url dynamically from config file or environment variable instead of hardcoding it
    // https://developer.android.com/studio/run/emulator-networking
    // 10.0.0.2 in Android is the special alias to your host loopback interface (127.0.0.1 on your development machine)
    private val baseUrl = "http://10.0.2.2:8080"

    private val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    private val retrofitService by lazy {
        retrofit.create(ApiServices::class.java)
    }
}