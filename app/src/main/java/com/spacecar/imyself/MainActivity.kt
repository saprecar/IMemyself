package com.spacecar.imyself

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spacecar.imyself.ui.AboutScreen
import com.spacecar.imyself.ui.CalendarScreen
import com.spacecar.imyself.ui.DisclaimerScreen
import com.spacecar.imyself.ui.PersonalLogScreen
import com.spacecar.imyself.ui.ReportScreen
import com.spacecar.imyself.ui.RuleBookScreen
import com.spacecar.imyself.ui.TrackingViewModel

import com.spacecar.imyself.ui.theme.IMyselfTheme

class MainActivity : ComponentActivity() {
    private val trackingViewModel: TrackingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val disclaimerPrefs = getSharedPreferences("disclaimer_prefs", Context.MODE_PRIVATE)
        
        val currentVersionCode = try {
            packageManager.getPackageInfo(packageName, 0).let {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    it.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    it.versionCode.toLong()
                }
            }
        } catch (e: Exception) {
            1L
        }

        val lastAcceptedVersion = disclaimerPrefs.getLong("last_accepted_version", 0L)
        val lastAcceptedTime = disclaimerPrefs.getLong("last_accepted_time", 0L)
        val thirtyDaysMillis = 30L * 24L * 60L * 60L * 1000L
        
        val needsDisclaimer = (currentVersionCode != lastAcceptedVersion) || 
                              (System.currentTimeMillis() - lastAcceptedTime > thirtyDaysMillis)
        
        val initialDestination = if (needsDisclaimer) "disclaimer" else "calendar"
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkTheme by remember {
                mutableStateOf(sharedPrefs.getBoolean("is_dark_theme", systemTheme))
            }
            
            val toggleTheme: () -> Unit = {
                isDarkTheme = !isDarkTheme
                sharedPrefs.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
            }

            IMyselfTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = initialDestination) {
                        composable("disclaimer") {
                            DisclaimerScreen(onAccept = {
                                disclaimerPrefs.edit()
                                    .putLong("last_accepted_version", currentVersionCode)
                                    .putLong("last_accepted_time", System.currentTimeMillis())
                                    .apply()
                                navController.navigate("calendar") {
                                    popUpTo("disclaimer") { inclusive = true }
                                }
                            })
                        }
                        composable("calendar") {
                            CalendarScreen(
                                viewModel = trackingViewModel,
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = toggleTheme,
                                onOpenRuleBook = { navController.navigate("rulebook") },
                                onOpenLogs = { navController.navigate("personallogs") },
                                onOpenReport = { navController.navigate("report") },
                                onOpenAbout = { navController.navigate("about") }
                            )
                        }
                        composable("rulebook") {
                            RuleBookScreen(onBack = { navController.popBackStack() })
                        }
                        composable("personallogs") {
                            PersonalLogScreen(
                                viewModel = trackingViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("report") {
                            ReportScreen(
                                viewModel = trackingViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("about") {
                            AboutScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
