package com.example.saludity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val date: LocalDate,
    val steps: Int,
    val activeMinutes: Int,
    val isSynced: Boolean = false
)
