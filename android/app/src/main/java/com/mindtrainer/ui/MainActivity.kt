package com.mindtrainer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindtrainer.di.hiltViewModel
import com.mindtrainer.ui.theme.MindTrainerTheme
import com.mindtrainer.ui.navigation.AppNavHost
import com.mindtrainer.ui.theme.AppCompositionLocals
import com.mindtrainer.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindTrainerTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        AppCompositionLocals.LocalViewModel provides mainViewModel
                    ) {
                        AppNavHost(mainViewModel)
                    }
                }
            }
        }
    }
}