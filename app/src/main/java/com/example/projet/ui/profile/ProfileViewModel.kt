package com.example.projet.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.createSavedStateHandle
import com.example.projet.data.dao.UserDao
import com.example.projet.data.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userDao: UserDao) : ViewModel() {
    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState = _userState.asStateFlow()
    
    fun loadUserData(userId: Int) {
        viewModelScope.launch {
            try {
                val user = userDao.getUserById(userId)
                _userState.value = UserState.Success(user)
            } catch(e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class UserState {
    object Loading : UserState()
    data class Success(val user: User?) : UserState()
    data class Error(val message: String) : UserState()
}

class ProfileViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
