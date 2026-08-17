package com.example.data.repository

import com.example.data.dao.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(
    private val restaurantDao: RestaurantDao,
    private val menuItemDao: MenuItemDao,
    private val userProfileDao: UserProfileDao,
    private val cartDao: CartDao,
    private val orderHistoryDao: OrderHistoryDao,
    private val favoriteDao: FavoriteDao
) {
    // Flow observables
    val allRestaurants: Flow<List<RestaurantEntity>> = restaurantDao.getAllRestaurantsFlow()
    val allMenuItems: Flow<List<MenuItemEntity>> = menuItemDao.getAllMenuItemsFlow()
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfileFlow()
    val cartItems: Flow<List<CartItemEntity>> = cartDao.getCartItemsFlow()
    val orderHistory: Flow<List<OrderHistoryEntity>> = orderHistoryDao.getAllOrdersFlow()
    val favorites: Flow<List<FavoriteEntity>> = favoriteDao.getAllFavoritesFlow()

    // Seeds data if necessary
    suspend fun seedDatabaseIfEmpty() {
        // 1. Seed Restaurants
        val currentRestaurants = restaurantDao.getAllRestaurants()
        if (currentRestaurants.isEmpty()) {
            val dummyRestaurants = listOf(
                RestaurantEntity(
                    id = "rest_downtown",
                    name = "QuickBite Downtown",
                    address = "123 Peachtree St NE, Atlanta, GA 30303",
                    phone = "(404) 555-0101",
                    distance = 0.8,
                    isOpen = true,
                    closesAt = "10:00 PM",
                    opensAt = "6:30 AM",
                    estimatedPrepTime = 12,
                    pickupAvailable = true,
                    driveThruAvailable = true,
                    curbsideAvailable = true,
                    deliveryAvailable = true,
                    specialInfo = "Dine-in currently closing at 9:00 PM. Drive-thru open till 10:00 PM."
                ),
                RestaurantEntity(
                    id = "rest_airport",
                    name = "QuickBite Airport Concourse T",
                    address = "6000 N Terminal Pkwy, Atlanta, GA 30320",
                    phone = "(404) 555-0102",
                    distance = 8.5,
                    isOpen = true,
                    closesAt = "9:00 PM",
                    opensAt = "5:00 AM",
                    estimatedPrepTime = 8,
                    pickupAvailable = true,
                    driveThruAvailable = false,
                    curbsideAvailable = false,
                    deliveryAvailable = false,
                    specialInfo = "Mobile ordering pickup shelf located near Concourse T security exit."
                ),
                RestaurantEntity(
                    id = "rest_university",
                    name = "QuickBite University Campus",
                    address = "750 Ferst Dr, Atlanta, GA 30332",
                    phone = "(404) 555-0103",
                    distance = 1.2,
                    isOpen = true,
                    closesAt = "8:00 PM",
                    opensAt = "7:00 AM",
                    estimatedPrepTime = 15,
                    pickupAvailable = true,
                    driveThruAvailable = false,
                    curbsideAvailable = true,
                    deliveryAvailable = true,
                    specialInfo = "Enjoy 20% off breakfast meals with valid student ID card scan."
                ),
                RestaurantEntity(
                    id = "rest_northside",
                    name = "QuickBite Northside",
                    address = "3456 Northside Pkwy NW, Atlanta, GA 30327",
                    phone = "(404) 555-0104",
                    distance = 4.2,
                    isOpen = true,
                    closesAt = "10:00 PM",
                    opensAt = "6:00 AM",
                    estimatedPrepTime = 10,
                    pickupAvailable = true,
                    driveThruAvailable = true,
                    curbsideAvailable = true,
                    deliveryAvailable = true,
                    specialInfo = "New multi-lane drive-thru structure active. Park in curbside spots 1-10."
                ),
                RestaurantEntity(
                    id = "rest_westend",
                    name = "QuickBite West End",
                    address = "890 Ralph David Abernathy Blvd, Atlanta, GA 30310",
                    phone = "(404) 555-0105",
                    distance = 3.1,
                    isOpen = false,
                    closesAt = "10:00 PM",
                    opensAt = "6:30 AM",
                    estimatedPrepTime = 14,
                    pickupAvailable = true,
                    driveThruAvailable = true,
                    curbsideAvailable = false,
                    deliveryAvailable = true,
                    specialInfo = "Closed today for scheduled maintenance. Reopening tomorrow morning at 6:30 AM."
                )
            )
            restaurantDao.insertRestaurants(dummyRestaurants)
        }

        // 2. Seed Menu Items
        val currentMenuItems = menuItemDao.getAllMenuItems()
        if (currentMenuItems.isEmpty()) {
            val dummyMenuItems = listOf(
                // Chicken Sandwiches
                MenuItemEntity(
                    id = "item_chicken_sandwich",
                    category = "ENTREES",
                    name = "Classic Chicken Sandwich",
                    description = "A boneless breast of chicken seasoned to perfection, freshly breaded, pressure cooked in 100% refined peanut oil and served on a toasted, buttered bun with dill pickle chips.",
                    price = 4.79,
                    calories = 440,
                    fat = "19g",
                    carbs = "41g",
                    protein = "28g",
                    sodium = "1400mg",
                    ingredients = "Chicken Breast (boneless, skinless), Water, Seasoning (salt, monosodium glutamate, sugar, spices, paprika), Bun (flour, water, sugar, yeast, butter), Dill Pickles.",
                    allergens = "Wheat, Milk, Soy, Egg, Peanut (oil)",
                    imageRes = "img_hero_banner",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                MenuItemEntity(
                    id = "item_spicy_sandwich",
                    category = "ENTREES",
                    name = "Spicy Chicken Sandwich",
                    description = "A boneless breast of chicken seasoned with a spicy blend of peppers, freshly breaded, pressure cooked in 100% refined peanut oil and served on a toasted, buttered bun with dill pickle chips.",
                    price = 5.09,
                    calories = 460,
                    fat = "21g",
                    carbs = "43g",
                    protein = "29g",
                    sodium = "1620mg",
                    ingredients = "Chicken Breast, Spicy Pepper Seasoning (red pepper, cayenne, garlic, salt, paprika), Bun, Dill Pickles.",
                    allergens = "Wheat, Soy, Peanut (oil)",
                    imageRes = "img_hero_banner",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                MenuItemEntity(
                    id = "item_grilled_sandwich",
                    category = "ENTREES",
                    name = "Grilled Chicken Sandwich",
                    description = "A lemon-herb marinated boneless breast of chicken, grilled for a tender, juicy, backyard-smoky taste, served on a toasted Multigrain Brioche Bun with Green Leaf lettuce and tomato.",
                    price = 5.95,
                    calories = 380,
                    fat = "12g",
                    carbs = "44g",
                    protein = "28g",
                    sodium = "980mg",
                    ingredients = "Grilled Chicken Breast, Multigrain Bun, Lettuce, Tomato, Honey Roasted BBQ Sauce.",
                    allergens = "Wheat, Soy",
                    imageRes = "img_hero_banner",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                // Nuggets
                MenuItemEntity(
                    id = "item_nuggets_8pc",
                    category = "NUGGETS",
                    name = "Chicken Nuggets - 8 Count",
                    description = "Bite-sized pieces of tender boneless breast of chicken, seasoned to perfection, freshly breaded and pressure cooked in 100% refined peanut oil. Available with your choice of dipping sauce.",
                    price = 4.95,
                    calories = 250,
                    fat = "11g",
                    carbs = "10g",
                    protein = "27g",
                    sodium = "1010mg",
                    ingredients = "Chicken Breast Pieces, Seasoned Breading, Peanut Oil.",
                    allergens = "Wheat, Egg, Soy, Peanut (oil)",
                    imageRes = "img_nuggets",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                MenuItemEntity(
                    id = "item_nuggets_12pc",
                    category = "NUGGETS",
                    name = "Chicken Nuggets - 12 Count",
                    description = "Twelve bite-sized pieces of tender boneless chicken breast, seasoned to perfection, freshly breaded and pressure cooked. Choice of dipping sauces.",
                    price = 6.85,
                    calories = 380,
                    fat = "16g",
                    carbs = "15g",
                    protein = "40g",
                    sodium = "1520mg",
                    ingredients = "Chicken Breast Pieces, Seasoned Breading, Peanut Oil.",
                    allergens = "Wheat, Egg, Soy, Peanut (oil)",
                    imageRes = "img_nuggets",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                // Salads
                MenuItemEntity(
                    id = "item_salad_cobb",
                    category = "SALADS",
                    name = "Cobb Salad with Grilled Chicken",
                    description = "Slices of grilled chicken breast served on a fresh bed of mixed greens, topped with shredded red cabbage and carrots, crumbled bacon, roasted corn kernels, diced eggs, and tomatoes.",
                    price = 8.99,
                    calories = 540,
                    fat = "29g",
                    carbs = "22g",
                    protein = "42g",
                    sodium = "1180mg",
                    ingredients = "Fresh Romaine and Salad Mix, Grilled Chicken, Crumbled Bacon, Corn, Egg, Tomatoes, Cheese.",
                    allergens = "Milk, Egg, Soy",
                    imageRes = "img_salad",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                // Sides
                MenuItemEntity(
                    id = "item_side_waffle_fries",
                    category = "SIDES",
                    name = "Waffle Potato Fries",
                    description = "Waffle-cut potatoes cooked in canola oil until crispy outside and tender inside. Sprinkled with sea salt.",
                    price = 2.45,
                    calories = 420,
                    fat = "24g",
                    carbs = "45g",
                    protein = "5g",
                    sodium = "280mg",
                    ingredients = "Potatoes, Canola Oil, Sea Salt.",
                    allergens = "None",
                    imageRes = "img_hero_banner", // part of banner image
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                MenuItemEntity(
                    id = "item_side_mac_cheese",
                    category = "SIDES",
                    name = "Mac & Cheese",
                    description = "A classic macaroni and cheese recipe featuring a special blend of cheeses including cheddar, parmesan, and Romano, baked fresh in restaurant daily.",
                    price = 3.55,
                    calories = 450,
                    fat = "27g",
                    carbs = "29g",
                    protein = "20g",
                    sodium = "1190mg",
                    ingredients = "Macaroni Pasta, Water, Cheese Blend (cheddar, parmesan, romano, blue cheese), Butter, Cream.",
                    allergens = "Milk, Wheat, Egg, Soy",
                    imageRes = "",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                MenuItemEntity(
                    id = "item_side_fruit",
                    category = "SIDES",
                    name = "Fruit Cup",
                    description = "A chilled, nutritious fruit mix made of mandarin orange segments, fresh strawberry slices, red and green apple pieces, and blueberries.",
                    price = 3.45,
                    calories = 60,
                    fat = "0g",
                    carbs = "15g",
                    protein = "1g",
                    sodium = "0mg",
                    ingredients = "Apples, Mandarin Oranges, Strawberries, Blueberries.",
                    allergens = "None",
                    imageRes = "",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                // Beverages
                MenuItemEntity(
                    id = "item_drink_lemonade",
                    category = "BEVERAGES",
                    name = "Fresh Lemonade",
                    description = "Classic, refreshing lemonade squeezed fresh in-house daily. Made with just three ingredients: real lemon juice, pure cane sugar, and water.",
                    price = 2.65,
                    calories = 220,
                    fat = "0g",
                    carbs = "54g",
                    protein = "0g",
                    sodium = "10mg",
                    ingredients = "Lemon Juice, Cane Sugar, Water, Ice.",
                    allergens = "None",
                    imageRes = "",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                MenuItemEntity(
                    id = "item_drink_iced_tea",
                    category = "BEVERAGES",
                    name = "Freshly Brewed Iced Tea",
                    description = "Brewed fresh from custom blends of orange pekoe and pekoe cut black tea leaves. Served sweet or unsweet over crushed ice.",
                    price = 2.15,
                    calories = 120,
                    fat = "0g",
                    carbs = "31g",
                    protein = "0g",
                    sodium = "10mg",
                    ingredients = "Black Tea, Sugar (in sweet version), Water, Ice.",
                    allergens = "None",
                    imageRes = "",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                MenuItemEntity(
                    id = "item_drink_milkshake_strawberry",
                    category = "DESSERTS",
                    name = "Strawberry Milkshake",
                    description = "Hand-spun the old-fashioned way, featuring delicious strawberry syrup, thick creamy vanilla Icedream, topped with whipped cream and a cherry.",
                    price = 4.35,
                    calories = 590,
                    fat = "15g",
                    carbs = "105g",
                    protein = "12g",
                    sodium = "380mg",
                    ingredients = "Vanilla Icedream (milk, sugar, cream), Strawberry Syrup, Whipped Cream, Maraschino Cherry.",
                    allergens = "Milk, Soy",
                    imageRes = "img_milkshake",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                // Dipping Sauces
                MenuItemEntity(
                    id = "item_sauce_cfa",
                    category = "DIPPING SAUCES",
                    name = "QuickBite Sauce",
                    description = "Our classic signature dipping sauce. A perfect blend of honey mustard, barbecue, and ranch for a smoky, sweet flavor.",
                    price = 0.00,
                    calories = 140,
                    fat = "13g",
                    carbs = "6g",
                    protein = "0g",
                    sodium = "210mg",
                    ingredients = "Soybean Oil, Sugar, Water, Distilled Vinegar, Egg Yolk, Honey, Mustard, Tomato Paste, Garlic, Spices.",
                    allergens = "Egg",
                    imageRes = "",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                MenuItemEntity(
                    id = "item_sauce_polynesian",
                    category = "DIPPING SAUCES",
                    name = "Polynesian Sauce",
                    description = "A delicious sweet and sour dipping sauce with a robust, tangy flavor profile.",
                    price = 0.00,
                    calories = 110,
                    fat = "9g",
                    carbs = "7g",
                    protein = "0g",
                    sodium = "220mg",
                    ingredients = "Sugar, Soybean Oil, Vinegar, Water, Tomato Paste, Salt, Onion, Garlic, Spices.",
                    allergens = "None",
                    imageRes = "",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                MenuItemEntity(
                    id = "item_sauce_honey_mustard",
                    category = "DIPPING SAUCES",
                    name = "Honey Mustard Sauce",
                    description = "A savory sauce with a perfect blend of sweet honey and tangy mustard.",
                    price = 0.00,
                    calories = 90,
                    fat = "8g",
                    carbs = "5g",
                    protein = "0g",
                    sodium = "180mg",
                    ingredients = "Mustard, Honey, Vinegar, Spices, Oil.",
                    allergens = "None",
                    imageRes = "",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = false
                ),
                // Breakfast
                MenuItemEntity(
                    id = "item_breakfast_biscuit",
                    category = "BREAKFAST",
                    name = "Chicken Biscuit",
                    description = "A breakfast portion of our famous boneless chicken breast, seasoned, breaded, and pressure-cooked, served on a fresh, buttermilk biscuit baked fresh daily.",
                    price = 3.89,
                    calories = 460,
                    fat = "20g",
                    carbs = "50g",
                    protein = "19g",
                    sodium = "1250mg",
                    ingredients = "Chicken Breast Piece, Fresh Buttermilk Biscuit (flour, butter, milk), Peanut Oil.",
                    allergens = "Wheat, Milk, Soy, Egg",
                    imageRes = "img_hero_banner",
                    availability = "BREAKFAST_ONLY",
                    isLimitedTime = false,
                    isMeal = false
                ),
                // Kids
                MenuItemEntity(
                    id = "item_kids_nuggets",
                    category = "KIDS",
                    name = "Kid's 5 Count Nuggets Meal",
                    description = "Five pieces of tender chicken nuggets, choice of small side (waffle fries, mac & cheese, fruit), juice/milk box, and a fun kid's surprise book.",
                    price = 5.75,
                    calories = 380,
                    fat = "14g",
                    carbs = "32g",
                    protein = "22g",
                    sodium = "750mg",
                    ingredients = "Kid-sized 5pc Chicken Nuggets, kid side, kid drink.",
                    allergens = "Wheat, Egg, Soy, Milk",
                    imageRes = "img_nuggets",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = true
                ),
                // Family Meals
                MenuItemEntity(
                    id = "item_family_meal_30pc",
                    category = "FAMILY MEALS",
                    name = "Family Nugget Feast (30 Count)",
                    description = "A giant 30-count platter of crispy chicken nuggets, accompanied by 2 large waffle potato fries, 4 dipping sauces, and a gallon of Fresh Sweet Iced Tea. Perfect for feeding 3-4 hungry people.",
                    price = 29.95,
                    calories = 2100,
                    fat = "98g",
                    carbs = "180g",
                    protein = "130g",
                    sodium = "4500mg",
                    ingredients = "30 Count Nuggets, 2 Large Fries, 1 Gallon Sweet Tea, 4 dipping sauces.",
                    allergens = "Wheat, Egg, Soy, Milk, Peanut (oil)",
                    imageRes = "img_nuggets",
                    availability = "AVAILABLE",
                    isLimitedTime = false,
                    isMeal = true
                ),
                // Seasonal
                MenuItemEntity(
                    id = "item_seasonal_peach_shake",
                    category = "SEASONAL",
                    name = "Seasonal Peach Milkshake",
                    description = "Limited Time Only! Hand-spun the old-fashioned way, featuring delicious peach purees, thick creamy Icedream, topped with whipped cream and a cherry.",
                    price = 4.75,
                    calories = 620,
                    fat = "16g",
                    carbs = "112g",
                    protein = "13g",
                    sodium = "400mg",
                    ingredients = "Vanilla Icedream, Real Peach Puree, Whipped Cream, Maraschino Cherry.",
                    allergens = "Milk, Soy",
                    imageRes = "img_milkshake",
                    availability = "SEASONAL",
                    isLimitedTime = true,
                    isMeal = false
                )
            )
            menuItemDao.insertMenuItems(dummyMenuItems)
        }

        // 3. Seed Default User Profile
        val currentProfile = userProfileDao.getUserProfile()
        if (currentProfile == null) {
            val defaultProfile = UserProfileEntity(
                id = "current_user",
                name = "Tom Harris",
                email = "tom@ahyx.org",
                phone = "(555) 123-4567",
                pointsBalance = 850,
                favoriteRestaurantId = "rest_downtown"
            )
            userProfileDao.insertUserProfile(defaultProfile)
        }
    }

    // Direct database actions
    suspend fun getRestaurantById(id: String): RestaurantEntity? = restaurantDao.getRestaurantById(id)
    suspend fun getMenuItemById(id: String): MenuItemEntity? = menuItemDao.getMenuItemById(id)
    suspend fun getMenuItemsByCategory(category: String): List<MenuItemEntity> = menuItemDao.getMenuItemsByCategory(category)

    // Cart actions
    suspend fun insertCartItem(item: CartItemEntity) = cartDao.insertCartItem(item)
    suspend fun updateCartItem(item: CartItemEntity) = cartDao.updateCartItem(item)
    suspend fun deleteCartItem(item: CartItemEntity) = cartDao.deleteCartItem(item)
    suspend fun deleteCartItemById(id: Int) = cartDao.deleteCartItemById(id)
    suspend fun clearCart() = cartDao.clearCart()

    // Order history actions
    suspend fun insertOrder(order: OrderHistoryEntity) = orderHistoryDao.insertOrder(order)
    suspend fun updateOrderStatus(orderId: String, status: String) = orderHistoryDao.updateOrderStatus(orderId, status)

    // Favorites actions
    suspend fun insertFavorite(favorite: FavoriteEntity) = favoriteDao.insertFavorite(favorite)
    suspend fun deleteFavoriteById(id: Int) = favoriteDao.deleteFavoriteById(id)
    suspend fun deleteFavorite(type: String, targetId: String) = favoriteDao.deleteFavorite(type, targetId)

    // User profile actions
    suspend fun updateUserProfile(user: UserProfileEntity) = userProfileDao.insertUserProfile(user)
    suspend fun updatePoints(points: Int) = userProfileDao.updatePoints(points)
}
