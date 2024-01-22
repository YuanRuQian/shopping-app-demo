package yuan.lydia.shoppingappdemo.data.cart.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartItemEntity::class], version = 1, exportSchema = false)
abstract class CartManagementDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao

    companion object {
        @Volatile
        private var Instance: CartManagementDatabase? = null

        fun getDatabase(context: Context): CartManagementDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CartManagementDatabase::class.java, "cart_wishlist_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}