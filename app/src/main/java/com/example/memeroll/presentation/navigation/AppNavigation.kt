package com.example.memeroll.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.memeroll.presentation.auth.signIn.SignInScreen
import com.example.memeroll.presentation.auth.signIn.SignInViewModel
import com.example.memeroll.presentation.auth.signUp.SignUpScreen
import com.example.memeroll.presentation.auth.signUp.SignUpViewModel
import com.example.memeroll.presentation.main.feed.FeedScreen
import com.example.memeroll.presentation.main.feed.FeedViewModel
import com.example.memeroll.presentation.main.shared.SharedViewModel
import com.example.memeroll.presentation.main.shared.post.PostScreen
import com.example.memeroll.presentation.main.shared.profile.ProfileScreen
import com.example.memeroll.presentation.main.shared.profile.ProfileViewModel
import com.example.memeroll.presentation.navigation.NavRoutes.*
import kotlinx.serialization.Serializable

@Composable
fun AppNavigation(modifier: Modifier = Modifier, scaffoldPadding: PaddingValues) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AuthNav.route, modifier = modifier){

        // Auth Navigation

        navigation(
            route = AuthNav.route,
            enterTransition = {enterTransitions(AuthNav)},
            exitTransition = {exitTransitions(AuthNav)},
            startDestination = SignIn.route
        ){


            composable(
                route = SignIn.route,
                enterTransition = {enterTransitions(SignIn)},
                exitTransition = {exitTransitions(SignIn)},
            ){
                val viewModel = hiltViewModel<SignInViewModel>()
                val state  by viewModel.state.collectAsStateWithLifecycle()
                SignInScreen(
                    modifier = Modifier.padding(scaffoldPadding),
                    onEvent = viewModel::onEvent,
                    state = state,
                    authComplete = {
                        navController.navigate(route = MainNav.route){
                           popUpTo(AuthNav.route){inclusive = true}
                        }
                        Log.d("Authentication", "Sign In Complete")
                    },
                    navigateToSignUp = {
                        navController.navigate(route = SignUp.route)
                    }

                )
            }

            composable(
                route = SignUp.route,
                enterTransition = { enterTransitions(SignUp) },
                exitTransition = { exitTransitions(SignUp) }
            ){
                val viewModel = hiltViewModel<SignUpViewModel>()
                val state  by viewModel.state.collectAsStateWithLifecycle()
                SignUpScreen(
                    modifier = Modifier.padding(scaffoldPadding),
                    onEvent = viewModel::onEvent,
                    state = state,
                    authComplete = {
                        navController.navigate(route = MainNav.route){
                            popUpTo(AuthNav.route)
                        }
                        Log.d("Authentication", "Sign Up Complete")
                    }
                )
            }
        }


        // Main Navigation

        navigation(
            route = MainNav.route,
            startDestination = Feed.route,
            enterTransition = { enterTransitions(MainNav) },
            exitTransition = { exitTransitions(MainNav) },
        ){

            composable(
                route = Feed.route,
                enterTransition = { enterTransitions(Feed) },
                exitTransition = { exitTransitions(Feed) },
            ){

                val viewModel = hiltViewModel<FeedViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()

                FeedScreen(
                    modifier = Modifier.padding(scaffoldPadding),
                    state = state,
                    onEvent = viewModel::onEvent,
                    navigateToProfile = {
                        navController.navigate(ProfileRoute())
                    }
                )
            }

            composable<ProfileRoute>(
                enterTransition = { enterTransitions(Profile) },
                exitTransition ={exitTransitions(Profile)}
            ){

                val sharedViewModel = it.sharedViewModel<SharedViewModel>(navController)
                val sharedState by sharedViewModel.state.collectAsState()

                val viewModel = it.sharedViewModel<ProfileViewModel>(navController)
                val state by viewModel.state.collectAsStateWithLifecycle()

                ProfileScreen(
                    state = state,
                    navigateToPostMeme = { navController.navigate(PostRoute(it.toString())) },
                    onSharedEvent = sharedViewModel::onSharedEvent,
                    sharedState = sharedState,
                    onProfileEvent = viewModel::onEvent,
                    navigateToAuth = {
                        navController.navigate(AuthNav.route){
                            popUpTo(MainNav.route) {inclusive = true}
                        }
                    }
                )
            }

            composable<PostRoute>(
                enterTransition = { enterTransitions(Post) },
                exitTransition = {exitTransitions(Post)}
            ){

                val sharedViewModel = it.sharedViewModel<SharedViewModel>(navController)
                val sharedState by sharedViewModel.state.collectAsState()

                PostScreen(
                    modifier = Modifier.padding(scaffoldPadding),
                    onSharedEvent = sharedViewModel::onSharedEvent,
                    navigateBack = {
                        navController.navigateUp()
                    },
                    sharedState = sharedState
                )
            }


        }
    }
}

@Serializable
data class PostRoute(val uriString: String)

@Serializable
data class ProfileRoute(val workId: String? = null)

@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T{

    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember (this){
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}


enum class NavRoutes(val route: String){

    MainNav("MainNavigation"),
    AuthNav("AuthNavigation"),
    SignIn("SignInRoute"),
    SignUp("SignUpRoute"),
    Feed("FeedRoute"),
    Profile("ProfileRoute"),
    Post("PostRoute")
}