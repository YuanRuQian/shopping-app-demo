package yuan.lydia.shoppingappdemo.data.cartWishlistManagement

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.daos.CartItemDao
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.daos.WishlistItemDao
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.CartItemEntity
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.WishlistItemEntity

@Database(entities = [CartItemEntity::class, WishlistItemEntity::class], version = 1, exportSchema = false)
abstract class CartWishlistManagementDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao

    abstract fun wishlistItemDao(): WishlistItemDao

    companion object {
        @Volatile
        private var Instance: CartWishlistManagementDatabase? = null

        fun getDatabase(context: Context): CartWishlistManagementDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CartWishlistManagementDatabase::class.java, "cart_wishlist_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}