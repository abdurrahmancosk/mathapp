package com.example.mathapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mathapp.data.model.*

@Database(
    entities = [
        Activity::class,
        Question::class,
        UserProgress::class,
        UserStats::class,
        Badge::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun questionDao(): QuestionDao
    abstract fun progressDao(): ProgressDao
    abstract fun statsDao(): StatsDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "math_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

import androidx.room.TypeConverter
import com.example.mathapp.data.model.BloomLevel
import com.example.mathapp.data.model.MathTopic

class Converters {
    @TypeConverter
    fun fromBloomLevel(value: BloomLevel) = value.name

    @TypeConverter
    fun toBloomLevel(value: String) = BloomLevel.valueOf(value)

    @TypeConverter
    fun fromMathTopic(value: MathTopic) = value.name

    @TypeConverter
    fun toMathTopic(value: String) = MathTopic.valueOf(value)
}
