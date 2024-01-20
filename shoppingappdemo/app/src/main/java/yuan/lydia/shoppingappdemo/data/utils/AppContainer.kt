package yuan.lydia.shoppingappdemo.data.utils


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import yuan.lydia.shoppingappdemo.network.shopping.NetworkShoppingRepository
import yuan.lydia.shoppingappdemo.network.shopping.ShoppingApiServices
import yuan.lydia.shoppingappdemo.network.shopping.ShoppingRepository
import yuan.lydia.shoppingappdemo.network.userAuth.NetworkUserAuthRepository
import yuan.lydia.shoppingappdemo.network.userAuth.UserAuthApiServices
import yuan.lydia.shoppingappdemo.network.userAuth.UserAuthRepository


// provide the app with access to the ArtworkRepository as a global state
interface AppContainer {
    val userAuthRepository: UserAuthRepository
    val shoppingRepository: ShoppingRepository
}

class DefaultAppContainer : AppContainer {
    override val userAuthRepository: UserAuthRepository by lazy {
        NetworkUserAuthRepository(userAuthService)
    }

    override val shoppingRepository: ShoppingRepository by lazy {
        NetworkShoppingRepository(shoppingService)
    }


    // FIXME: import base url dynamically from config file or environment variable instead of hardcoding it
    // https://developer.android.com/studio/run/emulator-networking
    // 10.0.0.2 in Android is the special alias to your host loopback interface (127.0.0.1 on your development machine)
    private val baseUrl = "http://10.0.2.2:8080"

    private val logger =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private val serviceBase = Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    private val userAuthService by lazy {
        serviceBase.create(UserAuthApiServices::class.java)
    }

    private val shoppingService by lazy {
        serviceBase.create(ShoppingApiServices::class.java)
    }
}