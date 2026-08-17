package com.example.data.dao

import androidx.room.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurants")
    fun getAllRestaurantsFlow(): Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM restaurants")
    suspend fun getAllRestaurants(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE id = :id")
    suspend fun getRestaurantById(id: String): RestaurantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(restaurants: List<RestaurantEntity>)
}

@Dao
interface MenuItemDao {
    @Query("SELECT * FROM menu_items")
    fun getAllMenuItemsFlow(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items")
    suspend fun getAllMenuItems(): List<MenuItemEntity>

    @Query("SELECT * FROM menu_items WHERE id = :id")
    suspend fun getMenuItemById(id: String): MenuItemEntity?

    @Query("SELECT * FROM menu_items WHERE category = :category")
    suspend fun getMenuItemsByCategory(category: String): List<MenuItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItemEntity>)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 'current_user'")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 'current_user'")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserProfileEntity)

    @Query("UPDATE user_profile SET pointsBalance = :points WHERE id = 'current_user'")
    suspend fun updatePoints(points: Int)
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItemsFlow(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items")
    suspend fun getCartItems(): List<CartItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItemById(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface OrderHistoryDao {
    @Query("SELECT * FROM order_history ORDER BY timestamp DESC")
    fun getAllOrdersFlow(): Flow<List<OrderHistoryEntity>>

    @Query("SELECT * FROM order_history ORDER BY timestamp DESC")
    suspend fun getAllOrders(): List<OrderHistoryEntity>

    @Query("SELECT * FROM order_history WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderHistoryEntity)

    @Query("UPDATE order_history SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavoritesFlow(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)

    @Query("DELETE FROM favorites WHERE type = :type AND targetId = :targetId")
    suspend fun deleteFavorite(type: String, targetId: String)
}
