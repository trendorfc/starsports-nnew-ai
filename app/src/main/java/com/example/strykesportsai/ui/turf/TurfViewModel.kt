package com.example.strykesportsai.ui.turf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.strykesportsai.data.local.entity.BookingEntity
import com.example.strykesportsai.data.local.entity.BookingStatus
import com.example.strykesportsai.data.repository.StrykeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class TurfViewModel(private val repository: StrykeRepository) : ViewModel() {

    private val _bookingSuccess = MutableSharedFlow<Boolean>()
    val bookingSuccess = _bookingSuccess.asSharedFlow()

    fun bookSlot(userId: Long, turfId: Long, slot: String, price: Double) {
        viewModelScope.launch {
            // In a real app, parse slot to timestamp
            val startTime = System.currentTimeMillis() 
            val booking = BookingEntity(
                userId = userId,
                turfId = turfId,
                startTime = startTime,
                endTime = startTime + 3600000,
                status = BookingStatus.CONFIRMED,
                totalPrice = price
            )
            repository.saveBooking(booking)
            _bookingSuccess.emit(true)
        }
    }
}

class TurfViewModelFactory(private val repository: StrykeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TurfViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TurfViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
