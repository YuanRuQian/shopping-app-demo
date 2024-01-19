package yuan.lydia.shoppingappdemo


import android.app.Application
import yuan.lydia.shoppingappdemo.data.utils.AppContainer
import yuan.lydia.shoppingappdemo.data.utils.DefaultAppContainer


class ShoppingApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}