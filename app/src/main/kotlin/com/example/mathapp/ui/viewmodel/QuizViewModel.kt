package com.example.mathapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mathapp.data.model.Activity
import com.example.mathapp.data.model.Question
import com.example.mathapp.data.repository.MathRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MathRepository(application)

    private val _currentActivity = MutableStateFlow<Activity?>(null)
    val currentActivity = _currentActivity.asStateFlow()

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions = _questions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _correctAnswers = MutableStateFlow(0)
    val correctAnswers = _correctAnswers.asStateFlow()

    private val _answered = MutableStateFlow<List<Boolean>>(emptyList())
    val answered = _answered.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted = _quizCompleted.asStateFlow()

    fun loadActivity(activityId: Int) {
        viewModelScope.launch {
            val activity = repository.getActivityById(activityId)
            _currentActivity.value = activity

            if (activity != null) {
                val questions = repository.getQuestionsByActivity(activityId)
                _questions.value = questions
                _answered.value = List(questions.size) { false }
                _currentQuestionIndex.value = 0
                _score.value = 0
                _correctAnswers.value = 0
            }
        }
    }

    fun answerQuestion(isCorrect: Boolean) {
        val currentQuestion = _questions.value.getOrNull(_currentQuestionIndex.value)
        val activityId = _currentActivity.value?.id

        if (currentQuestion != null && activityId != null) {
            viewModelScope.launch {
                repository.recordAnswer(
                    activityId = activityId,
                    questionId = currentQuestion.id,
                    isCorrect = isCorrect
                )

                if (isCorrect) {
                    _score.value += 10
                    _correctAnswers.value += 1
                }

                val answered = _answered.value.toMutableList()
                answered[_currentQuestionIndex.value] = true
                _answered.value = answered

                if (_currentQuestionIndex.value < _questions.value.size - 1) {
                    _currentQuestionIndex.value += 1
                } else {
                    _quizCompleted.value = true
                }
            }
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun getCurrentQuestion(): Question? =
        _questions.value.getOrNull(_currentQuestionIndex.value)

    fun getAccuracy(): Float {
        val total = _questions.value.size
        return if (total > 0) (_correctAnswers.value.toFloat() / total) * 100 else 0f
    }

    fun restartQuiz() {
        _currentQuestionIndex.value = 0
        _score.value = 0
        _correctAnswers.value = 0
        _quizCompleted.value = false
        _answered.value = List(_questions.value.size) { false }
    }
}
