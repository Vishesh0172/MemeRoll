package com.example.memeroll.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.memeroll.presentation.auth.signIn.SignInScreen
import com.example.memeroll.presentation.auth.signIn.SignInViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memeroll.presentation.auth.signUp.SignUpScreen
import com.example.memeroll.presentation.auth.signUp.SignUpViewModel
import com.example.memeroll.presentation.main.feed.FeedScreen
import com.example.memeroll.presentation.main.feed.FeedViewModel
import com.example.memeroll.presentation.main.profile.ProfileScreen
import com.example.memeroll.presentation.main.profile.ProfileViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier, scaffoldPadding: PaddingValues) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "AuthNavigation"){

        // Auth Navigation
        navigation(route = "AuthNavigation", startDestination = "SignInRoute"){

            composable(route = "SignInRoute"){
                val viewModel = hiltViewModel<SignInViewModel>()
                val state  by viewModel.state.collectAsStateWithLifecycle()
                SignInScreen(
                    modifier = Modifier.padding(scaffoldPadding),
                    onEvent = viewModel::onEvent,
                    state = state,
                    authComplete = {
                        navController.navigate(route = "MainNavigation"){
                           popUpTo("AuthNavigation")
                        }
                        Log.d("Authentication", "Sign In Complete")
                    },
                    navigateToSignUp = {
                        navController.navigate(route = "SignUpRoute")
                    }

                )
            }

            composable(route = "SignUpRoute"){
                val viewModel = hiltViewModel<SignUpViewModel>()
                val state  by viewModel.state.collectAsStateWithLifecycle()
                SignUpScreen(
                    modifier = Modifier.padding(scaffoldPadding),
                    onEvent = viewModel::onEvent,
                    state = state,
                    authComplete = {
                        navController.navigate(route = "MainNavigation"){
                            popUpTo("AuthNavigation")
                        }
                        Log.d("Authentication", "Sign Up Complete")
                    }
                )
            }
        }

        // Main Navigation
        navigation(route = "MainNavigation", startDestination = "FeedRoute"){

            composable(route = "FeedRoute"){
                val viewModel = hiltViewModel<FeedViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()
                FeedScreen(
                    modifier = Modifier.padding(scaffoldPadding),
                    state = state,
                    onEvent = viewModel::onEvent,
                    navigateToAuth = {
                        navController.navigate("AuthNavigation"){
                            popUpTo("MainNavigation") {inclusive = true}
                        }
                    },
                    navigateToProfile = {
                        navController.navigate("ProfileRoute")
                    }
                )
            }

            composable("ProfileRoute"){
                val viewModel = hiltViewModel<ProfileViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()
                ProfileScreen(state = state) { }
            }


        }
    }
}