package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(viewModel: AppViewModel) {
    val activeOrder by viewModel.activeOrder.collectAsState()

    val currentStatus = activeOrder?.status ?: "RECEIVED"

    val steps = listOf("RECEIVED", "PREPARING", "READY", "COMPLETED")
    val currentIndex = steps.indexOf(currentStatus).coerceAtLeast(0)

    // Pulse animation for preparing state
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ORDER TRACKER", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 2.sp, color = RedPrimary) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateAndClear(Screen.HOME) }) {
                        Icon(Icons.Filled.Home, contentDescription = "Home", tint = CharcoalText)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = WarmBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmBg)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (activeOrder == null) {
                // Empty fallback state
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.FactCheck, contentDescription = null, tint = MutedText, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No active orders", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Order some delicious food to track it here!", color = MutedText, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.navigateAndClear(Screen.HOME) },
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("GO TO HOME", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                val order = activeOrder!!

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Order ${order.id}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = CharcoalText
                        )
                        Text(
                            text = order.restaurantName,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MutedText)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Progress Tracker Line
                        steps.forEachIndexed { index, step ->
                            val isDone = index < currentIndex
                            val isActive = index == currentIndex
                            val color = when {
                                isDone -> GreenStatus
                                isActive -> RedPrimary
                                else -> GrayBorder
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            color = if (isActive) color.copy(alpha = pulseAlpha) else color,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    } else {
                                        Text(
                                            text = (index + 1).toString(),
                                            color = if (isActive) Color.White else MutedText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = when (step) {
                                            "RECEIVED" -> "Order Received"
                                            "PREPARING" -> "Preparing Deliciousness"
                                            "READY" -> "Ready for Pickup"
                                            "COMPLETED" -> "Order Completed"
                                            else -> step
                                        },
                                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = if (isActive) CharcoalText else MutedText,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = when (step) {
                                            "RECEIVED" -> "Our team has accepted your order."
                                            "PREPARING" -> "Your food is being cooked fresh in refined peanut oil."
                                            "READY" -> "Head over to our counter or drive-thru spot!"
                                            "COMPLETED" -> "Enjoy your meal! Thank you for ordering."
                                            else -> ""
                                        },
                                        fontSize = 11.sp,
                                        color = MutedText
                                    )
                                }
                            }

                            if (index < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 13.dp)
                                        .width(2.dp)
                                        .height(24.dp)
                                        .background(if (index < currentIndex) GreenStatus else GrayBorder)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Map simulation Canvas block
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Custom Canvas map drawing
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw a beautiful winding road
                            val pathColor = Color(0xFFCBD5E1)
                            drawLine(
                                color = pathColor,
                                start = Offset(w * 0.15f, h * 0.8f),
                                end = Offset(w * 0.45f, h * 0.5f),
                                strokeWidth = 12f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = pathColor,
                                start = Offset(w * 0.45f, h * 0.5f),
                                end = Offset(w * 0.85f, h * 0.2f),
                                strokeWidth = 12f,
                                cap = StrokeCap.Round
                            )

                            // Draw target destination
                            drawCircle(
                                color = RedPrimary,
                                radius = 24f,
                                center = Offset(w * 0.85f, h * 0.2f)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 8f,
                                center = Offset(w * 0.85f, h * 0.2f)
                            )

                            // Draw current location pin
                            drawCircle(
                                color = GreenStatus,
                                radius = 16f,
                                center = Offset(w * 0.35f, h * 0.6f)
                            )
                        }

                        // Text overlays
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text("Tracking Courier Live", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CharcoalText)
                            Text("Arriving in ~8 mins", fontSize = 10.sp, color = MutedText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Items summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Items in this order", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(order.itemsJson, color = MutedText, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Order Type", fontSize = 12.sp, color = CharcoalText)
                            Text(order.orderType, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RedPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { 
                        viewModel.completeActiveOrder()
                        viewModel.navigateAndClear(Screen.HOME) 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CharcoalText),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("complete_order_tracking_btn")
                ) {
                    Text("OKAY, GO HOME", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
