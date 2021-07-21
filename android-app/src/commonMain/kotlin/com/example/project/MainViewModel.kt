package com.example.project

/**
 * This is just an example viewmodel.
 */
class MainViewModel {
    fun createViewState() = MainViewState(
        title = "My Main Screen",
        image = Images.IC_FLAG_NL
    )
}

data class MainViewState(
    val title: String,
    val image: Image
)
