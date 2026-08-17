package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.models.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

// Representation of a reward option
data class RewardOption(
    val id: String,
    val name: String,
    val pointsCost: Int,
    val associatedItemId: String,
    val imageRes: String
)

enum class Screen {
    HOME, RESTAURANTS, ORDER_TYPE, MENU, PRODUCT_DETAIL, CART, CHECKOUT, TRACKING, REWARDS, ACCOUNT, LOGIN
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    // Navigation Stack State
    private val _navigationStack = MutableStateFlow<List<Screen>>(listOf(Screen.HOME))
    val navigationStack: StateFlow<List<Screen>> = _navigationStack.asStateFlow()

    fun navigateTo(screen: Screen) {
        val current = _navigationStack.value.toMutableList()
        current.add(screen)
        _navigationStack.value = current
    }

    fun navigateBack() {
        val current = _navigationStack.value.toMutableList()
        if (current.size > 1) {
            current.removeAt(current.size - 1)
            _navigationStack.value = current
        }
    }

    fun navigateAndClear(screen: Screen) {
        _navigationStack.value = listOf(screen)
    }

    // Currently Selected Menu Item for Customization
    private val _selectedMenuItem = MutableStateFlow<MenuItemEntity?>(null)
    val selectedMenuItem: StateFlow<MenuItemEntity?> = _selectedMenuItem.asStateFlow()

    fun selectMenuItem(item: MenuItemEntity) {
        _selectedMenuItem.value = item
    }

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(
            restaurantDao = database.restaurantDao(),
            menuItemDao = database.menuItemDao(),
            userProfileDao = database.userProfileDao(),
            cartDao = database.cartDao(),
            orderHistoryDao = database.orderHistoryDao(),
            favoriteDao = database.favoriteDao()
        )
        // Seed database immediately
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // Exposed DB Flows
    val restaurants: StateFlow<List<RestaurantEntity>> = repository.allRestaurants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val menuItems: StateFlow<List<MenuItemEntity>> = repository.allMenuItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orderHistory: StateFlow<List<OrderHistoryEntity>> = repository.orderHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<FavoriteEntity>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active User Selection States
    private val _selectedRestaurant = MutableStateFlow<RestaurantEntity?>(null)
    val selectedRestaurant: StateFlow<RestaurantEntity?> = _selectedRestaurant.asStateFlow()

    private val _selectedOrderType = MutableStateFlow<String>("PICKUP") // PICKUP, DRIVE_THRU, CURBSIDE, DINE_IN, DELIVERY
    val selectedOrderType: StateFlow<String> = _selectedOrderType.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String>("ENTREES")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Interactive Customization States
    private val _customizationBun = MutableStateFlow<String>("Standard Brioche Bun")
    val customizationBun: StateFlow<String> = _customizationBun.asStateFlow()

    private val _customizationPickles = MutableStateFlow<Boolean>(true)
    val customizationPickles: StateFlow<Boolean> = _customizationPickles.asStateFlow()

    private val _customizationSpicyExtra = MutableStateFlow<Boolean>(false)
    val customizationSpicyExtra: StateFlow<Boolean> = _customizationSpicyExtra.asStateFlow()

    private val _customizationSauces = MutableStateFlow<Map<String, Int>>(emptyMap()) // SauceId -> Quantity
    val customizationSauces: StateFlow<Map<String, Int>> = _customizationSauces.asStateFlow()

    // Meal Builder States
    private val _mealSelectedSide = MutableStateFlow<MenuItemEntity?>(null)
    val mealSelectedSide: StateFlow<MenuItemEntity?> = _mealSelectedSide.asStateFlow()

    private val _mealSelectedSideSize = MutableStateFlow<String>("Medium")
    val mealSelectedSideSize: StateFlow<String> = _mealSelectedSideSize.asStateFlow()

    private val _mealSelectedDrink = MutableStateFlow<MenuItemEntity?>(null)
    val mealSelectedDrink: StateFlow<MenuItemEntity?> = _mealSelectedDrink.asStateFlow()

    private val _mealSelectedDrinkSize = MutableStateFlow<String>("Medium")
    val mealSelectedDrinkSize: StateFlow<String> = _mealSelectedDrinkSize.asStateFlow()

    private val _mealSelectedDrinkIce = MutableStateFlow<String>("Regular Ice")
    val mealSelectedDrinkIce: StateFlow<String> = _mealSelectedDrinkIce.asStateFlow()

    // Search Query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active tracking order state
    private val _activeOrder = MutableStateFlow<OrderHistoryEntity?>(null)
    val activeOrder: StateFlow<OrderHistoryEntity?> = _activeOrder.asStateFlow()

    // Available loyalty reward redemptions
    val rewardOptions = listOf(
        RewardOption("rew_fries", "Medium Waffle Fries", 200, "item_side_waffle_fries", "img_hero_banner"),
        RewardOption("rew_lemonade", "Classic Fresh Lemonade", 300, "item_drink_lemonade", ""),
        RewardOption("rew_nuggets", "8 Count Nuggets", 500, "item_nuggets_8pc", "img_nuggets"),
        RewardOption("rew_sandwich", "Classic Chicken Sandwich", 600, "item_chicken_sandwich", "img_hero_banner"),
        RewardOption("rew_shake", "Strawberry Milkshake", 400, "item_drink_milkshake_strawberry", "img_milkshake")
    )

    // Applied cart discount from loyalty reward redemption
    private val _appliedDiscount = MutableStateFlow<Double>(0.0)
    val appliedDiscount: StateFlow<Double> = _appliedDiscount.asStateFlow()

    private val _appliedRewardItemName = MutableStateFlow<String?>(null)
    val appliedRewardItemName: StateFlow<String?> = _appliedRewardItemName.asStateFlow()

    // Selection handlers
    fun selectRestaurant(restaurant: RestaurantEntity) {
        _selectedRestaurant.value = restaurant
        // Auto-update user's preferred restaurant in DB
        viewModelScope.launch {
            userProfile.value?.let { profile ->
                repository.updateUserProfile(profile.copy(favoriteRestaurantId = restaurant.id))
            }
        }
    }

    fun selectOrderType(type: String) {
        _selectedOrderType.value = type
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Customization mutators
    fun setCustomizationBun(bun: String) {
        _customizationBun.value = bun
    }

    fun setCustomizationPickles(enabled: Boolean) {
        _customizationPickles.value = enabled
    }

    fun setCustomizationSpicyExtra(enabled: Boolean) {
        _customizationSpicyExtra.value = enabled
    }

    fun adjustSauceQty(sauceId: String, delta: Int) {
        val current = _customizationSauces.value.toMutableMap()
        val qty = (current[sauceId] ?: 0) + delta
        if (qty <= 0) {
            current.remove(sauceId)
        } else {
            current[sauceId] = qty
        }
        _customizationSauces.value = current
    }

    fun clearCustomizations() {
        _customizationBun.value = "Standard Brioche Bun"
        _customizationPickles.value = true
        _customizationSpicyExtra.value = false
        _customizationSauces.value = emptyMap()
    }

    // Meal Builder mutators
    fun setMealSide(side: MenuItemEntity?) {
        _mealSelectedSide.value = side
    }

    fun setMealSideSize(size: String) {
        _mealSelectedSideSize.value = size
    }

    fun setMealDrink(drink: MenuItemEntity?) {
        _mealSelectedDrink.value = drink
    }

    fun setMealDrinkSize(size: String) {
        _mealSelectedDrinkSize.value = size
    }

    fun setMealDrinkIce(ice: String) {
        _mealSelectedDrinkIce.value = ice
    }

    fun clearMealSelections() {
        _mealSelectedSide.value = null
        _mealSelectedSideSize.value = "Medium"
        _mealSelectedDrink.value = null
        _mealSelectedDrinkSize.value = "Medium"
        _mealSelectedDrinkIce.value = "Regular Ice"
    }

    // Cart Operations
    fun addProductToCart(
        item: MenuItemEntity,
        quantity: Int,
        isMealBuild: Boolean
    ) {
        viewModelScope.launch {
            // Build customizations text
            val builder = StringBuilder()
            if (!item.isMeal && !isMealBuild) {
                if (_customizationBun.value != "Standard Brioche Bun") {
                    builder.append("Bun: ${_customizationBun.value}, ")
                }
                if (!_customizationPickles.value) {
                    builder.append("No Pickles, ")
                }
                if (_customizationSpicyExtra.value) {
                    builder.append("Extra Spicy Spice, ")
                }
                _customizationSauces.value.forEach { (sauceId, qty) ->
                    val sauceName = sauceId.replace("item_sauce_", "").replace("_", " ").capitalize()
                    builder.append("+$qty $sauceName Sauce, ")
                }
            }

            var customText = builder.toString().trim()
            if (customText.endsWith(",")) {
                customText = customText.dropLast(1)
            }

            var totalPrice = item.price
            var sideId: String? = null
            var sideSize: String? = null
            var drinkId: String? = null
            var drinkSize: String? = null
            var drinkIce: String? = null

            if (isMealBuild) {
                val side = _mealSelectedSide.value
                val drink = _mealSelectedDrink.value
                sideId = side?.id
                sideSize = _mealSelectedSideSize.value
                drinkId = drink?.id
                drinkSize = _mealSelectedDrinkSize.value
                drinkIce = _mealSelectedDrinkIce.value

                // Meal price markup
                val sideMarkup = if (_mealSelectedSideSize.value == "Large") 0.50 else 0.00
                val drinkMarkup = if (_mealSelectedDrinkSize.value == "Large") 0.40 else 0.00
                totalPrice += 3.20 + sideMarkup + drinkMarkup // Flat $3.20 combo premium
            }

            val cartEntity = CartItemEntity(
                itemId = item.id,
                name = if (isMealBuild) "${item.name} Meal" else item.name,
                price = totalPrice,
                quantity = quantity,
                customizationsJson = customText.ifEmpty { "Standard preparation" },
                isMeal = isMealBuild,
                mealEntreeId = if (isMealBuild) item.id else null,
                mealSideId = sideId,
                mealSideSize = sideSize,
                mealDrinkId = drinkId,
                mealDrinkSize = drinkSize,
                mealDrinkIce = drinkIce
            )

            repository.insertCartItem(cartEntity)
            clearCustomizations()
            clearMealSelections()
        }
    }

    fun updateCartItemQty(cartItemId: Int, newQty: Int) {
        viewModelScope.launch {
            if (newQty <= 0) {
                repository.deleteCartItemById(cartItemId)
            } else {
                val cart = repository.cartItems.first()
                val match = cart.find { it.id == cartItemId }
                if (match != null) {
                    repository.insertCartItem(match.copy(quantity = newQty))
                }
            }
        }
    }

    fun removeCartItem(cartItemId: Int) {
        viewModelScope.launch {
            repository.deleteCartItemById(cartItemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
            _appliedDiscount.value = 0.0
            _appliedRewardItemName.value = null
        }
    }

    // Toggle Favorites
    fun toggleFavorite(type: String, targetId: String, name: String) {
        viewModelScope.launch {
            val favs = repository.favorites.first()
            val match = favs.find { it.type == type && it.targetId == targetId }
            if (match != null) {
                repository.deleteFavoriteById(match.id)
            } else {
                repository.insertFavorite(
                    FavoriteEntity(type = type, targetId = targetId, name = name)
                )
            }
        }
    }

    // Loyalty Rewards Redemption
    fun redeemReward(option: RewardOption) {
        viewModelScope.launch {
            val profile = repository.userProfile.first() ?: return@launch
            if (profile.pointsBalance >= option.pointsCost) {
                // Deduct loyalty points
                val updatedBalance = profile.pointsBalance - option.pointsCost
                repository.updateUserProfile(profile.copy(pointsBalance = updatedBalance))

                // Query associated item to apply discount or add to cart for free!
                val item = repository.getMenuItemById(option.associatedItemId)
                if (item != null) {
                    // Set discount to cover the item's price!
                    _appliedDiscount.value = item.price
                    _appliedRewardItemName.value = item.name

                    // Insert the item into cart immediately for $0 price
                    val freeItem = CartItemEntity(
                        itemId = item.id,
                        name = "${item.name} (Reward)",
                        price = 0.00,
                        quantity = 1,
                        customizationsJson = "Redeemed with loyalty rewards",
                        isMeal = false
                    )
                    repository.insertCartItem(freeItem)
                }
            }
        }
    }

    // Reorder flow
    fun reorder(order: OrderHistoryEntity) {
        viewModelScope.launch {
            repository.clearCart()
            // In a real app we'd parse order.itemsJson, let's look up matching items and re-add them
            // For safety in demo, let's add a custom item representing the previous order
            val reorderItem = CartItemEntity(
                itemId = "reorder_${order.id}",
                name = "Reordered: ${order.restaurantName} Meal",
                price = order.subtotal,
                quantity = 1,
                customizationsJson = "Includes previous items",
                isMeal = false
            )
            repository.insertCartItem(reorderItem)
        }
    }

    // Submit Order & Active Tracker Simulation
    fun submitOrder(
        paymentMethod: String,
        scheduledTime: String? = null,
        curbsideInfo: String? = null,
        deliveryAddress: String? = null
    ) {
        viewModelScope.launch {
            val currentCart = repository.cartItems.first()
            if (currentCart.isEmpty()) return@launch

            val rest = _selectedRestaurant.value ?: return@launch
            val subtotal = currentCart.sumOf { it.price * it.quantity } - _appliedDiscount.value
            val tax = subtotal * 0.08
            val deliveryFee = if (_selectedOrderType.value == "DELIVERY") 3.99 else 0.0
            val total = subtotal + tax + deliveryFee

            // Earn loyalty points: 10 points per dollar spent!
            val earnedPoints = (subtotal * 10).toInt()
            val profile = repository.userProfile.first()
            if (profile != null) {
                repository.updateUserProfile(profile.copy(pointsBalance = profile.pointsBalance + earnedPoints))
            }

            // Create historic order record
            val orderId = "ORD-${UUID.randomUUID().toString().take(6).uppercase()}"
            val itemsSummary = currentCart.joinToString(", ") { "${it.quantity}x ${it.name}" }

            val order = OrderHistoryEntity(
                id = orderId,
                timestamp = System.currentTimeMillis(),
                restaurantId = rest.id,
                restaurantName = rest.name,
                itemsJson = itemsSummary,
                subtotal = subtotal.coerceAtLeast(0.0),
                tax = tax.coerceAtLeast(0.0),
                deliveryFee = deliveryFee,
                total = total.coerceAtLeast(0.0),
                orderType = _selectedOrderType.value,
                status = "RECEIVED",
                pickupDetailsJson = curbsideInfo ?: scheduledTime,
                deliveryAddress = deliveryAddress
            )

            // Insert order & clear cart
            repository.insertOrder(order)
            repository.clearCart()

            // Start Order Status Tracking Simulation
            _activeOrder.value = order
            simulateOrderStatusUpdates(orderId)
        }
    }

    private fun simulateOrderStatusUpdates(orderId: String) {
        viewModelScope.launch {
            // RECEIVED -> PREPARING
            delay(5000)
            repository.updateOrderStatus(orderId, "PREPARING")
            _activeOrder.value = _activeOrder.value?.copy(status = "PREPARING")

            // PREPARING -> READY
            delay(8000)
            repository.updateOrderStatus(orderId, "READY")
            _activeOrder.value = _activeOrder.value?.copy(status = "READY")

            // READY -> COMPLETED / PICKED UP
            delay(12000)
            repository.updateOrderStatus(orderId, "COMPLETED")
            _activeOrder.value = _activeOrder.value?.copy(status = "COMPLETED")
        }
    }

    fun completeActiveOrder() {
        _activeOrder.value = null
    }
}
