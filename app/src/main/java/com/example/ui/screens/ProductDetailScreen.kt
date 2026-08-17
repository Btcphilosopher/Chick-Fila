package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.MenuItemEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(viewModel: AppViewModel) {
    val item by viewModel.selectedMenuItem.collectAsState()

    // Observables from VM
    val bun by viewModel.customizationBun.collectAsState()
    val pickles by viewModel.customizationPickles.collectAsState()
    val spicy by viewModel.customizationSpicyExtra.collectAsState()
    val sauces by viewModel.customizationSauces.collectAsState()

    val mealSide by viewModel.mealSelectedSide.collectAsState()
    val mealSideSize by viewModel.mealSelectedSideSize.collectAsState()
    val mealDrink by viewModel.mealSelectedDrink.collectAsState()
    val mealDrinkSize by viewModel.mealSelectedDrinkSize.collectAsState()
    val mealDrinkIce by viewModel.mealSelectedDrinkIce.collectAsState()

    // Local states
    var isMealBuildSelected by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }

    val activeItem = item ?: return

    val imageId = when (activeItem.imageRes) {
        "img_hero_banner" -> R.drawable.img_hero_banner
        "img_nuggets" -> R.drawable.img_nuggets
        "img_milkshake" -> R.drawable.img_milkshake
        "img_salad" -> R.drawable.img_salad
        else -> R.drawable.img_hero_banner
    }

    // Dynamic price logic
    var displayPrice = activeItem.price
    var displayCalories = activeItem.calories

    if (isMealBuildSelected) {
        displayPrice += 3.20
        displayCalories += 420 + 220 // estimate fries and drink
        if (mealSideSize == "Large") displayPrice += 0.50
        if (mealDrinkSize == "Large") displayPrice += 0.40
    }

    // Customization flags
    val canCustomize = !activeItem.isMeal && activeItem.category in listOf("ENTREES", "BREAKFAST")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(activeItem.name, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { 
                        viewModel.clearCustomizations()
                        viewModel.clearMealSelections()
                        viewModel.navigateBack() 
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBg)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                color = WarmSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Qty Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(CreamLight, RoundedCornerShape(12.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Icon(Icons.Filled.Remove, contentDescription = "Decrease qty", tint = CharcoalText)
                        }
                        Text(
                            text = quantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CharcoalText,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(onClick = { quantity++ }) {
                            Icon(Icons.Filled.Add, contentDescription = "Increase qty", tint = CharcoalText)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Add to Order Button
                    Button(
                        onClick = {
                            viewModel.addProductToCart(activeItem, quantity, isMealBuildSelected)
                            viewModel.navigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("add_to_order_button")
                    ) {
                        Text(
                            text = "ADD TO ORDER • $${String.format("%.2f", displayPrice * quantity)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            // Food Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = activeItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Product Details Block
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = activeItem.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = CharcoalText)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "$${String.format("%.2f", activeItem.price)}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = RedPrimary)
                    )
                    Text(
                        text = "$displayCalories Cal",
                        style = MaterialTheme.typography.titleMedium.copy(color = MutedText)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = activeItem.description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalText, lineHeight = 20.sp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Meal builder toggle option if applicable
                if (activeItem.category == "ENTREES" && !activeItem.isMeal) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PeachLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Make it a Meal?",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = RedDark
                                )
                                Text(
                                    text = "Add Waffle Fries + Fresh Beverage for only +$3.20",
                                    fontSize = 12.sp,
                                    color = CharcoalText
                                )
                            }
                            Switch(
                                checked = isMealBuildSelected,
                                onCheckedChange = { isMealBuildSelected = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = RedPrimary,
                                    checkedTrackColor = PeachLight,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.LightGray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Expandable meal selections
                AnimatedVisibility(visible = isMealBuildSelected) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "MEAL OPTIONS",
                            fontWeight = FontWeight.ExtraBold,
                            color = RedPrimary,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Side selections
                        Text(text = "Choose your Side", fontWeight = FontWeight.Bold, color = CharcoalText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Waffle Fries", "Mac & Cheese", "Fruit Cup").forEach { sideOption ->
                                val selected = mealSide?.name?.contains(sideOption) ?: (sideOption == "Waffle Fries" && mealSide == null)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) RedPrimary else CreamLight)
                                        .clickable { 
                                            // Mock item
                                            viewModel.setMealSide(MenuItemEntity("", "SIDES", sideOption, "", 0.0, 0, "", "", "", "", "", "", "", "", false, false))
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(sideOption, color = if (selected) Color.White else CharcoalText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Side size
                        Text(text = "Side Size", fontWeight = FontWeight.Bold, color = CharcoalText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Medium", "Large (+$0.50)").forEach { sizeOption ->
                                val sizeName = sizeOption.split(" ").first()
                                val selected = mealSideSize == sizeName
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) RedPrimary else CreamLight)
                                        .clickable { viewModel.setMealSideSize(sizeName) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(sizeOption, color = if (selected) Color.White else CharcoalText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Drink selections
                        Text(text = "Choose your Beverage", fontWeight = FontWeight.Bold, color = CharcoalText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Lemonade", "Iced Tea", "Soft Drink").forEach { drinkOption ->
                                val selected = mealDrink?.name?.contains(drinkOption) ?: (drinkOption == "Lemonade" && mealDrink == null)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) RedPrimary else CreamLight)
                                        .clickable {
                                            viewModel.setMealDrink(MenuItemEntity("", "BEVERAGES", drinkOption, "", 0.0, 0, "", "", "", "", "", "", "", "", false, false))
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(drinkOption, color = if (selected) Color.White else CharcoalText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Drink size
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Medium", "Large (+$0.40)").forEach { sizeOption ->
                                val sizeName = sizeOption.split(" ").first()
                                val selected = mealDrinkSize == sizeName
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) RedPrimary else CreamLight)
                                        .clickable { viewModel.setMealDrinkSize(sizeName) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(sizeOption, color = if (selected) Color.White else CharcoalText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(color = GrayBorder)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                // Customizations (Buns, Pickles, Sauces, Toppings)
                if (canCustomize && !isMealBuildSelected) {
                    Text(
                        text = "CUSTOMIZATION",
                        fontWeight = FontWeight.ExtraBold,
                        color = RedPrimary,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Bun Selector
                    Text(text = "Bun Option", fontWeight = FontWeight.Bold, color = CharcoalText)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("Standard Brioche Bun", "Multigrain Brioche Bun", "Gluten Free Bun", "No Bun").forEach { bunOption ->
                        val selected = bun == bunOption
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setCustomizationBun(bunOption) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = { viewModel.setCustomizationBun(bunOption) },
                                colors = RadioButtonDefaults.colors(selectedColor = RedPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(bunOption, color = CharcoalText)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pickles toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setCustomizationPickles(!pickles) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Dill Pickle Chips", fontWeight = FontWeight.SemiBold, color = CharcoalText)
                        Checkbox(
                            checked = pickles,
                            onCheckedChange = { viewModel.setCustomizationPickles(it) },
                            colors = CheckboxDefaults.colors(checkedColor = RedPrimary)
                        )
                    }

                    // Extra spicy toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setCustomizationSpicyExtra(!spicy) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Extra Spicy Rub", fontWeight = FontWeight.SemiBold, color = CharcoalText)
                        Checkbox(
                            checked = spicy,
                            onCheckedChange = { viewModel.setCustomizationSpicyExtra(it) },
                            colors = CheckboxDefaults.colors(checkedColor = RedPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add dipping sauces
                    Text(text = "Add Dipping Sauces", fontWeight = FontWeight.Bold, color = CharcoalText)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("QuickBite Sauce", "Polynesian Sauce", "Honey Mustard Sauce").forEach { sauceName ->
                        val sauceId = "item_sauce_" + sauceName.replace(" ", "_").lowercase()
                        val qty = sauces[sauceId] ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(sauceName, color = CharcoalText)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(CreamLight, CircleShape)
                                        .clickable { if (qty > 0) viewModel.adjustSauceQty(sauceId, -1) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    qty.toString(),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(PeachLight, CircleShape)
                                        .clickable { viewModel.adjustSauceQty(sauceId, 1) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(color = GrayBorder)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Nutrition and Allergen details
                Text(
                    text = "NUTRITION & INGREDIENTS",
                    fontWeight = FontWeight.ExtraBold,
                    color = RedPrimary,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                NutritionMetricRow("Total Fat", activeItem.fat)
                NutritionMetricRow("Total Carbs", activeItem.carbs)
                NutritionMetricRow("Total Protein", activeItem.protein)
                NutritionMetricRow("Sodium", activeItem.sodium)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ingredients",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = CharcoalText
                )
                Text(
                    text = activeItem.ingredients,
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Allergens",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = RedPrimary
                )
                Text(
                    text = activeItem.allergens,
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun NutritionMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = CharcoalText, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 13.sp)
    }
}
