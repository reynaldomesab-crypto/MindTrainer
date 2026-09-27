package com.mindtrainer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindtrainer.ui.viewmodel.MainViewModel
import com.mindtrainer.ui.screen.{HomeScreen, AuthScreen, OnboardingScreen, ProgressScreen, SettingsScreen, ExerciseScreen}
import com.mindtrainer.ui.screen.exercise.{SchulteScreen, BlindfoldScreen, NonDominantScreen, StroopScreen}
import com.mindtrainer.ui.theme.MindTrainerTheme

@Composable
fun AppNavHost(mainViewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onComplete = { navController.navigate("auth") { popUpTo("splash") { inclusive = true } } })
        }

        composable("auth") {
            AuthScreen(onAuthenticated = { navController.navigate("onboarding") { popUpTo("auth") { inclusive = true } } })
        }

        composable("onboarding") {
            OnboardingScreen(onComplete = { navController.navigate("home") { popUpTo("onboarding") { inclusive = true } } })
        }

        composable("home") {
            HomeScreen(
                onExerciseClick = { exerciseType ->
                    navController.navigate("exercise/$exerciseType")
                },
                onProgressClick = { navController.navigate("progress") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }

        composable(
            route = "exercise/{exerciseType}",
            arguments = listOf(androidx.navigation.navArgument("exerciseType") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val exerciseType = backStackEntry.getString() ?: "schulte"
            ExerciseScreen(
                exerciseType = exerciseType,
                onComplete = { navController.popBackStack() }
            )
        }

        composable("progress") {
            ProgressScreen(onBack = { navController.popBackStack() })
        }

        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    import androidx.compose.animation.core.animateFloatAsState
    import androidx.compose.animation.core.spring
    import androidx.compose.animation.core.tween
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.Unit
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.compose.material3.Text
    import androidx.compose.material3.ProgressIndicator
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.graphics.vector.ImageVector
    import androidx.compose.ui.res.painterResource
    import androidx.compose.foundation.Image
    import androidx.compose.ui.contentScale

    val scale by animateFloatAsState(targetValue = 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    val alpha by animateFloatAsState(targetValue = 1f, animationSpec = tween(800))

    LaunchedEffect(Unit) {
        androidx.compose.runtime.snapshots.Snapshot.observe {
            Thread.sleep(1500)
        }
        onComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
            ) {
                // Placeholder for logo
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(24.dp))
            Text(
                text = "MindTrainer",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                alpha = alpha
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
            ProgressIndicator(
                modifier = Modifier.size(32.dp).alpha(alpha),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}