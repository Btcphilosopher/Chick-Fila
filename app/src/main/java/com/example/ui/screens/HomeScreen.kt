package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.MenuItemEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun HomeScreen(viewModel: AppViewModel) {
    val user by viewModel.userProfile.collectAsState()
    val selectedRestaurant by viewModel.selectedRestaurant.collectAsState()
    val orderHistory by viewModel.orderHistory.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val menuItems by viewModel.menuItems.collectAsState()
    val cart by viewModel.cartItems.collectAsState()

    val userName = user?.name?.split(" ")?.firstOrNull() ?: "Guest"
    val pointsBalance = user?.pointsBalance ?: 0

    // Get popular items
    val popularItems = menuItems.filter { 
        it.id in listOf("item_chicken_sandwich", "item_nuggets_8pc", "item_drink_milkshake_strawberry", "item_salad_cobb")
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                viewModel = viewModel,
                selectedRestaurantName = selectedRestaurant?.name ?: "Select a Restaurant",
                cartCount = cart.sumOf { it.quantity }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Greeting Banner
            item {
                Column {
                    Text(
                        text = "Good afternoon, $userName!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText,
                            fontSize = 24.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "We are ready to serve you fresh, delicious meals.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MutedText)
                    )
                }
            }

            // Hero Visual Box
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner),
                            contentDescription = "QuickBite Chicken Sandwich Hero",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "WHAT CAN WE GET FOR YOU?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pressure-cooked in 100% refined peanut oil",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                            )
                        }
                    }
                }
            }

            // Quick Actions Block
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { 
                            if (selectedRestaurant == null) {
                                viewModel.navigateTo(Screen.RESTAURANTS)
                            } else {
                                viewModel.navigateTo(Screen.MENU)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(56.dp)
                            .testTag("start_order_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.RestaurantMenu, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "START ORDER",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    if (orderHistory.isNotEmpty()) {
                        FilledTonalButton(
                            onClick = {
                                viewModel.reorder(orderHistory.first())
                                viewModel.navigateTo(Screen.CART)
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = PeachLight,
                                contentColor = RedDark
                            ),
                            modifier = Modifier
                                .weight(0.9f)
                                .height(56.dp)
                                .testTag("reorder_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.History, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REORDER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.RESTAURANTS) },
                            modifier = Modifier
                                .weight(0.9f)
                                .height(56.dp)
                                .testTag("find_restaurant_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = RedPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FIND APPS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = RedPrimary
                            )
                        }
                    }
                }
            }

            // Loyalty Rewards Progress Summary
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.REWARDS) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Loyalty Points",
                                style = MaterialTheme.typography.labelMedium.copy(color = MutedText)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$pointsBalance points",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RedPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Simple Progress Bar
                            val progress = (pointsBalance % 600) / 600f
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = RedPrimary,
                                trackColor = PeachLight,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${600 - (pointsBalance % 600)} pts to next free sandwich",
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 11.sp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(PeachLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.CardGiftcard,
                                contentDescription = null,
                                tint = RedPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Popular Items Slider
            if (popularItems.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = "Popular Right Now",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(popularItems) { item ->
                                PopularItemCard(item = item, onClick = {
                                    viewModel.selectMenuItem(item)
                                    viewModel.navigateTo(Screen.PRODUCT_DETAIL)
                                })
                            }
                        }
                    }
                }
            }

            // Favorites quick block
            if (favorites.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = "Your Favorites",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            favorites.take(3).forEach { favorite ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.navigateTo(Screen.MENU)
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = CreamLight)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.Favorite,
                                            contentDescription = null,
                                            tint = RedPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = favorite.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = CharcoalText
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            Icons.Filled.ChevronRight,
                                            contentDescription = null,
                                            tint = MutedText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    viewModel: AppViewModel,
    selectedRestaurantName: String,
    cartCount: Int
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "QUICKBITE",
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = RedPrimary,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            Row(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CreamLight)
                    .clickable { viewModel.navigateTo(Screen.RESTAURANTS) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = RedPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedRestaurantName.take(15) + (if (selectedRestaurantName.length > 15) "..." else ""),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                )
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.ACCOUNT) },
                    modifier = Modifier.testTag("account_profile_button")
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Account Profile",
                        tint = CharcoalText,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("cart_bag_button")
                        .clickable { viewModel.navigateTo(Screen.CART) }
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.ShoppingBag,
                        contentDescription = "Cart Bag",
                        tint = CharcoalText,
                        modifier = Modifier.size(26.dp)
                    )
                    if (cartCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(18.dp)
                                .background(RedPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cartCount.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = WarmBg
        )
    )
}

@Composable
fun PopularItemCard(
    item: MenuItemEntity,
    onClick: () -> Unit
) {
    val imageId = when (item.imageRes) {
        "img_hero_banner" -> R.drawable.img_hero_banner
        "img_nuggets" -> R.drawable.img_nuggets
        "img_milkshake" -> R.drawable.img_milkshake
        "img_salad" -> R.drawable.img_salad
        else -> R.drawable.img_hero_banner // Fallback
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.calories} Cal",
                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", item.price)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = RedPrimary
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(RedPrimary, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Quick Add",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
