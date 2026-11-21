package com.example.mathapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mathapp.data.model.*
import com.example.mathapp.data.repository.MathRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MathRepository(application)

    private val _activities = MutableStateFlow<List<Activity>>(emptyList())
    val activities = _activities.asStateFlow()

    private val _stats = MutableStateFlow<UserStats?>(null)
    val stats = _stats.asStateFlow()

    private val _selectedTopic = MutableStateFlow<MathTopic?>(null)
    val selectedTopic = _selectedTopic.asStateFlow()

    private val _selectedBloomLevel = MutableStateFlow<BloomLevel?>(null)
    val selectedBloomLevel = _selectedBloomLevel.asStateFlow()

    init {
        loadActivities()
        loadStats()
        initializeDatabase()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            repository.getAllActivities()
                .collect { _activities.value = it }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            repository.getStats()
                .collect { _stats.value = it }
        }
    }

    fun filterByTopic(topic: MathTopic?) {
        _selectedTopic.value = topic
        viewModelScope.launch {
            if (topic != null) {
                repository.getActivitiesByTopic(topic)
                    .collect { _activities.value = it }
            } else {
                loadActivities()
            }
        }
    }

    fun filterByBloomLevel(level: BloomLevel?) {
        _selectedBloomLevel.value = level
        viewModelScope.launch {
            if (level != null) {
                repository.getActivitiesByBloomLevel(level)
                    .collect { _activities.value = it }
            } else {
                loadActivities()
            }
        }
    }

    private fun initializeDatabase() {
        viewModelScope.launch {
            repository.initializeWithSampleData()
            loadActivities()
        }
    }

    fun getAllTopics(): List<MathTopic> = MathTopic.values().toList()

    fun getAllBloomLevels(): List<BloomLevel> = BloomLevel.values().toList()

    fun getBloomLevelDescription(level: BloomLevel): String = when (level) {
        BloomLevel.REMEMBER -> "Hatırla"
        BloomLevel.UNDERSTAND -> "Anla"
        BloomLevel.APPLY -> "Uygula"
        BloomLevel.ANALYZE -> "Analiz Et"
        BloomLevel.EVALUATE -> "Değerlendir"
        BloomLevel.CREATE -> "Yarat"
    }

    fun getTopicDescription(topic: MathTopic): String = when (topic) {
        MathTopic.COUNTING -> "Sayma"
        MathTopic.ADDITION -> "Toplama"
        MathTopic.SUBTRACTION -> "Çıkarma"
        MathTopic.MULTIPLICATION -> "Çarpma"
        MathTopic.DIVISION -> "Bölme"
        MathTopic.SHAPES -> "Şekiller"
        MathTopic.MEASUREMENT -> "Ölçme"
        MathTopic.EVEN_ODD -> "Çift-Tek"
        MathTopic.FRACTIONS -> "Kesirler"
    }
}
