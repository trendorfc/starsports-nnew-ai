package com.example.strykesportsai.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.strykesportsai.data.local.entity.BookingEntity
import com.example.strykesportsai.data.local.entity.MatchEntity
import com.example.strykesportsai.data.local.entity.TurfEntity
import com.example.strykesportsai.data.local.entity.UserEntity
import com.example.strykesportsai.data.local.entity.UserRole
import com.example.strykesportsai.data.repository.StrykeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BookingWithTurf(
    val booking: BookingEntity,
    val turf: TurfEntity?
)

class PlayerViewModel(private val repository: StrykeRepository) : ViewModel() {

    val user: StateFlow<UserEntity?> = repository.getUser().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSport = MutableStateFlow<String?>(null)
    val selectedSport: StateFlow<String?> = _selectedSport.asStateFlow()

    val turfs: StateFlow<List<TurfEntity>> = repository.getAllTurfs().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val userBookings: StateFlow<List<BookingEntity>> = user.flatMapLatest { currentUser ->
        if (currentUser != null) {
            repository.getBookingsByUser(currentUser.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val bookingsWithTurf: StateFlow<List<BookingWithTurf>> = combine(userBookings, turfs) { bookings, turfList ->
        bookings.map { booking ->
            BookingWithTurf(booking, turfList.find { it.id == booking.turfId })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredTurfs = combine(turfs, selectedSport) { list, sport ->
        if (sport == null) list
        else list.filter { 
            it.sportsSupported.contains(sport, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val matches: StateFlow<List<MatchEntity>> = repository.getAllMatches().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _joinedMatchIds = MutableStateFlow<Set<Long>>(emptySet())
    val joinedMatchIds: StateFlow<Set<Long>> = _joinedMatchIds.asStateFlow()

    fun joinMatch(matchId: Long) {
        _joinedMatchIds.value = _joinedMatchIds.value + matchId
    }

    private val _leftMatchIds = MutableStateFlow<Set<Long>>(emptySet())
    val leftMatchIds: StateFlow<Set<Long>> = _leftMatchIds.asStateFlow()

    fun leaveMatch(matchId: Long, reason: String) {
        _joinedMatchIds.value = _joinedMatchIds.value - matchId
        _leftMatchIds.value = _leftMatchIds.value + matchId
    }

    val upcomingMatches = combine(matches, selectedSport, leftMatchIds) { list, sport, leftIds ->
        val currentTime = System.currentTimeMillis()
        list.filter { it.startTime > currentTime }
            .filter { !leftIds.contains(it.id) }
            .filter { sport == null || it.sport.equals(sport, ignoreCase = true) }
            .sortedBy { it.startTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myJoinedMatches = combine(matches, joinedMatchIds, user) { list, joinedIds, currentUser ->
        val currentTime = System.currentTimeMillis()
        list.filter { it.creatorId == currentUser?.id || joinedIds.contains(it.id) }
            .filter { it.startTime > currentTime }
            .sortedBy { it.startTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pastMatches = combine(matches, joinedMatchIds, leftMatchIds, user) { list, joinedIds, leftIds, currentUser ->
        val currentTime = System.currentTimeMillis()
        list.filter { 
            (it.startTime <= currentTime && (it.creatorId == currentUser?.id || joinedIds.contains(it.id))) ||
            leftIds.contains(it.id)
        }
            .sortedByDescending { it.startTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSportSelected(sport: String?) {
        _selectedSport.value = sport
    }

    val filteredMatches = combine(matches, selectedSport) { list, sport ->
        if (sport == null) list
        else list.filter { it.sport.equals(sport, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mock players for discovery (In a real app, this would come from a server or Room)
    private val _players = MutableStateFlow(listOf(
        UserEntity(id = 101, name = "John Doe", dob = "01/01/1995", sportsInterests = "Football", role = UserRole.PLAYER, phoneNumber = "9876543210"),
        UserEntity(id = 102, name = "Jane Smith", dob = "05/05/1998", sportsInterests = "Cricket", role = UserRole.PLAYER, phoneNumber = "8765432109"),
        UserEntity(id = 103, name = "Mike Ross", dob = "12/12/1990", sportsInterests = "Tennis", role = UserRole.PLAYER, phoneNumber = "7654321098")
    ))
    val players: StateFlow<List<UserEntity>> = _players.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun switchRole() {
        viewModelScope.launch {
            user.value?.let { currentUser ->
                val newRole = if (currentUser.role == UserRole.PLAYER) UserRole.TURF_OWNER else UserRole.PLAYER
                repository.updateUser(currentUser.copy(role = newRole))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearUser()
        }
    }

    private val _createMatchSuccess = MutableSharedFlow<Boolean>()
    val createMatchSuccess = _createMatchSuccess.asSharedFlow()

    fun createMatch(sport: String, dateTime: Long, location: String, playersNeeded: Int, description: String) {
        viewModelScope.launch {
            val creatorId = user.value?.id ?: 0L
            val match = MatchEntity(
                creatorId = creatorId,
                turfId = null,
                location = location,
                sport = sport,
                startTime = dateTime,
                endTime = dateTime + 3600000, // +1 hour
                playersNeeded = playersNeeded,
                currentPlayers = 1,
                description = description
            )
            val id = repository.saveMatch(match)
            _joinedMatchIds.value = _joinedMatchIds.value + (id as Long)
            _selectedSport.value = null // Reset filter to show all matches
            _createMatchSuccess.emit(true)
        }
    }

    fun updateProfile(name: String, photoUrl: String?) {
        viewModelScope.launch {
            user.value?.let { currentUser ->
                repository.updateUser(currentUser.copy(name = name, profileImageUrl = photoUrl))
            }
        }
    }
}

class PlayerViewModelFactory(private val repository: StrykeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
