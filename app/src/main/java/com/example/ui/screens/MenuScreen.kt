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
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.MenuItemEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(viewModel: AppViewModel) {
    val menuItems by viewModel.menuItems.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cart by viewModel.cartItems.collectAsState()
    val selectedRestaurant by viewModel.selectedRestaurant.collectAsState()

    val categories = listOf(
        "ENTREES", "BREAKFAST", "NUGGETS", "SALADS", "SIDES", "BEVERAGES", "DESSERTS", "DIPPING SAUCES", "KIDS", "FAMILY MEALS", "SEASONAL"
    )

    // Filter menu items by active category or active search query
    val displayedItems = if (searchQuery.isNotEmpty()) {
        menuItems.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    } else {
        menuItems.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(selectedRestaurant?.name?.uppercase() ?: "MENU", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = RedPrimary) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Quick cart summary
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("menu_cart_bag")
                            .clickable { viewModel.navigateTo(Screen.CART) }
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Outlined.ShoppingBag, contentDescription = "Cart", tint = CharcoalText)
                        val totalQty = cart.sumOf { it.quantity }
                        if (totalQty > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .size(16.dp)
                                    .background(RedPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(totalQty.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = WarmBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Search field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search chicken, fries, lemonade...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = RedPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = GrayBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Categories horizontal slider (only visible if search query is empty!)
            if (searchQuery.isEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        val bg = if (isSelected) RedPrimary else CreamLight
                        val tc = if (isSelected) Color.White else CharcoalText

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(bg)
                                .clickable { viewModel.selectCategory(category) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category.replace("_", " "),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = tc
                            )
                        }
                    }
                }
            } else {
                // If search is active, show descriptive text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search results for \"$searchQuery\"",
                        style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${displayedItems.size} items found",
                        style = MaterialTheme.typography.bodySmall.copy(color = RedPrimary, fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Menu List
            if (displayedItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.MenuBook, contentDescription = null, tint = MutedText, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No menu items available in this category.", style = MaterialTheme.typography.bodyMedium.copy(color = MutedText))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayedItems) { item ->
                        MenuItemCardRow(
                            item = item,
                            onClick = {
                                viewModel.selectMenuItem(item)
                                viewModel.navigateTo(Screen.PRODUCT_DETAIL)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemCardRow(
    item: MenuItemEntity,
    onClick: () -> Unit
) {
    val imageId = when (item.imageRes) {
        "img_hero_banner" -> R.drawable.img_hero_banner
        "img_nuggets" -> R.drawable.img_nuggets
        "img_milkshake" -> R.drawable.img_milkshake
        "img_salad" -> R.drawable.img_salad
        else -> R.drawable.img_hero_banner // fallback banner
    }

    val hasCustomization = !item.isMeal && item.category in listOf("ENTREES", "BREAKFAST", "KIDS", "FAMILY MEALS")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    )
                    if (item.isLimitedTime) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(PeachLight, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "LIMITED TIME",
                                color = RedPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 11.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$${String.format("%.2f", item.price)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = RedPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${item.calories} Cal",
                            style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PeachLight)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (hasCustomization) "CUSTOMIZE" else "ADD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RedDark
                            )
                        )
                    }
                }
            }
        }
    }
}
