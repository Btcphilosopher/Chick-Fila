package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

data class FulfillmentOption(
    val type: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTypeScreen(viewModel: AppViewModel) {
    val selectedRestaurant by viewModel.selectedRestaurant.collectAsState()
    val activeOrderType by viewModel.selectedOrderType.collectAsState()

    val options = listOf(
        FulfillmentOption(
            type = "PICKUP",
            title = "Counter Pickup",
            description = "Walk inside and pick up your bag at the counter.",
            icon = Icons.Filled.Store,
            tag = "fulfillment_pickup"
        ),
        FulfillmentOption(
            type = "DRIVE_THRU",
            title = "Drive-Thru",
            description = "Drive up, tell us your name, and get food from your car.",
            icon = Icons.Filled.DirectionsCar,
            tag = "fulfillment_drivethru"
        ),
        FulfillmentOption(
            type = "CURBSIDE",
            title = "Curbside",
            description = "Park in a designated spot, we bring it straight to you.",
            icon = Icons.Filled.LocalParking,
            tag = "fulfillment_curbside"
        ),
        FulfillmentOption(
            type = "DINE_IN",
            title = "Dine-In",
            description = "Order to your table and enjoy the full restaurant experience.",
            icon = Icons.Filled.Chair,
            tag = "fulfillment_dinein"
        ),
        FulfillmentOption(
            type = "DELIVERY",
            title = "Delivery",
            description = "Our friendly courier delivers fresh and warm to your door.",
            icon = Icons.Filled.DeliveryDining,
            tag = "fulfillment_delivery"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How would you like your order?", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = CharcoalText) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = CharcoalText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmBg)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            selectedRestaurant?.let { rest ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = RedPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Preparing from:",
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = rest.name,
                                style = MaterialTheme.typography.bodyLarge.copy(color = CharcoalText, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = rest.address,
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Select Fulfillment Option",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalText)
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(options) { option ->
                    val isSelected = option.type == activeOrderType
                    val borderCol = if (isSelected) RedPrimary else GrayBorder
                    val bg = if (isSelected) PeachLight else WarmSurface

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectOrderType(option.type) }
                            .testTag(option.tag),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = bg),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderCol))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = null,
                                tint = if (isSelected) RedPrimary else MutedText,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) RedDark else CharcoalText
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = option.description,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 12.sp)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = RedPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.navigateTo(Screen.MENU) },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("continue_to_menu_button")
            ) {
                Text(
                    text = "CONTINUE TO MENU",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
