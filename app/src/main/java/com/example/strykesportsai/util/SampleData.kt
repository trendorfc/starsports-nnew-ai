package com.example.strykesportsai.util

import com.example.strykesportsai.data.local.entity.TurfEntity
import com.example.strykesportsai.data.repository.StrykeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SampleData {
    fun insertSampleTurfs(repository: StrykeRepository) {
        val sampleTurfs = listOf(
            TurfEntity(
                name = "Stryke Arena",
                location = "Downtown, City Center",
                sportsSupported = "Football, Cricket",
                pricePerHour = 1200.0,
                description = "World-class turf with modern amenities and floodlights for night matches.",
                ownerId = 1,
                imageUrls = ""
            ),
            TurfEntity(
                name = "Elite Sports Complex",
                location = "Green Valley, North Side",
                sportsSupported = "Cricket, Tennis",
                pricePerHour = 1500.0,
                description = "Premium multi-sport complex offering high-quality turf and expert coaching.",
                ownerId = 1,
                imageUrls = ""
            ),
            TurfEntity(
                name = "Velocity Turf",
                location = "Industrial Area, East Side",
                sportsSupported = "Football",
                pricePerHour = 1000.0,
                description = "High-speed turf designed for intense football matches. Open 24/7.",
                ownerId = 1,
                imageUrls = ""
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            sampleTurfs.forEach { repository.saveTurf(it) }
        }
    }
}
