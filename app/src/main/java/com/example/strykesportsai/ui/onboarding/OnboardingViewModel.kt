package com.example.strykesportsai.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.strykesportsai.data.local.entity.UserEntity
import com.example.strykesportsai.data.local.entity.UserRole
import com.example.strykesportsai.data.repository.StrykeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(private val repository: StrykeRepository) : ViewModel() {

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName.asStateFlow()

    private val _dob = MutableStateFlow("")
    val dob: StateFlow<String> = _dob.asStateFlow()

    private val _sportsInterests = MutableStateFlow("")
    val sportsInterests: StateFlow<String> = _sportsInterests.asStateFlow()

    private val _selectedRole = MutableStateFlow(UserRole.UNDEFINED)
    val selectedRole: StateFlow<UserRole> = _selectedRole.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    fun onFirstNameChange(newName: String) { _firstName.value = newName }
    fun onLastNameChange(newName: String) { _lastName.value = newName }
    fun onDobChange(newDob: String) { _dob.value = newDob }
    fun onSportsInterestsChange(newInterests: String) { _sportsInterests.value = newInterests }
    
    fun toggleSportInterest(sport: String) {
        val current = _sportsInterests.value.split(", ").filter { it.isNotBlank() }.toMutableList()
        if (current.contains(sport)) {
            current.remove(sport)
        } else {
            current.add(sport)
        }
        _sportsInterests.value = current.joinToString(", ")
    }

    fun onRoleSelected(role: UserRole) { _selectedRole.value = role }

    fun completeOnboarding() {
        viewModelScope.launch {
            val user = UserEntity(
                name = "${_firstName.value} ${_lastName.value}".trim(),
                dob = _dob.value,
                sportsInterests = _sportsInterests.value,
                role = _selectedRole.value
            )
            repository.saveUser(user)
            _onboardingCompleted.value = true
        }
    }
}

class OnboardingViewModelFactory(private val repository: StrykeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnboardingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
