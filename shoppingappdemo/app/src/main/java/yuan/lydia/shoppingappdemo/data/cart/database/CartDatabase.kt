package yuan.lydia.shoppingappdemo.data.cart.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartItemEntity::class], version = 1, exportSchema = false)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao

    companion object {
        @Volatile
        private var Instance: CartDatabase? = null

        fun getDatabase(context: Context): CartDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CartDatabase::class.java, "cart_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}