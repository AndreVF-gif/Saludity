package com.example.saludity.data.repository

import android.util.Log
import com.example.saludity.data.local.entity.ActivityEntity
import com.example.saludity.data.local.entity.MoodEntity
import com.example.saludity.data.local.entity.NutritionEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class FirebaseRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val userId: String?
        get() = auth.currentUser?.uid

    private val TAG = "FirebaseSync"

    suspend fun syncActivity(activity: ActivityEntity): Boolean {
        return try {
            val uid = userId ?: return false
            Log.d(TAG, "Attempting to sync activity for user $uid on date ${activity.date}")
            val docRef = firestore.collection("users").document(uid)
                .collection("activities").document(activity.date.toString())
            
            val data = mapOf(
                "steps" to activity.steps,
                "activeMinutes" to activity.activeMinutes,
                "date" to activity.date.toString()
            )
            docRef.set(data).await()
            Log.d(TAG, "Successfully synced activity for ${activity.date}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing activity for ${activity.date}: ${e.message}")
            false
        }
    }

    suspend fun syncNutrition(nutrition: NutritionEntity): Boolean {
        return try {
            val uid = userId ?: return false
            Log.d(TAG, "Attempting to sync nutrition for user $uid on date ${nutrition.date}")
            val docRef = firestore.collection("users").document(uid)
                .collection("nutrition").document(nutrition.date.toString())
            
            val data = mapOf(
                "calories" to nutrition.calories,
                "waterMl" to nutrition.waterMl,
                "date" to nutrition.date.toString()
            )
            docRef.set(data).await()
            Log.d(TAG, "Successfully synced nutrition for ${nutrition.date}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing nutrition for ${nutrition.date}: ${e.message}")
            false
        }
    }

    suspend fun syncMood(mood: MoodEntity): Boolean {
        return try {
            val uid = userId ?: return false
            Log.d(TAG, "Attempting to sync mood for user $uid on date ${mood.date}")
            val docRef = firestore.collection("users").document(uid)
                .collection("moods").document(mood.date.toString())
            
            val data = mapOf(
                "moodScore" to mood.moodScore,
                "note" to mood.note,
                "date" to mood.date.toString()
            )
            docRef.set(data).await()
            Log.d(TAG, "Successfully synced mood for ${mood.date}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing mood for ${mood.date}: ${e.message}")
            false
        }
    }

    suspend fun deleteActivity(date: LocalDate) {
        val uid = userId ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("activities").document(date.toString())
                .delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting activity: ${e.message}")
        }
    }

    suspend fun deleteNutrition(date: LocalDate) {
        val uid = userId ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("nutrition").document(date.toString())
                .delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting nutrition: ${e.message}")
        }
    }

    suspend fun deleteMood(date: LocalDate) {
        val uid = userId ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("moods").document(date.toString())
                .delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting mood: ${e.message}")
        }
    }

    suspend fun fetchAllActivities(): List<ActivityEntity> {
        val uid = userId ?: return emptyList()
        return try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("activities").get().await()
            snapshot.documents.mapNotNull { doc ->
                val date = LocalDate.parse(doc.id)
                val steps = doc.getLong("steps")?.toInt() ?: 0
                val activeMinutes = doc.getLong("activeMinutes")?.toInt() ?: 0
                ActivityEntity(date, steps, activeMinutes, isSynced = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching activities: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchAllNutrition(): List<NutritionEntity> {
        val uid = userId ?: return emptyList()
        return try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("nutrition").get().await()
            snapshot.documents.mapNotNull { doc ->
                val date = LocalDate.parse(doc.id)
                val calories = doc.getLong("calories")?.toInt() ?: 0
                val waterMl = doc.getLong("waterMl")?.toInt() ?: 0
                NutritionEntity(date, calories, waterMl, isSynced = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching nutrition: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchAllMoods(): List<MoodEntity> {
        val uid = userId ?: return emptyList()
        return try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("moods").get().await()
            snapshot.documents.mapNotNull { doc ->
                val date = LocalDate.parse(doc.id)
                val moodScore = doc.getLong("moodScore")?.toInt() ?: 0
                val note = doc.getString("note")
                MoodEntity(date, moodScore, note, isSynced = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching moods: ${e.message}")
            emptyList()
        }
    }
}
