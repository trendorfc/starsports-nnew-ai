package com.example.strykesportsai.data.repository

import com.example.strykesportsai.data.local.dao.BookingDao
import com.example.strykesportsai.data.local.dao.MatchDao
import com.example.strykesportsai.data.local.dao.TurfDao
import com.example.strykesportsai.data.local.dao.UserDao
import com.example.strykesportsai.data.local.entity.BookingEntity
import com.example.strykesportsai.data.local.entity.MatchEntity
import com.example.strykesportsai.data.local.entity.TurfEntity
import com.example.strykesportsai.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class StrykeRepository(
    private val userDao: UserDao,
    private val turfDao: TurfDao,
    private val bookingDao: BookingDao,
    private val matchDao: MatchDao
) {
    // User
    fun getUser(): Flow<UserEntity?> = userDao.getUser()
    suspend fun saveUser(user: UserEntity): Long = userDao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    suspend fun clearUser() = userDao.clearUser()

    // Turfs
    fun getAllTurfs(): Flow<List<TurfEntity>> = turfDao.getAllTurfs()
    fun getTurfsByOwner(ownerId: Long): Flow<List<TurfEntity>> = turfDao.getTurfsByOwner(ownerId)
    suspend fun getTurfById(id: Long) = turfDao.getTurfById(id)
    suspend fun saveTurf(turf: TurfEntity) = turfDao.insertTurf(turf)

    // Bookings
    fun getBookingsByUser(userId: Long): Flow<List<BookingEntity>> = bookingDao.getBookingsByUser(userId)
    fun getBookingsByTurf(turfId: Long): Flow<List<BookingEntity>> = bookingDao.getBookingsByTurf(turfId)
    suspend fun saveBooking(booking: BookingEntity) = bookingDao.insertBooking(booking)

    // Matches
    fun getAllMatches(): Flow<List<MatchEntity>> = matchDao.getAllMatches()
    fun getMatchesByCreator(creatorId: Long): Flow<List<MatchEntity>> = matchDao.getMatchesByCreator(creatorId)
    suspend fun saveMatch(match: MatchEntity) = matchDao.insertMatch(match)
}
