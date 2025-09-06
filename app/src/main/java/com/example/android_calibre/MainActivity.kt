package com.example.android_calibre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.android_calibre.ui.theme.AndroidcalibreTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Books : Screen("books", "Books", Icons.Default.Book)
    object Comics : Screen("comics", "Comics", Icons.Default.MenuBook)
    object Articles : Screen("articles", "Articles", Icons.Default.Newspaper)
}

val items = listOf(
    Screen.Books,
    Screen.Comics,
    Screen.Articles,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidcalibreTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = items.find { it.route == currentDestination?.route } ?: Screen.Books

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) }
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Books.route, Modifier.padding(innerPadding)) {
            composable(Screen.Books.route) { BooksScreen() }
            composable(Screen.Comics.route) { ComicsScreen() }
            composable(Screen.Articles.route) { ArticlesScreen() }
        }
    }
}

@Composable
fun BooksScreen() {
    Box(modifier = Modifier.padding()) {
        Text(text = "Books Screen")
    }
}

@Composable
fun ComicsScreen() {
    Box(modifier = Modifier.padding()) {
        Text(text = "Comics Screen")
    }
}

@Composable
fun ArticlesScreen() {
    Box(modifier = Modifier.padding()) {
        Text(text = "Articles Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidcalibreTheme {
        MainScreen()
    }
}
