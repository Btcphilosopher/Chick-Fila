package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.RewardOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(viewModel: AppViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val rewardOptions = viewModel.rewardOptions
    val points = userProfile?.pointsBalance ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rewards & Loyalty", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CharcoalText) },
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
            // Points Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WarmSurface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(PeachLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CardGiftcard,
                            contentDescription = null,
                            tint = RedPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "YOUR BALANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MutedText,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "$points points",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = RedPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { (points % 600) / 600f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = RedPrimary,
                        trackColor = CreamLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val nextSandwichPts = 600 - (points % 600)
                    Text(
                        text = "Only $nextSandwichPts points until your next Free Sandwich!",
                        style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontWeight = FontWeight.Medium),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Redeem Food & Treats",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalText)
            )
            Text(
                text = "Points will be deducted when you add these to your order.",
                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(rewardOptions) { option ->
                    val isEligible = points >= option.pointsCost
                    RewardCard(
                        option = option,
                        isEligible = isEligible,
                        onRedeem = { 
                            viewModel.redeemReward(option)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RewardCard(
    option: RewardOption,
    isEligible: Boolean,
    onRedeem: () -> Unit
) {
    val imageId = when (option.id) {
        "rew_fries" -> R.drawable.img_hero_banner
        "rew_nuggets" -> R.drawable.img_nuggets
        "rew_shake" -> R.drawable.img_milkshake
        else -> R.drawable.img_hero_banner
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reward_card_${option.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = option.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.15f))
                )
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            color = if (isEligible) RedPrimary else Color.DarkGray,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "${option.pointsCost} pts",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalText),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onRedeem() },
                    enabled = isEligible,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPrimary,
                        disabledContainerColor = CreamLight
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("redeem_btn_${option.id}")
                ) {
                    Text(
                        text = if (isEligible) "REDEEM" else "LOCKED",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isEligible) Color.White else MutedText
                    )
                }
            }
        }
    }
}
