package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val distance: Double,
    val isOpen: Boolean,
    val closesAt: String,
    val opensAt: String,
    val estimatedPrepTime: Int,
    val pickupAvailable: Boolean,
    val driveThruAvailable: Boolean,
    val curbsideAvailable: Boolean,
    val deliveryAvailable: Boolean,
    val specialInfo: String
)

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val category: String,
    val name: String,
    val description: String,
    val price: Double,
    val calories: Int,
    val fat: String,
    val carbs: String,
    val protein: String,
    val sodium: String,
    val ingredients: String,
    val allergens: String,
    val imageRes: String,
    val availability: String, // AVAILABLE, LIMITED, UNAVAILABLE, BREAKFAST_ONLY, SEASONAL
    val isLimitedTime: Boolean,
    val isMeal: Boolean
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "current_user",
    val name: String,
    val email: String,
    val phone: String,
    val pointsBalance: Int,
    val favoriteRestaurantId: String? = null
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val customizationsJson: String, // JSON serialization of customizations
    val isMeal: Boolean,
    val mealEntreeId: String? = null,
    val mealSideId: String? = null,
    val mealSideSize: String? = null,
    val mealDrinkId: String? = null,
    val mealDrinkSize: String? = null,
    val mealDrinkIce: String? = null
)

@Entity(tableName = "order_history")
data class OrderHistoryEntity(
    @PrimaryKey val id: String, // e.g. ORD-10294
    val timestamp: Long,
    val restaurantId: String,
    val restaurantName: String,
    val itemsJson: String, // JSON serialization of items ordered
    val subtotal: Double,
    val tax: Double,
    val deliveryFee: Double,
    val total: Double,
    val orderType: String, // PICKUP, DRIVE_THRU, CURBSIDE, DINE_IN, DELIVERY
    val status: String, // RECEIVED, PREPARING, READY, COMPLETED
    val pickupDetailsJson: String? = null, // e.g. curbside vehicle info or scheduled time
    val deliveryAddress: String? = null
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // ITEM, MEAL, RESTAURANT
    val targetId: String, // MenuItem id or Restaurant id
    val name: String,
    val detailsJson: String? = null // For customized items/meals
)
