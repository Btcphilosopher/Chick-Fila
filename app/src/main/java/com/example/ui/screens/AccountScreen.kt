package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(viewModel: AppViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val orderHistory by viewModel.orderHistory.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val name = userProfile?.name ?: "Guest User"
    val email = userProfile?.email ?: "guest@quickbite.com"
    val phone = userProfile?.phone ?: "(404) 555-0199"

    val sdf = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Account", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CharcoalText) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = CharcoalText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBg)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmBg)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // User Header Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(PeachLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalText)
                            )
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                            )
                            Text(
                                text = phone,
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                            )
                        }
                    }
                }
            }

            // Favorites quick overview
            item {
                Column {
                    Text(
                        text = "Your Favorites",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalText)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (favorites.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = WarmSurface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Text(
                                text = "Items you favorite will appear here.",
                                color = MutedText,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = WarmSurface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column {
                                favorites.forEachIndexed { index, fav ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.navigateTo(Screen.MENU) }
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Favorite, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(fav.name, fontWeight = FontWeight.SemiBold, color = CharcoalText, fontSize = 14.sp)
                                        }
                                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MutedText)
                                    }
                                    if (index < favorites.size - 1) {
                                        Divider(color = GrayBorder, modifier = Modifier.padding(horizontal = 14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Order History Header
            item {
                Text(
                    text = "Order History",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalText)
                )
            }

            if (orderHistory.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmSurface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Text(
                            text = "You haven't placed any orders yet.",
                            color = MutedText,
                            fontSize = 13.sp,
                            modifier = Modifier
                                        .padding(24.dp)
                                        .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(orderHistory) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("history_card_${order.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmSurface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sdf.format(Date(order.timestamp)),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MutedText
                                )
                                Box(
                                    modifier = Modifier
                                        .background(PeachLight, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = order.status,
                                        color = if (order.status == "COMPLETED") GreenStatus else RedPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = order.restaurantName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = CharcoalText
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = order.itemsJson,
                                fontSize = 12.sp,
                                color = MutedText
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total: $${String.format("%.2f", order.total)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = CharcoalText
                                )

                                Button(
                                    onClick = {
                                        viewModel.reorder(order)
                                        viewModel.navigateTo(Screen.CART)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("reorder_history_btn_${order.id}")
                                ) {
                                    Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("REORDER", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
