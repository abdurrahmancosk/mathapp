package com.example.mathapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

enum class BloomLevel {
    REMEMBER,      // Hatırla - Sayıları tanı, basit soruları cevapla
    UNDERSTAND,    // Anla - Kavramları öğren
    APPLY,         // Uygula - Problemleri çöz
    ANALYZE,       // Analiz - Karşılaştır, sınıflandır
    EVALUATE,      // Değerlendir - Cevapları açıkla
    CREATE         // Yarat - Kendi problemlerini oluştur
}

enum class MathTopic {
    COUNTING,           // Sayma
    ADDITION,           // Toplama
    SUBTRACTION,        // Çıkarma
    MULTIPLICATION,     // Çarpma
    DIVISION,           // Bölme
    SHAPES,             // Şekiller
    MEASUREMENT,        // Ölçme
    EVEN_ODD,           // Çift-Tek
    FRACTIONS           // Kesirler
}

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val topic: MathTopic,
    val bloomLevel: BloomLevel,
    val difficulty: Int = 1, // 1-5
    val order: Int = 0
)

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val activityId: Int,
    val question: String,
    val imageUrl: String? = null,
    val correctAnswer: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val explanation: String = "",
    val order: Int = 0
)

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val activityId: Int,
    val questionId: Int,
    val isCorrect: Boolean,
    val attemptCount: Int = 1,
    val score: Int = 0,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val totalQuestionsAnswered: Int = 0,
    val correctAnswers: Int = 0,
    val totalScore: Int = 0,
    val streakDays: Int = 0,
    val lastActivityDate: Long = 0
)

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val requirement: String,
    val unlockedAt: Long? = null,
    val isUnlocked: Boolean = false
)

// UI Models
data class ActivityWithProgress(
    val activity: Activity,
    val completedCount: Int,
    val totalQuestions: Int,
    val percentage: Float
)

data class QuestionUI(
    val id: Int,
    val question: String,
    val imageUrl: String? = null,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

data class UserStats(
    val totalPoints: Int,
    val answeredQuestions: Int,
    val correctAnswers: Int,
    val accuracyPercentage: Float,
    val unlockedBadges: Int,
    val currentStreak: Int
)
