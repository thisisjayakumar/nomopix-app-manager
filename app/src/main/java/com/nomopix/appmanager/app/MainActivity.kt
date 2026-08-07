package com.nomopix.appmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.nomopix.appmanager.feature.manager.AppManagerScreen
import com.nomopix.appmanager.feature.manager.AppManagerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: AppManagerViewModel = hiltViewModel()
            AppManagerScreen(viewModel = viewModel)
        }
    }
}
