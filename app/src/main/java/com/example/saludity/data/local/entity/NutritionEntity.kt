package com.example.saludity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "nutrition")
data class NutritionEntity(
    @PrimaryKey val date: LocalDate,
    val calories: Int,
    val waterMl: Int,
    val isSynced: Boolean = false
)
