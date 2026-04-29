package com.example.strykesportsai.ui.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.strykesportsai.data.local.entity.BookingEntity
import com.example.strykesportsai.data.local.entity.TurfEntity
import com.example.strykesportsai.data.local.entity.UserEntity
import com.example.strykesportsai.data.repository.StrykeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OwnerViewModel(private val repository: StrykeRepository) : ViewModel() {

    val user: StateFlow<UserEntity?> = repository.getUser().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _ownerTurfs = MutableStateFlow<List<TurfEntity>>(emptyList())
    val ownerTurfs: StateFlow<List<TurfEntity>> = _ownerTurfs.asStateFlow()

    init {
        viewModelScope.launch {
            user.collectLatest { currentUser ->
                currentUser?.let {
                    repository.getTurfsByOwner(it.id).collect { turfs ->
                        _ownerTurfs.value = turfs
                    }
                }
            }
        }
    }

    private val _operationSuccess = MutableSharedFlow<Boolean>()
    val operationSuccess = _operationSuccess.asSharedFlow()

    fun saveTurf(name: String, location: String, sports: String, price: Double, description: String, imageUrl: String = "", turfId: Long = 0) {
        viewModelScope.launch {
            val ownerId = user.value?.id ?: 0L
            val turf = TurfEntity(
                id = turfId,
                ownerId = ownerId,
                name = name,
                location = location,
                sportsSupported = sports,
                pricePerHour = price,
                description = description,
                imageUrls = imageUrl
            )
            repository.saveTurf(turf)
            _operationSuccess.emit(true)
        }
    }

    fun deleteTurf(turf: TurfEntity) {
        viewModelScope.launch {
            repository.deleteTurf(turf)
        }
    }

    fun updateTurfTimings(turfId: Long, timings: String, openDays: String) {
        viewModelScope.launch {
            val turf = ownerTurfs.value.find { it.id == turfId }
            turf?.let {
                repository.saveTurf(it.copy(availableTimings = timings, openDays = openDays))
            }
        }
    }

    fun getBookingsForTurf(turfId: Long): Flow<List<BookingEntity>> {
        return repository.getBookingsByTurf(turfId)
    }

    fun switchRole() {
        viewModelScope.launch {
            user.value?.let { currentUser ->
                val newRole = if (currentUser.role == com.example.strykesportsai.data.local.entity.UserRole.PLAYER) com.example.strykesportsai.data.local.entity.UserRole.TURF_OWNER else com.example.strykesportsai.data.local.entity.UserRole.PLAYER
                repository.updateUser(currentUser.copy(role = newRole))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearUser()
        }
    }

    fun updateProfile(name: String, photoUrl: String?) {
        viewModelScope.launch {
            user.value?.let { currentUser ->
                repository.updateUser(currentUser.copy(name = name, profileImageUrl = photoUrl))
            }
        }
    }

    suspend fun getPlayer(userId: Long): UserEntity? {
        return repository.getUserById(userId)
    }

    suspend fun getPlayerName(userId: Long): String {
        return repository.getUserById(userId)?.name ?: "Unknown Player"
    }
}

class OwnerViewModelFactory(private val repository: StrykeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OwnerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OwnerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
