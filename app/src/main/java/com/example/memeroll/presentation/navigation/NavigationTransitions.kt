package com.example.memeroll.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import com.example.memeroll.presentation.navigation.NavRoutes.*


fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransitions(route: NavRoutes): EnterTransition{

    val previousRoute = initialState.destination.route ?: ""
    when(route){

        AuthNav -> {
            return when(previousRoute){
                MainNav.route -> slideInHorizontally()
                else -> fadeIn()
            }
        }
        SignIn -> {
            return when(previousRoute){
                SignUp.route -> slideInHorizontally()
                else -> fadeIn()
            }
        }
        SignUp -> {
            return when(previousRoute){
                SignIn.route -> slideInHorizontally(initialOffsetX = {it/2})
                else -> fadeIn()
            }
        }

        MainNav ->{
            return when(previousRoute){
                AuthNav.route -> slideInHorizontally(initialOffsetX = {it/2})
                else -> fadeIn()
            }
        }

        Feed ->{
            return when{
                previousRoute.startsWith(Profile.route) -> slideInHorizontally()
                else -> fadeIn()
            }
        }

        Profile -> return when{
            previousRoute.startsWith(Feed.route) -> slideInHorizontally(initialOffsetX = {it/2})
            previousRoute.startsWith(Post.route) -> slideInHorizontally()
            else -> fadeIn()
        }

        Post -> {
            return when{
                previousRoute.startsWith(Profile.route) -> slideInHorizontally(initialOffsetX = {it/2})
                else -> fadeIn()
            }
        }
    }

}


fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransitions(route: NavRoutes): ExitTransition{

    val nextRoute = targetState.destination.route ?: ""
    when(route){

        AuthNav -> {
            return when(nextRoute){
                MainNav.route -> slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                Feed.route -> slideOutHorizontally()
                else -> fadeOut()
            }
        }
        SignIn -> {
            return when(nextRoute){
                SignUp.route -> slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                else -> fadeOut()
            }
        }
        SignUp -> {
            return when(nextRoute){
                SignIn.route -> slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                else -> fadeOut()
            }
        }

        MainNav ->{
            return when(nextRoute){
                AuthNav.route -> slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                else -> fadeOut()
            }
        }

        Feed ->{
            return when{
                nextRoute.startsWith(Profile.route) -> slideOutHorizontally()
                else -> fadeOut()
            }
        }

        Profile -> return when{
            nextRoute == Feed.route -> slideOutHorizontally(targetOffsetX = {it})
            nextRoute == SignIn.route -> slideOutHorizontally(targetOffsetX = {it})
            nextRoute.startsWith(Post.route) -> slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            else -> fadeOut()
        }

        Post -> {
            return when{
                nextRoute.startsWith(Profile.route) -> slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                else -> fadeOut()
            }
        }
    }

}
