package com.example.mathapp.data.repository

import android.content.Context
import com.example.mathapp.data.db.*
import com.example.mathapp.data.model.*
import kotlinx.coroutines.flow.Flow

class MathRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val activityDao = database.activityDao()
    private val questionDao = database.questionDao()
    private val progressDao = database.progressDao()
    private val statsDao = database.statsDao()
    private val badgeDao = database.badgeDao()

    // Activity operations
    fun getAllActivities(): Flow<List<Activity>> = activityDao.getAllActivities()

    suspend fun getActivityById(id: Int) = activityDao.getActivityById(id)

    fun getActivitiesByTopic(topic: MathTopic): Flow<List<Activity>> =
        activityDao.getActivitiesByTopic(topic.name)

    fun getActivitiesByBloomLevel(level: BloomLevel): Flow<List<Activity>> =
        activityDao.getActivitiesByBloomLevel(level.name)

    // Question operations
    suspend fun getQuestionsByActivity(activityId: Int) =
        questionDao.getQuestionsByActivity(activityId)

    suspend fun getQuestionById(id: Int) = questionDao.getQuestionById(id)

    // Progress operations
    suspend fun recordAnswer(
        activityId: Int,
        questionId: Int,
        isCorrect: Boolean
    ) {
        val progress = UserProgress(
            activityId = activityId,
            questionId = questionId,
            isCorrect = isCorrect,
            score = if (isCorrect) 10 else 0
        )
        progressDao.insertProgress(progress)

        statsDao.incrementQuestionCount()
        if (isCorrect) {
            statsDao.incrementCorrectAnswers()
            statsDao.addScore(10)
        }
    }

    suspend fun getActivityProgress(activityId: Int): Pair<Int, Int> {
        val completed = progressDao.getCompletedQuestionCount(activityId)
        val total = questionDao.getQuestionCountByActivity(activityId)
        return Pair(completed, total)
    }

    suspend fun getAccuracy(activityId: Int): Float =
        progressDao.getAccuracyPercentage(activityId)

    // Stats operations
    fun getStats(): Flow<UserStats?> = statsDao.getStats()

    suspend fun initializeStats() {
        val existingStats = statsDao.getStats()
        // Stats will be created on first use
    }

    // Badge operations
    fun getAllBadges(): Flow<List<Badge>> = badgeDao.getAllBadges()

    fun getUnlockedBadges(): Flow<List<Badge>> = badgeDao.getUnlockedBadges()

    suspend fun unlockBadge(badgeId: Int) {
        val badge = badgeDao.getAllBadges()
        // Update badge logic handled elsewhere
    }

    // Initialize with sample data
    suspend fun initializeWithSampleData() {
        val activities = getSampleActivities()
        for (activity in activities) {
            activityDao.insert(activity)
        }

        val questions = getSampleQuestions()
        for (question in questions) {
            questionDao.insert(question)
        }

        val badges = getSampleBadges()
        for (badge in badges) {
            badgeDao.insertBadge(badge)
        }

        // Initialize stats
        statsDao.insertStats(UserStats())
    }

    private fun getSampleActivities(): List<Activity> = listOf(
        // Remember Level
        Activity(
            title = "Sayıları Tanı (1-10)",
            description = "1'den 10'a kadar sayıları tanıyın",
            topic = MathTopic.COUNTING,
            bloomLevel = BloomLevel.REMEMBER,
            difficulty = 1,
            order = 1
        ),
        Activity(
            title = "Basit Toplama (0-5)",
            description = "5'e kadar basit toplama problemleri",
            topic = MathTopic.ADDITION,
            bloomLevel = BloomLevel.REMEMBER,
            difficulty = 1,
            order = 2
        ),
        // Understand Level
        Activity(
            title = "Toplamayı Anlama",
            description = "Toplama işleminin anlamını öğrenin",
            topic = MathTopic.ADDITION,
            bloomLevel = BloomLevel.UNDERSTAND,
            difficulty = 2,
            order = 3
        ),
        Activity(
            title = "Çıkarmayı Anlama",
            description = "Çıkarma işleminin anlamını öğrenin",
            topic = MathTopic.SUBTRACTION,
            bloomLevel = BloomLevel.UNDERSTAND,
            difficulty = 2,
            order = 4
        ),
        // Apply Level
        Activity(
            title = "Toplama Problemlerini Çöz",
            description = "Gerçek hayat toplama problemleri",
            topic = MathTopic.ADDITION,
            bloomLevel = BloomLevel.APPLY,
            difficulty = 3,
            order = 5
        ),
        // Analyze Level
        Activity(
            title = "Çift ve Tek Sayıları Ayırt Et",
            description = "Çift ve tek sayıları karşılaştırın",
            topic = MathTopic.EVEN_ODD,
            bloomLevel = BloomLevel.ANALYZE,
            difficulty = 2,
            order = 6
        ),
        // Evaluate Level
        Activity(
            title = "Cevaplarını Açıkla",
            description = "Çözümleri açıkla ve savun",
            topic = MathTopic.ADDITION,
            bloomLevel = BloomLevel.EVALUATE,
            difficulty = 3,
            order = 7
        ),
        // Create Level
        Activity(
            title = "Kendi Problemini Oluştur",
            description = "Kendi math problemlerini yarat",
            topic = MathTopic.ADDITION,
            bloomLevel = BloomLevel.CREATE,
            difficulty = 4,
            order = 8
        )
    )

    private fun getSampleQuestions(): List<Question> = listOf(
        // Sayıları Tanı (Activity 1)
        Question(
            activityId = 1,
            question = "Kaç tane yıldız vardır?",
            correctAnswer = "5",
            option1 = "3",
            option2 = "5",
            option3 = "7",
            explanation = "Yıldızları saydığımızda 5 tane var",
            order = 1
        ),
        Question(
            activityId = 1,
            question = "Bu sayı kaçtır? 🌟🌟🌟🌟🌟🌟🌟🌟",
            correctAnswer = "8",
            option1 = "6",
            option2 = "8",
            option3 = "10",
            explanation = "Toplam 8 yıldız var",
            order = 2
        ),
        // Basit Toplama (Activity 2)
        Question(
            activityId = 2,
            question = "2 + 1 = ?",
            correctAnswer = "3",
            option1 = "2",
            option2 = "3",
            option3 = "4",
            explanation = "2 artı 1 eşittir 3",
            order = 1
        ),
        Question(
            activityId = 2,
            question = "3 + 2 = ?",
            correctAnswer = "5",
            option1 = "4",
            option2 = "5",
            option3 = "6",
            explanation = "3 artı 2 eşittir 5",
            order = 2
        ),
        // Toplamayı Anlama (Activity 3)
        Question(
            activityId = 3,
            question = "Toplama işleminin amacı nedir?",
            correctAnswer = "İki ya da daha fazla sayıyı birleştirmek",
            option1 = "Sayıları küçültmek",
            option2 = "İki ya da daha fazla sayıyı birleştirmek",
            option3 = "Sayıları bölmek",
            explanation = "Toplama, sayıları birleştirme işlemidir",
            order = 1
        ),
        // Çıkarmayı Anlama (Activity 4)
        Question(
            activityId = 4,
            question = "Çıkarma işleminin amacı nedir?",
            correctAnswer = "Bir sayıdan diğerini çıkarmak",
            option1 = "Sayıları toplamak",
            option2 = "Bir sayıdan diğerini çıkarmak",
            option3 = "Sayıları bölmek",
            explanation = "Çıkarma, bir sayıdan diğerini alma işlemidir",
            order = 1
        ),
        // Toplama Problemleri (Activity 5)
        Question(
            activityId = 5,
            question = "Ahmet'in 3 elması var. Ayşe ona 2 elma daha verdi. Toplam kaç elması var?",
            correctAnswer = "5",
            option1 = "3",
            option2 = "4",
            option3 = "5",
            explanation = "3 + 2 = 5 elma",
            order = 1
        ),
        // Çift ve Tek (Activity 6)
        Question(
            activityId = 6,
            question = "4 sayısı çift mi yoksa tek mi?",
            correctAnswer = "Çift",
            option1 = "Tek",
            option2 = "Çift",
            option3 = "İkisi de değil",
            explanation = "4 sayısı 2'ye tam bölünebildiği için çifttir",
            order = 1
        ),
        // Cevapları Açıkla (Activity 7)
        Question(
            activityId = 7,
            question = "2 + 3 = 5 olduğunu nasıl açıklardın?",
            correctAnswer = "2 nesne artı 3 nesne toplam 5 nesne yapar",
            option1 = "5 olur çünkü öyle",
            option2 = "2 nesne artı 3 nesne toplam 5 nesne yapar",
            option3 = "Sadece 5",
            explanation = "Çözümü açıklamak önemlidir",
            order = 1
        ),
        // Kendi Problemi Oluştur (Activity 8)
        Question(
            activityId = 8,
            question = "Kendi sayı hikayeni oluştur: ____ + ____ = ____",
            correctAnswer = "Herhangi bir doğru cevap",
            option1 = "1 + 1 = 2",
            option2 = "2 + 3 = 5",
            option3 = "3 + 2 = 5",
            explanation = "Yaratıcılık önemlidir! Kendi problemini yarat",
            order = 1
        )
    )

    private fun getSampleBadges(): List<Badge> = listOf(
        Badge(
            name = "İlk Adım",
            description = "İlk soruyu cevapla",
            iconEmoji = "🎯",
            requirement = "1 soru cevapla"
        ),
        Badge(
            name = "Hızlı Zekâ",
            description = "10 soruyu doğru cevapla",
            iconEmoji = "⚡",
            requirement = "10 doğru cevap"
        ),
        Badge(
            name = "Math Şampiyonu",
            description = "100 puan topla",
            iconEmoji = "🏆",
            requirement = "100 puan"
        ),
        Badge(
            name = "Kesif",
            description = "Tüm konuları keşfet",
            iconEmoji = "🔍",
            requirement = "Tüm aktiviteleri başla"
        ),
        Badge(
            name = "İçgüdü Ustası",
            description = "%90 doğruluk oranı",
            iconEmoji = "🧠",
            requirement = "90% doğruluk"
        )
    )
}
