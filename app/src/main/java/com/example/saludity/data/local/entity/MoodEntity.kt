package com.example.saludity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "moods")
data class MoodEntity(
    @PrimaryKey val date: LocalDate,
    val moodScore: Int, // 1 to 5
    val note: String? = null,
    val isSynced: Boolean = false
)
