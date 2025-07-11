package com.example.u_study.ui.screens.modifyUser

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ModifyUserState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = ""
)

interface ModifyUserActions {
    fun setFirstName(firstName: String)
    fun setLastName(lastName: String)
    fun setEmail(email: String)
    //fun changeValues()
}

class ModifyUserViewModel : ViewModel() {
    private val _state = MutableStateFlow(ModifyUserState())
    val state = _state.asStateFlow()

    val actions = object : ModifyUserActions {
        override fun setFirstName(firstName: String) =
            _state.update { it.copy(firstName = firstName) }

        override fun setLastName(lastName: String) =
            _state.update { it.copy(lastName = lastName) }

        override fun setEmail(email: String) =
            _state.update { it.copy(email = email) }
    }

    init {
        // Il blocco init funziona allo stesso modo per caricare i dati iniziali
        loadInitialUserData()
    }

    private fun loadInitialUserData() {
        _state.update { currentState ->
            currentState.copy(
                firstName = "Sofia",
                lastName = "Bianchi",
                email = "sofiabianchi@gmail.com"
            )
        }
    }
}