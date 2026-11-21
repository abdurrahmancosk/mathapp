package com.example.mathapp.data.db

import androidx.room.*
import com.example.mathapp.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Insert
    suspend fun insert(activity: Activity)

    @Update
    suspend fun update(activity: Activity)

    @Delete
    suspend fun delete(activity: Activity)

    @Query("SELECT * FROM activities")
    fun getAllActivities(): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getActivityById(id: Int): Activity?

    @Query("SELECT * FROM activities WHERE topic = :topic")
    fun getActivitiesByTopic(topic: String): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE bloomLevel = :level ORDER BY `order`")
    fun getActivitiesByBloomLevel(level: String): Flow<List<Activity>>

    @Query("SELECT DISTINCT topic FROM activities")
    fun getAllTopics(): Flow<List<String>>
}

@Dao
interface QuestionDao {
    @Insert
    suspend fun insert(question: Question)

    @Update
    suspend fun update(question: Question)

    @Delete
    suspend fun delete(question: Question)

    @Query("SELECT * FROM questions WHERE activityId = :activityId ORDER BY `order`")
    suspend fun getQuestionsByActivity(activityId: Int): List<Question>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Int): Question?

    @Query("SELECT COUNT(*) FROM questions WHERE activityId = :activityId")
    suspend fun getQuestionCountByActivity(activityId: Int): Int
}

@Dao
interface ProgressDao {
    @Insert
    suspend fun insertProgress(progress: UserProgress)

    @Query("SELECT * FROM user_progress WHERE activityId = :activityId")
    suspend fun getProgressByActivity(activityId: Int): List<UserProgress>

    @Query("SELECT COUNT(*) FROM user_progress WHERE activityId = :activityId AND isCorrect = 1")
    suspend fun getCorrectAnswerCount(activityId: Int): Int

    @Query("SELECT COUNT(DISTINCT questionId) FROM user_progress WHERE activityId = :activityId AND isCorrect = 1")
    suspend fun getCompletedQuestionCount(activityId: Int): Int

    @Query("SELECT AVG(CASE WHEN isCorrect = 1 THEN 1.0 ELSE 0.0 END) * 100 FROM user_progress WHERE activityId = :activityId")
    suspend fun getAccuracyPercentage(activityId: Int): Float

    @Query("DELETE FROM user_progress")
    suspend fun deleteAllProgress()
}

@Dao
interface StatsDao {
    @Insert
    suspend fun insertStats(stats: UserStats)

    @Update
    suspend fun updateStats(stats: UserStats)

    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getStats(): Flow<UserStats?>

    @Query("UPDATE user_stats SET totalQuestionsAnswered = totalQuestionsAnswered + 1 WHERE id = 1")
    suspend fun incrementQuestionCount()

    @Query("UPDATE user_stats SET correctAnswers = correctAnswers + 1 WHERE id = 1")
    suspend fun incrementCorrectAnswers()

    @Query("UPDATE user_stats SET totalScore = totalScore + :points WHERE id = 1")
    suspend fun addScore(points: Int)
}

@Dao
interface BadgeDao {
    @Insert
    suspend fun insertBadge(badge: Badge)

    @Update
    suspend fun updateBadge(badge: Badge)

    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<Badge>>

    @Query("SELECT * FROM badges WHERE isUnlocked = 1")
    fun getUnlockedBadges(): Flow<List<Badge>>

    @Query("SELECT COUNT(*) FROM badges WHERE isUnlocked = 1")
    suspend fun getUnlockedBadgeCount(): Int
}
