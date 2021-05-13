package com.example.navigationcomponentapp.extensions

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.example.navigationcomponentapp.R

private val navOptions = NavOptions.Builder()
    .setEnterAnim(R.anim.fragment_fade_enter)
    .setExitAnim(R.anim.fragment_fade_exit)
    .setPopEnterAnim(R.anim.fragment_fade_enter)
    .setPopExitAnim(R.anim.fragment_fade_exit)
    .build()


fun NavController.navigateWithAnimation(destinationId: Int) {
    this.navigate(destinationId, null, navOptions)
}

fun NavController.navigateWithAnimation(directions: NavDirections) {
    this.navigate(directions, navOptions)
}