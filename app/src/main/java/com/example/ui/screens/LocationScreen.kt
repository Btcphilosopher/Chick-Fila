package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.RestaurantEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(viewModel: AppViewModel) {
    val restaurants by viewModel.restaurants.collectAsState()
    val selectedRestaurant by viewModel.selectedRestaurant.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var filterFavoritesOnly by remember { mutableStateOf(false) }

    val filteredRestaurants = restaurants.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || 
                            it.address.contains(searchQuery, ignoreCase = true)
        val matchesFilter = !filterFavoritesOnly // simplified for demo
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find a Restaurant", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by zip code, city, or address") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = RedPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPrimary,
                    unfocusedBorderColor = GrayBorder,
                    focusedLabelColor = RedPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Use Current Location button
            Button(
                onClick = {
                    // Simulate selecting the closest one (Downtown)
                    val closest = restaurants.minByOrNull { it.distance }
                    if (closest != null) {
                        viewModel.selectRestaurant(closest)
                        viewModel.navigateTo(Screen.ORDER_TYPE)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PeachLight, contentColor = RedDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("use_my_location_button")
            ) {
                Icon(Icons.Filled.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("USE MY CURRENT LOCATION", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Nearby Restaurants",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalText)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredRestaurants.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No restaurants found matching \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MutedText)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredRestaurants) { rest ->
                        RestaurantRowCard(
                            restaurant = rest,
                            isSelected = selectedRestaurant?.id == rest.id,
                            onOrderHere = {
                                viewModel.selectRestaurant(rest)
                                viewModel.navigateTo(Screen.ORDER_TYPE)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantRowCard(
    restaurant: RestaurantEntity,
    isSelected: Boolean,
    onOrderHere: () -> Unit
) {
    val statusText = if (restaurant.isOpen) "OPEN" else "CLOSED"
    val statusColor = if (restaurant.isOpen) GreenStatus else RedPrimary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            width = if (isSelected) 2.dp else 1.dp,
            brush = if (isSelected) androidx.compose.ui.graphics.SolidColor(RedPrimary) else androidx.compose.ui.graphics.SolidColor(GrayBorder)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = CharcoalText
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = restaurant.address,
                        style = MaterialTheme.typography.bodySmall.copy(color = MutedText),
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${restaurant.distance} mi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = RedPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = GrayBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Pickup options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServiceChip(enabled = restaurant.pickupAvailable, label = "Pickup")
                ServiceChip(enabled = restaurant.driveThruAvailable, label = "Drive-Thru")
                ServiceChip(enabled = restaurant.curbsideAvailable, label = "Curbside")
                ServiceChip(enabled = restaurant.deliveryAvailable, label = "Delivery")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hours Today",
                        style = MaterialTheme.typography.labelMedium.copy(color = MutedText)
                    )
                    Text(
                        text = "${restaurant.opensAt} - ${restaurant.closesAt}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    )
                }

                Button(
                    onClick = { onOrderHere() },
                    colors = ButtonDefaults.buttonColors(containerColor = if (restaurant.isOpen) RedPrimary else MutedText),
                    shape = RoundedCornerShape(8.dp),
                    enabled = restaurant.isOpen,
                    modifier = Modifier.testTag("order_here_btn_${restaurant.id}")
                ) {
                    Text(
                        text = "ORDER HERE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceChip(enabled: Boolean, label: String) {
    val alpha = if (enabled) 1.0f else 0.4f
    val bg = if (enabled) CreamLight else Color.Transparent
    val border = if (enabled) Color.Transparent else GrayBorder

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(6.dp))
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (enabled) FontWeight.Bold else FontWeight.Normal,
            color = if (enabled) CharcoalText else MutedText,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
