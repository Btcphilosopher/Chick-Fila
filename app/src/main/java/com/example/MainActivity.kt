package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: AppViewModel = viewModel()
        val navigationStack by viewModel.navigationStack.collectAsState()
        val activeScreen = navigationStack.lastOrNull() ?: Screen.HOME

        when (activeScreen) {
          Screen.HOME -> HomeScreen(viewModel)
          Screen.RESTAURANTS -> LocationScreen(viewModel)
          Screen.ORDER_TYPE -> OrderTypeScreen(viewModel)
          Screen.MENU -> MenuScreen(viewModel)
          Screen.PRODUCT_DETAIL -> ProductDetailScreen(viewModel)
          Screen.CART -> CartAndCheckoutScreen(viewModel, isCheckoutMode = false)
          Screen.CHECKOUT -> CartAndCheckoutScreen(viewModel, isCheckoutMode = true)
          Screen.TRACKING -> TrackingScreen(viewModel)
          Screen.REWARDS -> RewardsScreen(viewModel)
          Screen.ACCOUNT -> AccountScreen(viewModel)
          else -> HomeScreen(viewModel)
        }
      }
    }
  }
}
