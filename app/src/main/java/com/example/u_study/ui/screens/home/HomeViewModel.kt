package com.example.u_study.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.ui.screens.register.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeState(
    val isAuthenticated: Boolean = false,
    val name: String? = null
)

interface HomeActions {

}

class HomeViewModel (
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    val actions = object : HomeActions {

    }

}