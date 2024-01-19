package yuan.lydia.shoppingappdemo.data.userAuth


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
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

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/java".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService by lazy {
        retrofit.create(ApiServices::class.java)
    }
}