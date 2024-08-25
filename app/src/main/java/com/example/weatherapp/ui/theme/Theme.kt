package com.example.weatherapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColors(
    primary = Purple80,
    secondary = PurpleGrey80,
    primaryVariant = Pink80
)

private val LightColorScheme = lightColors(
    primary = Purple40,
    secondary = PurpleGrey40,
    primaryVariant = Pink40
)

@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    if (Build.VERSION.SDK_INT >= 29) {
        LocalView.current.isForceDarkAllowed = false
    }
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
    }


    MaterialTheme(
        colors = colorScheme,
        typography = Typography,
        content = content
    )
}