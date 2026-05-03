package com.example.saludity.ui.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saludity.data.local.entity.ActivityEntity
import com.example.saludity.data.local.entity.MoodEntity
import com.example.saludity.data.local.entity.NutritionEntity
import com.example.saludity.data.repository.HealthRepository
import com.example.saludity.data.datastore.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HealthViewModel(
    private val repository: HealthRepository,
    private val userSettings: UserSettings
) : ViewModel() {

    private val today = LocalDate.now()

    val userName: StateFlow<String?> = userSettings.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activityState: StateFlow<ActivityEntity?> = repository.getActivityForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val nutritionState: StateFlow<NutritionEntity?> = repository.getNutritionForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val moodState: StateFlow<MoodEntity?> = repository.getMoodForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allActivities: StateFlow<List<ActivityEntity>> = repository.getAllActivities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNutrition: StateFlow<List<NutritionEntity>> = repository.getAllNutrition()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMoods: StateFlow<List<MoodEntity>> = repository.getAllMoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Default Goals
    val stepGoal = 10000
    val activeMinutesGoal = 30
    val calorieGoal = 2000
    val waterGoalMl = 2500

    fun updateSteps(steps: Int) {
        viewModelScope.launch {
            val current = activityState.value ?: ActivityEntity(date = today, steps = 0, activeMinutes = 0)
            repository.insertActivity(current.copy(steps = steps))
        }
    }

    fun updateActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.insertActivity(activity)
        }
    }

    fun updateNutrition(nutrition: NutritionEntity) {
        viewModelScope.launch {
            repository.insertNutrition(nutrition)
        }
    }

    fun addSteps(stepsToAdd: Int) {
        viewModelScope.launch {
            val current = activityState.value ?: ActivityEntity(date = today, steps = 0, activeMinutes = 0)
            repository.insertActivity(current.copy(steps = current.steps + stepsToAdd))
        }
    }

    fun addActiveMinutes(minutes: Int) {
        viewModelScope.launch {
            val current = activityState.value ?: ActivityEntity(date = today, steps = 0, activeMinutes = 0)
            repository.insertActivity(current.copy(activeMinutes = current.activeMinutes + minutes))
        }
    }

    fun addCalories(kcal: Int) {
        viewModelScope.launch {
            val current = nutritionState.value ?: NutritionEntity(date = today, calories = 0, waterMl = 0)
            repository.insertNutrition(current.copy(calories = current.calories + kcal))
        }
    }

    fun addWater(ml: Int) {
        viewModelScope.launch {
            val current = nutritionState.value ?: NutritionEntity(date = today, calories = 0, waterMl = 0)
            repository.insertNutrition(current.copy(waterMl = current.waterMl + ml))
        }
    }

    fun updateMood(score: Int, note: String?) {
        viewModelScope.launch {
            val current = moodState.value ?: MoodEntity(date = today, moodScore = score, note = note)
            repository.insertMood(current.copy(moodScore = score, note = note))
        }
    }

    fun updateMood(mood: MoodEntity) {
        viewModelScope.launch {
            repository.insertMood(mood)
        }
    }

    fun deleteActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
        }
    }

    fun deleteNutrition(nutrition: NutritionEntity) {
        viewModelScope.launch {
            repository.deleteNutrition(nutrition)
        }
    }

    fun deleteMood(mood: MoodEntity) {
        viewModelScope.launch {
            repository.deleteMood(mood)
        }
    }

    fun setUserName(name: String) {
        viewModelScope.launch {
            userSettings.saveUserName(name)
        }
    }

    fun restoreData() {
        viewModelScope.launch {
            repository.restoreDataFromCloud()
        }
    }

    class Factory(
        private val repository: HealthRepository,
        private val userSettings: UserSettings
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HealthViewModel(repository, userSettings) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
