package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.models.CartItemEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.foundation.border

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartAndCheckoutScreen(viewModel: AppViewModel, isCheckoutMode: Boolean) {
    val cartItems by viewModel.cartItems.collectAsState()
    val appliedDiscount by viewModel.appliedDiscount.collectAsState()
    val appliedRewardItemName by viewModel.appliedRewardItemName.collectAsState()
    val selectedRestaurant by viewModel.selectedRestaurant.collectAsState()
    val orderType by viewModel.selectedOrderType.collectAsState()

    val subtotal = cartItems.sumOf { it.price * it.quantity } - appliedDiscount
    val tax = subtotal * 0.08
    val deliveryFee = if (orderType == "DELIVERY") 3.99 else 0.00
    val total = subtotal + tax + deliveryFee

    // Checkout specific input states
    var paymentMethod by remember { mutableStateOf("CREDIT_CARD") }
    var scheduledLater by remember { mutableStateOf(false) }
    var scheduledTime by remember { mutableStateOf("12:35 PM") }

    // Curbside inputs
    var curbsideSpot by remember { mutableStateOf("Spot #4") }
    var curbsideCarDesc by remember { mutableStateOf("Toyota RAV4") }
    var curbsideCarColor by remember { mutableStateOf("Silver") }
    var curbsideLicenseLast4 by remember { mutableStateOf("8421") }

    // Delivery inputs
    var deliveryAddress by remember { mutableStateOf("750 Ferst Dr, Atlanta, GA 30332") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isCheckoutMode) "Checkout" else "Your Order", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBg)
            )
        }
    ) { innerPadding ->
        if (cartItems.isEmpty() && !isCheckoutMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = MutedText, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Your bag is empty",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = CharcoalText)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Start adding delicious items from our menu!",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MutedText)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.MENU) },
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("VIEW MENU", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (!isCheckoutMode) {
            // CART VIEW MODE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Review Items",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalText)
                        )
                    }

                    items(cartItems) { item ->
                        CartItemRowCard(
                            item = item,
                            onIncrease = { viewModel.updateCartItemQty(item.id, item.quantity + 1) },
                            onDecrease = { viewModel.updateCartItemQty(item.id, item.quantity - 1) },
                            onRemove = { viewModel.removeCartItem(item.id) },
                            onSaveFav = { viewModel.toggleFavorite("ITEM", item.itemId, item.name) }
                        )
                    }

                    if (appliedDiscount > 0) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PeachLight),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = RedPrimary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Loyalty Reward Applied!", fontWeight = FontWeight.Bold, color = RedDark, fontSize = 13.sp)
                                        Text("Free $appliedRewardItemName added to bag.", color = CharcoalText, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Divider(color = GrayBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WarmSurface, RoundedCornerShape(12.dp))
                                .border(1.dp, GrayBorder, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Text("Summary", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            SummaryRow("Subtotal", "$${String.format("%.2f", subtotal)}")
                            if (appliedDiscount > 0) {
                                SummaryRow("Loyalty Reward Discount", "-$${String.format("%.2f", appliedDiscount)}", textColor = GreenStatus)
                            }
                            SummaryRow("Estimated Tax (8%)", "$${String.format("%.2f", tax)}")
                            if (orderType == "DELIVERY") {
                                SummaryRow("Delivery Fee", "$${String.format("%.2f", deliveryFee)}")
                            }

                            Divider(color = GrayBorder, modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total", fontWeight = FontWeight.ExtraBold, color = CharcoalText, fontSize = 18.sp)
                                Text("$${String.format("%.2f", total.coerceAtLeast(0.0))}", fontWeight = FontWeight.ExtraBold, color = RedPrimary, fontSize = 18.sp)
                            }
                        }
                    }
                }

                Surface(
                    tonalElevation = 8.dp,
                    color = WarmSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding()
                    ) {
                        Button(
                            onClick = { 
                                if (selectedRestaurant == null) {
                                    viewModel.navigateTo(Screen.RESTAURANTS)
                                } else {
                                    viewModel.navigateTo(Screen.CHECKOUT)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("checkout_button")
                        ) {
                            Text("CHECKOUT • $${String.format("%.2f", total.coerceAtLeast(0.0))}", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        } else {
            // CHECKOUT PROCESS MODE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Step header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).height(4.dp).background(RedPrimary, CircleShape))
                    Box(modifier = Modifier.weight(1f).height(4.dp).background(RedPrimary, CircleShape))
                    Box(modifier = Modifier.weight(1f).height(4.dp).background(RedPrimary, CircleShape))
                }

                Text(
                    text = "Review Order & Pickup",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalText)
                )

                // Selected Restaurant Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = RedPrimary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(selectedRestaurant?.name ?: "No restaurant selected", fontWeight = FontWeight.Bold, color = CharcoalText)
                            Text(selectedRestaurant?.address ?: "", fontSize = 12.sp, color = MutedText)
                        }
                    }
                }

                // Order Type Selection (Drive-thru, curbside, pickup, delivery, dine-in)
                Text(text = "Choose Hand-off Method", fontWeight = FontWeight.Bold, color = CharcoalText)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("PICKUP", "DRIVE_THRU", "CURBSIDE", "DELIVERY").forEach { method ->
                        val selected = orderType == method
                        val isSupported = when (method) {
                            "DRIVE_THRU" -> selectedRestaurant?.driveThruAvailable ?: true
                            "CURBSIDE" -> selectedRestaurant?.curbsideAvailable ?: true
                            "DELIVERY" -> selectedRestaurant?.deliveryAvailable ?: true
                            else -> true
                        }

                        if (isSupported) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) RedPrimary else CreamLight)
                                    .clickable { viewModel.selectOrderType(method) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = method.replace("_", " "),
                                    color = if (selected) Color.White else CharcoalText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Conditionally display dynamic details for curbside or delivery
                AnimatedVisibility(visible = orderType == "CURBSIDE") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Curbside Spot Information", fontWeight = FontWeight.Bold, color = CharcoalText)
                        OutlinedTextField(
                            value = curbsideSpot,
                            onValueChange = { curbsideSpot = it },
                            label = { Text("Parking Spot Number") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = curbsideCarDesc,
                                onValueChange = { curbsideCarDesc = it },
                                label = { Text("Car Make/Model") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.2f)
                            )
                            OutlinedTextField(
                                value = curbsideCarColor,
                                onValueChange = { curbsideCarColor = it },
                                label = { Text("Color") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(0.8f)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = orderType == "DELIVERY") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Delivery Destination Address", fontWeight = FontWeight.Bold, color = CharcoalText)
                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = { deliveryAddress = it },
                            label = { Text("Address") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Scheduling block
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CreamLight)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Schedule for Later?", fontWeight = FontWeight.Bold, color = CharcoalText)
                            Text(
                                text = if (scheduledLater) "Ready at $scheduledTime" else "Prepare as soon as possible",
                                fontSize = 12.sp,
                                color = MutedText
                            )
                        }
                        Switch(
                            checked = scheduledLater,
                            onCheckedChange = { scheduledLater = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = RedPrimary)
                        )
                    }
                }

                // Payment selection
                Text(text = "Select Tokenized Payment Method", fontWeight = FontWeight.Bold, color = CharcoalText)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column {
                        listOf(
                            "CREDIT_CARD" to "Credit / Debit Card (Visa •••• 4812)",
                            "APPLE_PAY" to "Apple Pay",
                            "GOOGLE_PAY" to "Google Pay",
                            "DIGITAL_WALLET" to "QuickBite Wallet"
                        ).forEach { (method, label) ->
                            val selected = paymentMethod == method
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { paymentMethod = method }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = { paymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = RedPrimary)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(label, color = CharcoalText)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price Summary Review
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SummaryRow("Subtotal", "$${String.format("%.2f", subtotal)}")
                        if (appliedDiscount > 0) {
                            SummaryRow("Rewards Coupon Applied", "-$${String.format("%.2f", appliedDiscount)}", textColor = GreenStatus)
                        }
                        SummaryRow("Tax", "$${String.format("%.2f", tax)}")
                        if (orderType == "DELIVERY") {
                            SummaryRow("Delivery Fee", "$${String.format("%.2f", deliveryFee)}")
                        }
                        Divider(color = GrayBorder, modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Charge", fontWeight = FontWeight.ExtraBold, color = CharcoalText)
                            Text("$${String.format("%.2f", total.coerceAtLeast(0.0))}", fontWeight = FontWeight.ExtraBold, color = RedPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Place Order Button
                Button(
                    onClick = {
                        val pickupDetails = if (orderType == "CURBSIDE") {
                            "Curbside: $curbsideSpot, Car: $curbsideCarColor $curbsideCarDesc, Lic: $curbsideLicenseLast4"
                        } else if (scheduledLater) {
                            "Scheduled: $scheduledTime"
                        } else null

                        viewModel.submitOrder(
                            paymentMethod = paymentMethod,
                            scheduledTime = if (scheduledLater) scheduledTime else null,
                            curbsideInfo = pickupDetails,
                            deliveryAddress = if (orderType == "DELIVERY") deliveryAddress else null
                        )
                        viewModel.navigateAndClear(Screen.TRACKING)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("place_order_button")
                ) {
                    Text(
                        text = "PLACE ORDER • $${String.format("%.2f", total.coerceAtLeast(0.0))}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun CartItemRowCard(
    item: CartItemEntity,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    onSaveFav: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.customizationsJson,
                        style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 11.sp)
                    )
                }

                Text(
                    text = "$${String.format("%.2f", item.price * item.quantity)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = RedPrimary
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onSaveFav() }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Filled.FavoriteBorder, contentDescription = "Add to favorites", tint = RedPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = { onRemove() }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Remove", tint = MutedText, modifier = Modifier.size(20.dp))
                    }
                }

                // Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(CreamLight, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(onClick = { onDecrease() }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = item.quantity.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = { onIncrease() }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, textColor: Color = CharcoalText) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = CharcoalText, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Bold, color = textColor, fontSize = 13.sp)
    }
}
