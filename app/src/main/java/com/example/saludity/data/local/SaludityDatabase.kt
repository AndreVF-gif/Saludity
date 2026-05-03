package com.example.saludity.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.saludity.data.local.dao.HealthDao
import com.example.saludity.data.local.entity.ActivityEntity
import com.example.saludity.data.local.entity.MoodEntity
import com.example.saludity.data.local.entity.NutritionEntity

@Database(
    entities = [ActivityEntity::class, NutritionEntity::class, MoodEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SaludityDatabase : RoomDatabase() {
    abstract fun healthDao(): HealthDao

    companion object {
        const val DATABASE_NAME = "saludity_db"
    }
}
