package tr.mathapp.data

import android.content.Context
import tr.mathapp.model.BloomLevel
import tr.mathapp.model.Topic

data class Progress(
    val name: String = "",
    val stars: Map<Pair<Topic, BloomLevel>, Int> = emptyMap(),
    val levelCorrect: Map<BloomLevel, Int> = emptyMap(),
    val levelAnswered: Map<BloomLevel, Int> = emptyMap(),
    val points: Int = 0,
    val quizzes: Int = 0,
    val streak: Int = 0,
    val lastPlayDay: Long = 0,
    val badges: Set<Badge> = emptySet(),
) {
    fun stars(topic: Topic, level: BloomLevel) = stars[topic to level] ?: 0
    fun topicStars(topic: Topic) = BloomLevel.entries.sumOf { stars(topic, it) }
    val totalStars get() = stars.values.sum()
    val totalCorrect get() = levelCorrect.values.sum()
    val totalAnswered get() = levelAnswered.values.sum()

    fun isUnlocked(topic: Topic, level: BloomLevel) =
        level.ordinal == 0 || stars(topic, BloomLevel.entries[level.ordinal - 1]) > 0

    companion object {
        const val MAX_STARS_PER_LEVEL = 3
    }
}

enum class Badge(val emoji: String, val title: String, val description: String, val earned: (Progress) -> Boolean) {
    ILK_ADIM("🎯", "İlk Adım", "İlk etkinliğini tamamla", { it.quizzes >= 1 }),
    YUZ_PUAN("💯", "100 Puan", "Toplam 100 puan topla", { it.points >= 100 }),
    YILDIZ_AVCISI("🌟", "Yıldız Avcısı", "Toplam 30 yıldız topla", { it.totalStars >= 30 }),
    KASIF("🧭", "Kâşif", "10 konunun hepsinde en az 1 yıldız kazan", { p -> Topic.entries.all { p.topicStars(it) > 0 } }),
    YARATICI("🎨", "Yaratıcı", "Herhangi bir konuda “Oluştur” basamağını geç",
        { p -> Topic.entries.any { p.stars(it, BloomLevel.OLUSTUR) > 0 } }),
    ZIRVE("🏔️", "Zirve", "Bir konunun 6 basamağının hepsini geç",
        { p -> Topic.entries.any { t -> BloomLevel.entries.all { p.stars(t, it) > 0 } } }),
    BILGE("🧠", "Matematik Bilgesi", "50 soruyu doğru cevapla", { it.totalCorrect >= 50 }),
    SAMPIYON("🏆", "Şampiyon", "Toplam 500 puan topla", { it.points >= 500 }),
    SERI("🔥", "Ateşli Seri", "3 gün üst üste çalış", { it.streak >= 3 }),
}

class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("progress", Context.MODE_PRIVATE)

    fun load(): Progress {
        val stars = buildMap {
            for (t in Topic.entries) for (l in BloomLevel.entries) {
                val v = prefs.getInt("stars_${t.name}_${l.name}", 0)
                if (v > 0) put(t to l, v)
            }
        }
        return Progress(
            name = prefs.getString("name", "") ?: "",
            stars = stars,
            levelCorrect = BloomLevel.entries.associateWith { prefs.getInt("correct_${it.name}", 0) },
            levelAnswered = BloomLevel.entries.associateWith { prefs.getInt("answered_${it.name}", 0) },
            points = prefs.getInt("points", 0),
            quizzes = prefs.getInt("quizzes", 0),
            streak = prefs.getInt("streak", 0),
            lastPlayDay = prefs.getLong("lastPlayDay", 0),
            badges = prefs.getStringSet("badges", emptySet()).orEmpty()
                .mapNotNull { id -> Badge.entries.firstOrNull { it.name == id } }.toSet(),
        )
    }

    fun save(p: Progress) {
        prefs.edit().apply {
            clear()
            putString("name", p.name)
            p.stars.forEach { (k, v) -> putInt("stars_${k.first.name}_${k.second.name}", v) }
            p.levelCorrect.forEach { (k, v) -> putInt("correct_${k.name}", v) }
            p.levelAnswered.forEach { (k, v) -> putInt("answered_${k.name}", v) }
            putInt("points", p.points)
            putInt("quizzes", p.quizzes)
            putInt("streak", p.streak)
            putLong("lastPlayDay", p.lastPlayDay)
            putStringSet("badges", p.badges.map { it.name }.toSet())
        }.apply()
    }
}
