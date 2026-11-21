package tr.mathapp.model

import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import tr.mathapp.model.BloomLevel.ANLA
import tr.mathapp.model.BloomLevel.COZUMLE
import tr.mathapp.model.BloomLevel.DEGERLENDIR
import tr.mathapp.model.BloomLevel.HATIRLA
import tr.mathapp.model.BloomLevel.OLUSTUR
import tr.mathapp.model.BloomLevel.UYGULA

const val YES = "Evet, haklı"
const val NO = "Hayır, haklı değil"

/**
 * Question content follows the TYMM İlkokul Matematik Dersi Öğretim Programı, 2. sınıf
 * (MAT.2.x.x). Number limits come from the programme: numbers up to 100, multiplication of
 * numbers up to 10 by 1–5, division of numbers up to 20 by 2–5 without remainder.
 */
class QuestionGenerator(private val random: Random = Random.Default) {

    fun quiz(topic: Topic, level: BloomLevel, count: Int = 5): List<Question> {
        val result = mutableListOf<Question>()
        var attempts = 0
        while (result.size < count && attempts < 300) {
            attempts++
            val q = generate(topic, level)
            if (result.none { it.signature() == q.signature() }) result += q
        }
        while (result.size < count) result += generate(topic, level)
        return result
    }

    fun generate(topic: Topic, level: BloomLevel): Question = when (topic) {
        Topic.SAYILAR -> sayilar(level)
        Topic.TOPLAMA -> toplama(level)
        Topic.CIKARMA -> cikarma(level)
        Topic.CARPMA -> carpma(level)
        Topic.BOLME -> bolme(level)
        Topic.KESIRLER -> kesirler(level)
        Topic.GEOMETRI -> geometri(level)
        Topic.OLCME -> olcme(level)
        Topic.VERI -> veri(level)
        Topic.ORUNTU -> oruntu(level)
    }

    // region helpers

    private fun r(range: IntRange) = range.random(random)
    private fun chance() = random.nextBoolean()
    private fun <T> List<T>.one(): T = this[random.nextInt(size)]
    private fun said(claim: String) = "${NAMES.one()}: “$claim” dedi. Haklı mı?"

    private fun num(
        prompt: String,
        answer: Int,
        explanation: String,
        visual: String? = null,
        unit: String = "",
        near: List<Int> = emptyList(),
    ): ChoiceQuestion {
        val set = linkedSetOf(answer)
        near.filter { it in 0..100 && it != answer }.shuffled(random).forEach { if (set.size < 4) set += it }
        var spread = 2
        while (set.size < 4) {
            val c = answer + (if (chance()) 1 else -1) * r(1..spread)
            if (c in 0..100) set += c
            spread++
        }
        return ChoiceQuestion(prompt, set.map { "$it$unit" }.shuffled(random), "$answer$unit", explanation, visual)
    }

    private fun text(
        prompt: String,
        answer: String,
        wrong: List<String>,
        explanation: String,
        visual: String? = null,
    ): ChoiceQuestion {
        val options = (wrong.distinct().filter { it != answer }.shuffled(random).take(3) + answer).shuffled(random)
        return ChoiceQuestion(prompt, options, answer, explanation, visual)
    }

    private fun claim(prompt: String, truth: Boolean, explanation: String, visual: String? = null) =
        ChoiceQuestion(prompt, listOf(YES, NO), if (truth) YES else NO, explanation, visual)

    private fun build(
        prompt: String,
        labelA: String,
        labelB: String,
        rangeA: IntRange,
        rangeB: IntRange,
        explanation: String,
        visual: String? = null,
        render: (Int, Int) -> String,
        check: (Int, Int) -> Boolean,
    ) = BuildQuestion(prompt, labelA, labelB, rangeA, rangeB, render, check, explanation, visual)

    /** Two-digit number whose ones digit is not 5, so rounding to the nearest ten is unambiguous. */
    private fun roundable(range: IntRange): Int {
        var n = r(range)
        while (n % 10 == 5 || n % 10 == 0) n = r(range)
        return n
    }

    /** A plausible wrong result that stays within 100. */
    private fun other(correct: Int) = if (correct + 10 <= 100) correct + 10 else correct - 1

    private fun tens(answer: Int) = listOf(answer - 20, answer - 10, answer + 10, answer + 20).filter { it in 10..100 }

    // endregion

    // MAT.2.1.1–2.1.4, 2.1.6
    private fun sayilar(level: BloomLevel): Question = when (level) {
        HATIRLA -> {
            val n = r(11..99)
            if (chance()) {
                num("“${oku(n).cap()}” sayısı hangisidir?", n, "${oku(n).cap()} = $n",
                    near = listOf(ters(n), n + 10, n - 10, n + 1))
            } else {
                text("$n sayısı nasıl okunur?", oku(n).cap(),
                    listOf(ters(n), n + 10, n + 1, n - 1, n - 10).filter { it in 1..99 && it != n }.map { oku(it).cap() },
                    "$n → ${oku(n)}")
            }
        }
        ANLA -> {
            val n = r(21..98)
            val o = n / 10
            val b = n % 10
            when (r(0..2)) {
                0 -> num("$n sayısında kaç onluk vardır?", o, "$n = $o onluk + $b birlik", near = listOf(b, o + 1, o - 1))
                1 -> num("$o onluk ve $b birlik hangi sayıyı oluşturur?", n, "$o onluk = ${o * 10}. ${o * 10} + $b = $n",
                    visual = "🟦".repeat(o) + "  " + "🟡".repeat(b) + "\n🟦 = 10    🟡 = 1",
                    near = listOf(ters(n), o + b, n + 10))
                else -> num("$n sayısında onlar basamağındaki rakamın basamak değeri kaçtır?", o * 10,
                    "Onlar basamağındaki $o rakamı $o onluk demektir: basamak değeri ${o * 10}.",
                    near = listOf(o, b, n, b * 10))
            }
        }
        UYGULA -> if (r(0..2) > 0) {
            val (s, top) = listOf(2 to 20, 3 to 30, 4 to 40, 5 to 100, 10 to 100).one()
            val forward = chance()
            val seq = if (forward) {
                val start = if (s == 10) r(1..9) + 10 * r(0..4) else s * r(0..top / s - 4)
                List(4) { start + it * s }
            } else {
                val first = if (s == 10) r(4..9) * 10 + r(0..9) else s * r(4..top / s)
                List(4) { first - it * s }
            }
            val ans = if (forward) seq.last() + s else seq.last() - s
            num("${ritmik(s)} ${if (forward) "ileriye" else "geriye"} doğru ritmik say: ${seq.joinToString(", ")}, ?", ans,
                "Her adımda $s ${if (forward) "ekleriz" else "çıkarırız"}: ${seq.last()} ${if (forward) "+" else "−"} $s = $ans",
                near = listOf(seq.last() + 1, seq.last() - 1, if (forward) ans + s else ans - s))
        } else {
            val n = roundable(21..49)
            val rows = (0 until n).chunked(10).joinToString("\n") { row -> row.chunked(5).joinToString(" ") { "●".repeat(it.size) } }
            val ans = round10(n)
            num("Noktaların sayısını tahmin et. Her tam satırda 10 nokta var. Yaklaşık kaç nokta vardır?", ans,
                "${n / 10} tam satır ${n / 10 * 10} eder, ${n % 10} nokta daha var: $n. $n sayısına en yakın onluk $ans.",
                visual = rows, near = tens(ans))
        }
        COZUMLE -> when (r(0..3)) {
            0 -> {
                val a = r(1..9)
                var b = r(1..9)
                while (b == a) b = r(1..9)
                val nums = linkedSetOf(a * 10 + b, b * 10 + a)
                while (nums.size < 4) nums += r(10..99)
                val big = chance()
                val ans = if (big) nums.max() else nums.min()
                text("Hangisi en ${if (big) "büyük" else "küçük"} sayıdır?", "$ans", nums.filter { it != ans }.map { "$it" },
                    "Önce onlar basamaklarını karşılaştırırız; eşitse birler basamağına bakarız. Cevap: $ans")
            }
            1 -> {
                val nums = mutableSetOf<Int>()
                while (nums.size < 4) nums += r(10..99)
                val sorted = nums.sorted()
                val right = sorted.joinToString(" < ")
                val wrong = List(6) { sorted.shuffled(random) }.filter { it != sorted }.map { it.joinToString(" < ") }
                text("Hangisinde sayılar küçükten büyüğe doğru sıralanmıştır?", right, wrong,
                    "Sayı doğrusunda soldaki sayı daha küçüktür: $right")
            }
            2 -> {
                val n = roundable(11..94)
                val lo = n / 10 * 10
                val hi = lo + 10
                val ans = round10(n)
                text("$n sayısı hangi onluğa daha yakındır?", "$ans", listOf(lo, hi, hi + 10, lo - 10).filter { it in 0..100 && it != ans }.map { "$it" },
                    "$n, $lo ile $hi arasındadır. Birler basamağı ${n % 10} olduğu için en yakın onluk $ans.")
            }
            else -> {
                val lo = r(1..8) * 10
                val ans = lo + r(1..9)
                text("Sayı doğrusunda $lo ile ${lo + 10} arasında hangi sayı vardır?", "$ans",
                    listOf("${lo - r(1..9)}", "${lo + 10 + r(1..9)}", "${ans + 20}", "${lo - 10 + r(1..9)}").filter { it.toInt() in 0..100 },
                    "$ans sayısının onlar basamağı ${lo / 10}; bu yüzden $lo ile ${lo + 10} arasındadır.")
            }
        }
        DEGERLENDIR -> when (r(0..2)) {
            0 -> {
                val a = r(10..99)
                var b = r(10..99)
                while (b == a) b = r(10..99)
                val why = if (a / 10 != b / 10) {
                    "Onlar basamaklarına bakarız: ${a / 10} ve ${b / 10}."
                } else {
                    "Onlar basamakları eşit, birler basamaklarına bakarız: ${a % 10} ve ${b % 10}."
                }
                claim(said("$a, $b sayısından büyüktür."), a > b, "$why Bu yüzden ${max(a, b)} > ${min(a, b)}.")
            }
            1 -> {
                val real = r(20..50)
                val guess = (real + r(-12..12)).coerceIn(10, 50)
                val ans = when {
                    guess < real -> "Tahmini sayma sonucundan az"
                    guess > real -> "Tahmini sayma sonucundan fazla"
                    else -> "Tahmini sayma sonucuna eşit"
                }
                text("${NAMES.one()} kavanozdaki bilyelerin sayısını $guess olarak tahmin etti. Saydığında $real bilye çıktı. Tahmini için ne söylenebilir?",
                    ans, listOf("Tahmini sayma sonucundan az", "Tahmini sayma sonucundan fazla", "Tahmini sayma sonucuna eşit"),
                    "Tahmin $guess, sayma sonucu $real. Aradaki fark ${abs(real - guess)}.")
            }
            else -> {
                val n = roundable(11..94)
                val honest = chance()
                val claimed = if (honest) round10(n) else if (round10(n) == n / 10 * 10) n / 10 * 10 + 10 else n / 10 * 10
                claim(said("$n sayısı en yakın onluğa yuvarlanınca $claimed olur."), honest,
                    "$n sayısının birler basamağı ${n % 10}. En yakın onluk ${round10(n)}.")
            }
        }
        OLUSTUR -> {
            val lo = r(2..6) * 10
            val t = r(3..8) * 10
            val (prompt, rule) = listOf<Pair<String, (Int) -> Boolean>>(
                "$lo ile ${lo + 20} arasında bir sayı oluştur." to { n -> n > lo && n < lo + 20 },
                "Rakamlarının toplamı 9 olan bir sayı oluştur." to { n -> n / 10 + n % 10 == 9 },
                "Onlar basamağı, birler basamağından büyük olan bir sayı oluştur." to { n -> n / 10 > n % 10 },
                "En yakın onluğa yuvarlanınca $t olan bir sayı oluştur." to { n -> n != t && round10(n) == t },
            ).one()
            build(prompt, "Onluk", "Birlik", 1..9, 0..9,
                "Onlukları ve birlikleri değiştirerek koşula uyan sayıyı bulabilirsin. Birden fazla doğru cevap olabilir!",
                render = { a, b -> "$a onluk + $b birlik = ${a * 10 + b}" },
                check = { a, b -> rule(a * 10 + b) })
        }
    }

    // MAT.2.2.1–2.2.3, 2.2.6
    private fun toplama(level: BloomLevel): Question = when (level) {
        HATIRLA -> if (r(0..3) == 0) {
            val a = r(11..49)
            val b = r(11..49)
            val askTotal = chance()
            text("$a + $b = ${a + b} işleminde ${if (askTotal) a + b else a} sayısına ne ad verilir?",
                if (askTotal) "Toplam" else "Toplanan", listOf("Toplam", "Toplanan", "Fark", "Eksilen"),
                "Toplama işleminde toplanan sayılara “toplanan”, sonuca “toplam” denir.")
        } else {
            val a = r(2..9)
            val b = r(2..9)
            num("$a + $b = ?", a + b, "$a + $b = ${a + b}", near = listOf(a + b + 1, a + b - 1, a + b + 10))
        }
        ANLA -> if (chance()) {
            val a = r(2..6)
            val b = r(1..5)
            val e = OBJECTS.one()
            text("Resimdeki durumu hangi işlem gösterir?", "$a + $b = ${a + b}",
                listOf(
                    if (a >= b) "$a − $b = ${a - b}" else "$b − $a = ${b - a}",
                    "$a + $b = ${a + b + 1}",
                    "$a + ${b + 1} = ${a + b + 1}",
                ),
                "$a nesneye $b nesne eklenince ${a + b} nesne olur.",
                visual = e.repeat(a) + "\n➕\n" + e.repeat(b))
        } else {
            val a = r(12..48)
            val b = r(11..40)
            num("$a + $b = ${a + b} ise $b + $a = ?", a + b,
                "Toplananların yeri değişirse toplam değişmez: $b + $a = ${a + b}",
                near = listOf(a + b + 1, a - b + 50, a + b - 10))
        }
        UYGULA -> when (r(0..3)) {
            0 -> {
                val a = r(12..58)
                val b = r(11..40)
                val prompt = listOf(
                    "Kütüphanede $a masal kitabı ve $b bilim kitabı var. Toplam kaç kitap vardır?",
                    "Ayşe $a ceviz, kardeşi $b ceviz topladı. İkisi toplam kaç ceviz topladı?",
                    "Bir çiftlikte $a tavuk ve $b ördek var. Çiftlikte toplam kaç hayvan vardır?",
                    "Okul bahçesine sabah $a fidan, öğleden sonra $b fidan dikildi. Toplam kaç fidan dikildi?",
                ).one()
                num(prompt, a + b, "$a + $b = ${a + b}", near = listOf(a + b + 10, a + b - 10, a + b + 1))
            }
            1 -> {
                val a = r(10..40)
                val b = r(10..30)
                val c = r(5..20)
                num("Bir okulun 2. sınıflarında $a, $b ve $c öğrenci var. Toplam kaç öğrenci vardır?", a + b + c,
                    "Önce iki sayıyı, sonra üçüncüyü toplarız: $a + $b = ${a + b}, ${a + b} + $c = ${a + b + c}",
                    near = listOf(a + b, a + b + c + 10, a + b + c - 10))
            }
            2 -> if (chance()) {
                val d = r(2..8)
                val k = r(1..9)
                num("Bir deste kalem 10 kalemdir. $d deste ve $k kalem toplam kaç kalemdir?", d * 10 + k,
                    "$d deste = ${d * 10} kalem. ${d * 10} + $k = ${d * 10 + k}", near = listOf(d + k, d * 10, k * 10 + d))
            } else {
                val k = r(2..20)
                num("Bir düzine yumurta 12 yumurtadır. 1 düzine ve $k yumurta toplam kaç yumurtadır?", 12 + k,
                    "1 düzine = 12. 12 + $k = ${12 + k}", near = listOf(10 + k, 12 + k + 10, 1 + k))
            }
            else -> {
                val a = r(20..50)
                val b = r(10..30)
                val c = r(5..a)
                num("Ali'nin $a bilyesi vardı. $b bilye daha aldı, sonra $c bilyesini kardeşine verdi. Ali'nin kaç bilyesi oldu?",
                    a + b - c, "Önce toplarız: $a + $b = ${a + b}. Sonra çıkarırız: ${a + b} − $c = ${a + b - c}",
                    near = listOf(a + b, a + b - c + 10, a - b + c))
            }
        }
        COZUMLE -> when (r(0..2)) {
            0 -> {
                val tot = r(30..90)
                val a = r(10..tot - 10)
                num("$a + ? = $tot", tot - a,
                    "Eksik sayıyı bulmak için çıkarma yaparız: $tot − $a = ${tot - a}. Kontrol: $a + ${tot - a} = $tot",
                    near = listOf(tot - a + 10, tot - a - 10, tot - a + 1))
            }
            1 -> {
                val a = roundable(11..49)
                val b = roundable(11..49)
                val ans = round10(a) + round10(b)
                num("$a + $b işleminin sonucunu tahmin et. Sonuç yaklaşık kaçtır?", ans,
                    "Sayıları en yakın onluğa yuvarlarız: $a → ${round10(a)}, $b → ${round10(b)}. ${round10(a)} + ${round10(b)} = $ans. (Gerçek sonuç ${a + b})",
                    near = tens(ans))
            }
            else -> {
                val a = r(5..20)
                val b = r(2..15)
                val c = r(1..a + b - 1)
                num("$a + $b = $c + ?", a + b - c,
                    "Eşittir işaretinin iki tarafı aynı değerde olmalı: $a + $b = ${a + b}. ${a + b} − $c = ${a + b - c}",
                    near = listOf(a + b, b, a + b - c + 1))
            }
        }
        DEGERLENDIR -> when (r(0..2)) {
            0 -> {
                val ao = r(5..9)
                val bo = r(10 - ao..9)
                val a = r(1..5) * 10 + ao
                val b = r(1..3) * 10 + bo
                val correct = a + b
                val forgot = correct - 10
                val honest = chance()
                val claimed = if (honest) correct else forgot
                val answer = if (honest) "Doğru" else "Yanlış, sonuç $correct olmalı"
                val wrong = if (honest) {
                    listOf("Yanlış, sonuç $forgot olmalı", "Yanlış, sonuç ${other(correct)} olmalı")
                } else {
                    listOf("Doğru", "Yanlış, sonuç ${correct - 1} olmalı")
                }
                text("${NAMES.one()}, $a + $b işleminin sonucunu $claimed buldu. Bu sonuç doğru mu?", answer, wrong,
                    "Birler: $ao + $bo = ${ao + bo}. ${ao + bo - 10} yazılır, 1 onluk elde onlar basamağına eklenir. $a + $b = $correct")
            }
            1 -> {
                val a = roundable(11..49)
                val b = roundable(11..49)
                val est = round10(a) + round10(b)
                val honest = chance()
                val wrongEst = if (est + 20 <= 100) est + 20 else est - 20
                claim(said("$a + $b işleminin sonucu yaklaşık ${if (honest) est else wrongEst} olur."), honest,
                    "$a → ${round10(a)}, $b → ${round10(b)}. Tahmini sonuç $est, gerçek sonuç ${a + b}.")
            }
            else -> {
                val a = r(5..20)
                val b = r(2..15)
                val c = r(1..a + b - 1)
                val d = a + b - c
                val honest = chance()
                claim(said("$a + $b = $c + ${if (honest) d else d + r(1..2)}"), honest,
                    "Sol taraf: $a + $b = ${a + b}. Sağ tarafın da ${a + b} olması için $c + $d gerekir.")
            }
        }
        OLUSTUR -> if (chance()) {
            val t = r(15..60)
            build("Toplamı $t olan bir toplama işlemi oluştur.", "1. sayı", "2. sayı", 0..t, 0..t,
                "Toplamı $t olan pek çok işlem vardır. Sayılardan biri büyürken diğeri küçülmeli!",
                render = { a, b -> "$a + $b = ${a + b}" },
                check = { a, b -> a + b == t && a > 0 && b > 0 })
        } else {
            val a = r(5..20)
            val b = r(5..20)
            build("Eşitliği tamamla: $a + $b = ? + ?  (Aynı sayıları kullanma!)", "1. sayı", "2. sayı", 0..40, 0..40,
                "Sağ tarafın toplamı da ${a + b} olmalı.",
                render = { x, y -> "$a + $b = $x + $y" },
                check = { x, y -> x + y == a + b && !(x == a && y == b) && !(x == b && y == a) })
        }
    }

    // MAT.2.2.1–2.2.3
    private fun cikarma(level: BloomLevel): Question = when (level) {
        HATIRLA -> if (r(0..3) == 0) {
            val a = r(40..99)
            val b = r(11..a - 10)
            val (n, name) = listOf(a to "Eksilen", b to "Çıkan", a - b to "Fark").one()
            text("$a − $b = ${a - b} işleminde $n sayısına ne ad verilir?", name, listOf("Eksilen", "Çıkan", "Fark", "Toplam"),
                "Çıkarma işleminde: eksilen − çıkan = fark.")
        } else {
            val a = r(10..18)
            val b = r(2..9)
            num("$a − $b = ?", a - b, "$a − $b = ${a - b}", near = listOf(a - b + 1, a - b - 1, a + b))
        }
        ANLA -> if (chance()) {
            val total = r(5..9)
            val gone = r(1..total - 1)
            val (e, noun, verb) = listOf(
                Triple("🎈", "balon", "patladı"),
                Triple("🐦", "kuş", "uçup gitti"),
                Triple("🍪", "kurabiye", "yendi"),
                Triple("🐟", "balık", "yüzüp gitti"),
            ).one()
            text("$total $noun vardı. $gone tanesi $verb. Hangi işlem bu durumu gösterir?",
                "$total − $gone = ${total - gone}",
                listOf("$total + $gone = ${total + gone}", "$total − $gone = ${total - gone + 1}", "$gone + ${total - gone} = ${total + 1}"),
                "Azalan bir durumu çıkarma ile gösteririz: $total − $gone = ${total - gone}",
                visual = e.repeat(total - gone) + " " + "❌".repeat(gone))
        } else {
            val x = r(12..50)
            val y = r(11..40)
            num("$x + $y = ${x + y} ise ${x + y} − $y = ?", x,
                "Toplama ve çıkarma birbirinin tersidir. Toplamdan bir toplananı çıkarırsak diğer toplananı buluruz: $x",
                near = listOf(y, x + y, x + 10))
        }
        UYGULA -> if (r(0..2) > 0) {
            val a = r(40..100)
            val b = r(11..a - 10)
            val prompt = listOf(
                "Otobüste $a yolcu vardı. Durakta $b yolcu indi. Otobüste kaç yolcu kaldı?",
                "Manavda $a karpuz vardı. $b tanesi satıldı. Kaç karpuz kaldı?",
                "Elif'in $a lirası vardı. $b liralık bir kitap aldı. Kaç lirası kaldı?",
                "Bir ağaçta $a elma vardı. $b tanesi toplandı. Ağaçta kaç elma kaldı?",
            ).one()
            num(prompt, a - b, "$a − $b = ${a - b}", near = listOf(a - b + 10, a - b - 10, a - b + 1))
        } else {
            val a = r(30..60)
            val b = r(5..a - 10)
            val c = r(5..30)
            num("Otobüste $a yolcu vardı. Durakta $b yolcu indi, $c yolcu bindi. Otobüste kaç yolcu oldu?", a - b + c,
                "Önce inenleri çıkarırız: $a − $b = ${a - b}. Sonra binenleri ekleriz: ${a - b} + $c = ${a - b + c}",
                near = listOf(a - b, a + b + c, a - b - c))
        }
        COZUMLE -> when (r(0..2)) {
            0 -> {
                val b = r(10..40)
                val d = r(10..50)
                num("? − $b = $d", b + d,
                    "Eksik sayıyı bulmak için toplama yaparız: $d + $b = ${b + d}. Kontrol: ${b + d} − $b = $d",
                    near = listOf(d - b, b + d + 10, b + d - 10))
            }
            1 -> {
                val a = r(30..99)
                val b = r(10..a - 10)
                num("$a − ? = ${a - b}", b,
                    "Çıkanı bulmak için eksilenden farkı çıkarırız: $a − ${a - b} = $b",
                    near = listOf(a, b + 10, b - 1))
            }
            else -> {
                var a = roundable(41..94)
                var b = roundable(11..39)
                while (round10(a) <= round10(b)) { a = roundable(41..94); b = roundable(11..39) }
                val ans = round10(a) - round10(b)
                num("$a − $b işleminin sonucunu tahmin et. Sonuç yaklaşık kaçtır?", ans,
                    "Sayıları en yakın onluğa yuvarlarız: $a → ${round10(a)}, $b → ${round10(b)}. ${round10(a)} − ${round10(b)} = $ans. (Gerçek sonuç ${a - b})",
                    near = tens(ans))
            }
        }
        DEGERLENDIR -> if (chance()) {
            val ao = r(0..4)
            val at = r(3..9)
            val a = at * 10 + ao
            val bo = r(5..9)
            val bt = r(1..at - 1)
            val b = bt * 10 + bo
            val correct = a - b
            val mistake = (at - bt) * 10 + (bo - ao)
            val honest = chance()
            val claimed = if (honest) correct else mistake
            val answer = if (honest) "Doğru" else "Yanlış, sonuç $correct olmalı"
            val wrong = if (honest) {
                listOf("Yanlış, sonuç $mistake olmalı", "Yanlış, sonuç ${other(correct)} olmalı")
            } else {
                listOf("Doğru", "Yanlış, sonuç ${other(correct)} olmalı")
            }
            text("${NAMES.one()}, $a − $b işleminin sonucunu $claimed buldu. Bu sonuç doğru mu?", answer, wrong,
                "Birler basamağında $ao, $bo sayısından küçük olduğu için onlar basamağından 1 onluk bozarız. $a − $b = $correct")
        } else {
            val a = r(30..99)
            val b = r(10..a - 10)
            val d = a - b
            if (chance()) {
                claim(said("$a − $b = $d işleminin doğruluğunu $d + $b = $a toplamasıyla kontrol edebilirim."), true,
                    "Fark ile çıkanı toplarsak eksileni buluruz. Toplama, çıkarmanın tersidir.")
            } else {
                claim(said("$a − $b = $d işleminin doğruluğunu $a + $b toplamasıyla kontrol edebilirim."), false,
                    "Kontrol için fark ile çıkan toplanır: $d + $b = $a. Eksilen ile çıkanı toplamak işe yaramaz.")
            }
        }
        OLUSTUR -> {
            val d = r(5..40)
            build("Farkı $d olan bir çıkarma işlemi oluştur.", "Eksilen", "Çıkan", 0..100, 0..100,
                "İki sayı arasındaki fark $d olmalı. İpucu: Çıkana $d eklersen eksileni bulursun.",
                render = { a, b -> "$a − $b = ${a - b}" },
                check = { a, b -> a - b == d && b > 0 })
        }
    }

    // MAT.2.2.4–2.2.6: numbers up to 10 multiplied by 1–5
    private fun carpma(level: BloomLevel): Question = when (level) {
        HATIRLA -> if (r(0..3) == 0) {
            val a = r(2..5)
            val b = r(2..10)
            val askProduct = chance()
            text("$a × $b = ${a * b} işleminde ${if (askProduct) a * b else b} sayısına ne ad verilir?",
                if (askProduct) "Çarpım" else "Çarpan", listOf("Çarpım", "Çarpan", "Toplam", "Bölüm"),
                "Çarpılan sayılara “çarpan”, sonuca “çarpım” denir.")
        } else {
            val a = r(1..5)
            val b = r(0..10)
            val why = when {
                b == 0 -> "$a tane 0 toplanırsa sonuç 0 olur."
                a == 1 -> "1 tane $b, $b demektir."
                else -> "$a × $b = ${a * b} ($a tane $b)"
            }
            num("$a × $b = ?", a * b, why, near = listOf(a * b + a, a * b - b, a + b, b))
        }
        ANLA -> if (chance()) {
            val g = r(2..5)
            val n = r(2..5)
            text("${List(g) { n }.joinToString(" + ")} işlemi hangi çarpma işlemiyle gösterilir?", "$g × $n",
                listOf("$g + $n", "${g + 1} × $n", "$g × ${n + 1}"),
                "$g tane $n var. Bu, $g × $n = ${g * n} demektir.")
        } else {
            val s = r(2..5)
            val k = r(3..5)
            text("${List(k) { (it + 1) * s }.joinToString(", ")} diye ${ritmik(s)} saymak hangi çarpma işlemini gösterir?",
                "$k × $s = ${k * s}", listOf("$k + $s = ${k + s}", "${k + 1} × $s = ${(k + 1) * s}", "$k × ${s + 1} = ${k * (s + 1)}"),
                "$k kez $s saydık: $k × $s = ${k * s}")
        }
        UYGULA -> {
            val g = r(2..5)
            val n = r(2..10)
            val prompt = listOf(
                "Her kutuda $n kalem var. $g kutuda toplam kaç kalem vardır?",
                "Her tabakta $n kurabiye var. $g tabakta toplam kaç kurabiye vardır?",
                "Her vazoda $n çiçek var. $g vazoda toplam kaç çiçek vardır?",
                "Her sepette $n elma var. $g sepette toplam kaç elma vardır?",
            ).one()
            num(prompt, g * n, "$g tane $n: $g × $n = ${g * n}", near = listOf(g + n, g * n + n, g * n - g))
        }
        COZUMLE -> {
            val g = r(2..5)
            val n = r(2..5)
            val e = OBJECTS.one()
            val visual = List(g) { e.repeat(n) }.joinToString("\n")
            if (chance()) {
                text("Resimdeki nesneleri hangi çarpma işlemi gösterir?", "$g grup × $n = ${g * n}",
                    listOf("$n grup × $g = ${g * n}", "$g grup × ${n + 1} = ${g * (n + 1)}", "$g + $n = ${g + n}"),
                    "Resimde $g grup var ve her grupta $n nesne var: $g × $n = ${g * n}", visual)
            } else {
                num("? × $n = ${g * n}", g, "${g * n} nesneyi her grupta $n nesne olacak şekilde ayırırsak $g grup olur. $g × $n = ${g * n}",
                    near = listOf(n, g + 1, g * n - n))
            }
        }
        DEGERLENDIR -> {
            val a = r(2..5)
            var b = r(2..9)
            while (b == a) b = r(2..9)
            when (r(0..3)) {
                0 -> claim(said("$a × $b ile $b × $a aynı sonucu verir."), true,
                    "Çarpanların yeri değişse de çarpım değişmez: $a × $b = $b × $a = ${a * b}")
                1 -> claim(said("$a × $b = ${a + b}"), false,
                    "$a × $b, $a tane $b demektir: ${List(a) { b }.joinToString(" + ")} = ${a * b}. ${a + b} toplama sonucudur.")
                2 -> claim(said("$a × $b işlemi $a tane $b demektir."), true,
                    "Evet! $a × $b = ${List(a) { b }.joinToString(" + ")} = ${a * b}")
                else -> claim(said("$b × 0 = $b"), false, "Hiç (0) grup yoksa nesne de yoktur: $b × 0 = 0")
            }
        }
        OLUSTUR -> {
            val t = listOf(6, 8, 10, 12, 15, 16, 18, 20, 24, 30).one()
            build("Sonucu $t olan bir çarpma işlemi oluştur.", "Grup sayısı", "Gruptaki nesne", 1..5, 0..10,
                "$t nesneyi eşit gruplara ayırmayı düşün. Birden fazla doğru cevap olabilir!",
                render = { a, b -> "$a × $b = ${a * b}" },
                check = { a, b -> a * b == t })
        }
    }

    // MAT.2.2.4–2.2.6: numbers up to 20 divided by 2, 3, 4, 5 without remainder
    private fun bolme(level: BloomLevel): Question = when (level) {
        HATIRLA -> when (r(0..3)) {
            0 -> text("Bölme işleminin işareti hangisidir?", "÷", listOf("×", "+", "−"), "Bölme işareti ÷ şeklindedir.")
            1 -> {
                val g = r(2..5)
                val q = r(2..20 / g)
                val (n, name) = listOf(g * q to "Bölünen", g to "Bölen", q to "Bölüm").one()
                text("${g * q} ÷ $g = $q işleminde $n sayısına ne ad verilir?", name, listOf("Bölünen", "Bölen", "Bölüm", "Çarpım"),
                    "Bölme işleminde: bölünen ÷ bölen = bölüm.")
            }
            else -> {
                val g = r(2..5)
                val q = r(1..20 / g)
                num("${g * q} ÷ $g = ?", q, "${g * q} nesne $g eş gruba ayrılırsa her grupta $q nesne olur.",
                    near = listOf(q + 1, g, g * q - g))
            }
        }
        ANLA -> if (chance()) {
            val g = r(2..5)
            val q = r(2..20 / g)
            val total = g * q
            text("$total ÷ $g işlemi ne anlama gelir?", "$total nesneyi $g eş gruba ayırmak",
                listOf("$total nesneye $g nesne eklemek", "$total nesneden $g nesne çıkarmak", "$total nesneyi $g kez saymak"),
                "$total ÷ $g: $total nesne $g eş gruba ayrılır, her grupta $q nesne olur.")
        } else {
            val g = r(2..5)
            val q = r(2..20 / g)
            val total = g * q
            num("$total sayısından art arda $g çıkararak 0'a ulaşmak istiyoruz. Kaç kez $g çıkarmalıyız?", q,
                "${List(q + 1) { total - it * g }.joinToString(" → ")}. $q kez çıkardık. Bu, $total ÷ $g = $q demektir.",
                near = listOf(g, q + 1, q - 1))
        }
        UYGULA -> {
            val g = r(2..5)
            val q = r(2..20 / g)
            val (e, item, place) = listOf(
                Triple("🍪", "kurabiye", "tabağa"),
                Triple("🍎", "elma", "sepete"),
                Triple("⚽", "top", "kutuya"),
                Triple("🌸", "çiçek", "vazoya"),
            ).one()
            val total = g * q
            num("$total $item $g $place eşit olarak paylaştırılıyor. Her birine kaç $item düşer?", q,
                "$total ÷ $g = $q, çünkü $g × $q = $total", visual = e.repeat(total), near = listOf(q + 1, q - 1, g))
        }
        COZUMLE -> {
            val g = r(2..5)
            val q = r(2..20 / g)
            num("$g × $q = ${g * q} ise ${g * q} ÷ $g = ?", q,
                "Bölme, çarpmanın tersidir. $g × $q = ${g * q} olduğu için ${g * q} ÷ $g = $q",
                near = listOf(g, q + 1, g * q - g))
        }
        DEGERLENDIR -> {
            val n = r(6..20)
            val k = r(2..5)
            val fair = n % k == 0
            claim(said("$n şekeri $k arkadaşıma hiç artmadan eşit paylaştırabilirim."), fair,
                if (fair) "$n ÷ $k = ${n / k}. Her arkadaşa ${n / k} şeker düşer, hiç artmaz."
                else "Her arkadaşa ${n / k} şeker düşer ama ${n % k} şeker artar (kalan ${n % k}).")
        }
        OLUSTUR -> {
            val n = listOf(12, 15, 16, 18, 20).one()
            build("$n kalemi eşit gruplara ayır. Kaç grup olsun, her grupta kaç kalem olsun?",
                "Grup sayısı", "Gruptaki kalem", 2..5, 1..10,
                "Grup sayısı × gruptaki kalem = $n olmalı.",
                render = { a, b -> "$a grup × $b kalem = ${a * b} kalem" },
                check = { a, b -> a * b == n })
        }
    }

    // MAT.2.1.7 (no fraction notation)
    private fun kesirler(level: BloomLevel): Question = when (level) {
        HATIRLA -> {
            val (q, a, w) = listOf(
                Triple("Bir bütünün 2 eş parçasından her birine ne denir?", "Yarım", listOf("Çeyrek", "Bütün")),
                Triple("Bir bütünün 4 eş parçasından her birine ne denir?", "Çeyrek", listOf("Yarım", "Bütün")),
                Triple("Yarımın yarısına ne denir?", "Çeyrek", listOf("Yarım", "Bütün")),
                Triple("Kaç yarım bir bütün eder?", "2", listOf("1", "3", "4")),
                Triple("Kaç çeyrek bir bütün eder?", "4", listOf("2", "3", "5")),
                Triple("Kaç çeyrek bir yarım eder?", "2", listOf("1", "3", "4")),
            ).one()
            text(q, a, w, "Yarım: bütünün 2 eş parçasından biri. Çeyrek: bütünün 4 eş parçasından biri.")
        }
        ANLA -> {
            val c = listOf("🟩", "🟦", "🟥", "🟨", "🟪").one()
            val (vis, ans) = listOf(
                c + "⬜⬜⬜" to "Çeyrek",
                "⬜" + c + "⬜⬜" to "Çeyrek",
                c + c + "⬜⬜" to "Yarım",
                "⬜⬜" + c + c to "Yarım",
                c + "⬜" to "Yarım",
                c.repeat(4) to "Bütün",
                c + c to "Bütün",
            ).one()
            text("Şekil eş parçalara bölünmüştür. Boyalı kısım bütünün ne kadarıdır?", ans,
                listOf("Bütün", "Yarım", "Çeyrek"),
                when (ans) {
                    "Çeyrek" -> "4 eş parçadan 1'i boyalı: çeyrek."
                    "Yarım" -> "Parçaların yarısı boyalı: yarım."
                    else -> "Bütün parçalar boyalı: bütün."
                },
                visual = vis)
        }
        UYGULA -> if (chance()) {
            val n = r(2..10) * 2
            num("$n elmanın yarısı kaç elmadır?", n / 2, "Yarısını bulmak için 2 eş gruba ayırırız: $n ÷ 2 = ${n / 2}",
                near = listOf(n / 4, n - 2, n / 2 + 2))
        } else {
            val n = r(1..5) * 4
            num("$n bilyenin çeyreği kaç bilyedir?", n / 4, "Çeyreğini bulmak için 4 eş gruba ayırırız: $n ÷ 4 = ${n / 4}",
                near = listOf(n / 2, n / 4 + 1, n - 4))
        }
        COZUMLE -> {
            val (q, a, w) = listOf(
                Triple("Aynı büyüklükteki iki pizzadan birinin yarımı mı, diğerinin çeyreği mi daha büyüktür?",
                    "Yarım", listOf("Çeyrek", "İkisi eşit")),
                Triple("Bir yarım kaç çeyrek eder?", "2", listOf("1", "3", "4")),
                Triple("Bir yarım ile bir çeyrek toplam kaç çeyrek eder?", "3", listOf("2", "4", "5")),
                Triple("Bir pastanın yarısı yendi. Geriye ne kadar kaldı?", "Yarım", listOf("Çeyrek", "Bütün")),
                Triple("Bir pastanın çeyreği yendi. Geriye kaç çeyrek kaldı?", "3", listOf("1", "2", "4")),
            ).one()
            text(q, a, w, "1 bütün = 2 yarım = 4 çeyrek. 1 yarım = 2 çeyrek.")
        }
        DEGERLENDIR -> {
            val (c, truth, why) = listOf(
                Triple("Pastayı farklı büyüklükte 4 parçaya böldüm, her parça bir çeyrektir.", false,
                    "Çeyrek olması için 4 parçanın eş (aynı büyüklükte) olması gerekir."),
                Triple("Yarım, çeyrekten küçüktür.", false, "Yarım = 2 çeyrek. Bu yüzden yarım, çeyrekten büyüktür."),
                Triple("İki yarım bir bütün eder.", true, "Bir bütün 2 eş parçaya bölünür: 2 yarım = 1 bütün."),
                Triple("Dört çeyrek bir bütün eder.", true, "Bir bütün 4 eş parçaya bölünür: 4 çeyrek = 1 bütün."),
                Triple("Bir elmayı iki eş parçaya bölersem her parça yarım olur.", true, "2 eş parçadan her biri yarımdır."),
                Triple("Üç çeyrek, bir bütünden fazladır.", false, "Bir bütün 4 çeyrektir. 3 çeyrek bir bütünden azdır."),
                Triple("50 kuruş, 1 liranın yarısıdır.", true, "1 TL = 100 kuruş. İki tane 50 kuruş 1 TL eder."),
            ).one()
            claim(said(c), truth, why)
        }
        OLUSTUR -> if (chance()) {
            val (whole, quarters) = listOf("Bir bütün" to 4, "İki bütün" to 8, "Bir buçuk bütün" to 6).one()
            build("$whole oluştur! Kaç yarım ve kaç çeyrek kullanırsın?", "Yarım", "Çeyrek", 0..4, 0..8,
                "1 yarım = 2 çeyrek. $whole = $quarters çeyrek eder.",
                render = { a, b -> "$a yarım + $b çeyrek = ${a * 2 + b} çeyrek" },
                check = { a, b -> a * 2 + b == quarters })
        } else {
            val n = listOf(8, 12, 16, 20).one()
            val half = chance()
            val red = if (half) n / 2 else n / 4
            build("$n boncuğun ${if (half) "yarısı" else "çeyreği"} kırmızı, kalanı mavi olsun. Kaç kırmızı, kaç mavi boncuk kullanırsın?",
                "🔴 Kırmızı", "🔵 Mavi", 0..n, 0..n,
                if (half) "Yarısını bulmak için 2'ye böleriz: $n ÷ 2 = $red" else "Çeyreğini bulmak için 4'e böleriz: $n ÷ 4 = $red",
                render = { a, b -> "🔴 $a + 🔵 $b = ${a + b} boncuk" },
                check = { a, b -> a == red && a + b == n })
        }
    }

    // MAT.2.3.1–2.3.7
    private fun geometri(level: BloomLevel): Question = when (level) {
        HATIRLA -> if (r(0..3) > 0) {
            val (q, a, vis) = listOf(
                Triple("Üçgenin kaç kenarı vardır?", 3, "🔺"),
                Triple("Üçgenin kaç köşesi vardır?", 3, "🔺"),
                Triple("Karenin kaç kenarı vardır?", 4, "🟥"),
                Triple("Karenin kaç köşesi vardır?", 4, "🟥"),
                Triple("Dikdörtgenin kaç kenarı vardır?", 4, "▬"),
                Triple("Dikdörtgenin kaç köşesi vardır?", 4, "▬"),
                Triple("Dairenin kaç köşesi vardır?", 0, "⚪"),
            ).one()
            num(q, a, "Üçgen: 3 kenar, 3 köşe. Kare ve dikdörtgen: 4 kenar, 4 köşe. Dairenin köşesi yoktur.",
                visual = vis, near = listOf(a + 1, a - 1, 0, 3, 4))
        } else if (chance()) {
            text("Hangisi bir geometrik cisimdir?", SOLIDS.one(), SHAPES,
                "Küp, küre, silindir ve prizmalar geometrik cisimdir. Üçgen, kare, dikdörtgen ve daire geometrik şekildir.")
        } else {
            text("Hangisi bir geometrik şekildir?", SHAPES.one(), SOLIDS,
                "Üçgen, kare, dikdörtgen ve daire geometrik şekildir. Küp, küre, silindir ve prizmalar geometrik cisimdir.")
        }
        ANLA -> {
            val same = "Şekil ve cisimlerin yönü, konumu ya da büyüklüğü değişse de biçimi değişmez."
            val (q, a, w, why) = listOf(
                listOf("Kareyi dikdörtgenden ayıran özellik nedir?", "Karenin dört kenarı da eşittir",
                    "Karenin 3 kenarı vardır|Karenin köşesi yoktur|Karenin kenarları eğridir",
                    "Kare ve dikdörtgenin 4 kenarı vardır; karenin dört kenarı da eşit uzunluktadır."),
                listOf("Hangi şeklin köşesi yoktur?", "Daire", "Üçgen|Kare|Dikdörtgen", "Dairenin köşesi ve düz kenarı yoktur."),
                listOf("Hangi şeklin 3 kenarı vardır?", "Üçgen", "Kare|Daire|Dikdörtgen", "Üçgenin 3 kenarı ve 3 köşesi vardır."),
                listOf("Dikdörtgenin karşılıklı kenarları için ne söylenebilir?", "Eşit uzunluktadır",
                    "Hepsi farklı uzunluktadır|Eğridir|Hiç yoktur", "Dikdörtgenin karşılıklı kenarları eşit uzunluktadır."),
                listOf("Bir üçgeni ters çevirirsek hangi şekil olur?", "Yine üçgen olur", "Kare olur|Daire olur|Dikdörtgen olur", same),
                listOf("Bir kareyi büyütürsek hangi şekil olur?", "Yine kare olur", "Üçgen olur|Daire olur|Hiçbir şekil olmaz", same),
                listOf("Yere yatırılmış bir silindir hangi cisimdir?", "Yine silindirdir", "Küredir|Küptür|Üçgen prizmadır", same),
            ).one()
            text(q, a, w.split("|"), why)
        }
        UYGULA -> if (r(0..3) > 0) {
            val (e, obj, ans) = listOf(
                Triple("⚠️", "Üçgen trafik levhası", "Üçgen"),
                Triple("🧀", "Üçgen peynir dilimi", "Üçgen"),
                Triple("🚪", "Bir kapı", "Dikdörtgen"),
                Triple("🍪", "Yuvarlak bir kurabiye", "Daire"),
                Triple("🎲", "Bir zar", "Küp"),
                Triple("⚽", "Bir futbol topu", "Küre"),
                Triple("🌍", "Dünya maketi", "Küre"),
                Triple("🥁", "Bir davul", "Silindir"),
                Triple("🔋", "Bir pil", "Silindir"),
                Triple("📦", "Bir koli", "Dikdörtgen prizma"),
                Triple("📕", "Bir kitap", "Dikdörtgen prizma"),
                Triple("⛺", "Bir çadır", "Üçgen prizma"),
            ).one()
            val isShape = ans in SHAPES
            text("$obj hangi geometrik ${if (isShape) "şekle" else "cisme"} benzer?", ans,
                if (isShape) SHAPES else SOLIDS, "$obj → $ans", visual = e)
        } else {
            val k = r(2..5)
            val n = r(2..4)
            num("Bir sürahi $k bardak su alıyor. $n sürahi su kaç bardak eder?", k * n,
                "$n kez $k bardak: ${List(n) { k }.joinToString(" + ")} = ${k * n}", visual = "🥛",
                near = listOf(k + n, k * n + k, k * n - 1))
        }
        COZUMLE -> when (r(0..3)) {
            0 -> {
                val a = r(1..4)
                val b = r(1..4)
                num("$a üçgen ve $b karenin toplam kaç kenarı vardır?", 3 * a + 4 * b,
                    "Üçgenler: ${List(a) { 3 }.joinToString(" + ")} = ${3 * a}. Kareler: ${List(b) { 4 }.joinToString(" + ")} = ${4 * b}. Toplam: ${3 * a + 4 * b}",
                    visual = "🔺".repeat(a) + "  " + "🟥".repeat(b),
                    near = listOf(3 * a + 4 * b + 1, 4 * a + 3 * b, a + b))
            }
            1 -> {
                val sym = listOf("A", "H", "M", "O", "T", "U", "V", "Y").one()
                text("Hangi harf, ortasından katlandığında iki eş parçaya ayrılır (simetriktir)?", sym,
                    listOf("F", "G", "J", "L", "N", "P", "R", "S", "Z"),
                    "$sym harfini ortasından dikey bir çizgiyle katlarsak iki taraf tam üst üste gelir.")
            }
            2 -> {
                val big = r(3..6)
                val small = big + r(2..5)
                text("Bir kovayı büyük bardakla $big kez, küçük bardakla $small kez doldurduk. Hangi bardak daha çok su alır?",
                    "Büyük bardak", listOf("Küçük bardak", "İkisi eşit su alır"),
                    "Kovayı daha az seferde dolduran bardak daha çok su alır. Kovadaki su miktarı ise değişmez.")
            }
            else -> {
                val (grid, dx, dy) = robotGrid()
                num("Robot 🤖 sadece sağa ve yukarı gidebiliyor. Bayrağa 🚩 en az kaç adımda ulaşır?", dx + dy,
                    "$dx adım sağa ve $dy adım yukarı: $dx + $dy = ${dx + dy} adım.", visual = grid,
                    near = listOf(dx, dy, dx + dy + 1, dx + dy - 1))
            }
        }
        DEGERLENDIR -> {
            val (c, truth, why) = listOf(
                Triple("Her kare aynı zamanda bir dikdörtgendir.", true,
                    "Karenin karşılıklı kenarları eşit ve köşeleri diktir; bu yüzden kare özel bir dikdörtgendir."),
                Triple("Dairenin bir köşesi vardır.", false, "Dairenin köşesi yoktur."),
                Triple("Üçgenin 4 köşesi vardır.", false, "Üçgenin 3 köşesi ve 3 kenarı vardır."),
                Triple("Futbol topu küre şeklindedir.", true, "Top her yönden yuvarlaktır, küreye benzer."),
                Triple("Kareyi döndürünce artık kare olmaz.", false, "Şekli döndürmek biçimini değiştirmez; yine karedir."),
                Triple("Suyu büyük bardaktan küçük bardaklara döktüm, suyun miktarı azaldı.", false,
                    "Ölçme aracı değişse de suyun gerçek miktarı değişmez."),
                Triple("Kelebeğin iki kanadı birbirinin simetriğidir.", true, "Kelebek ortasından ikiye ayrılınca iki taraf birbirinin aynısıdır."),
                Triple("Çadır, üçgen prizmaya benzer.", true, "Çadırın ön ve arka yüzü üçgendir; üçgen prizmaya benzer."),
            ).one()
            claim(said(c), truth, why)
        }
        OLUSTUR -> if (chance()) {
            val n = listOf(7, 10, 11, 13, 14, 17, 18).one()
            build("Üçgen ve karelerden bir grup oluştur. Toplam kenar sayısı $n olsun.", "🔺 Üçgen", "🟥 Kare", 0..8, 0..6,
                "Her üçgenin 3, her karenin 4 kenarı vardır.",
                render = { a, b -> (if (a + b == 0) "Henüz şekil yok" else "🔺".repeat(a) + "🟥".repeat(b)) + "\n${3 * a + 4 * b} kenar" },
                check = { a, b -> 3 * a + 4 * b == n })
        } else {
            val (grid, dx, dy) = robotGrid()
            build("Robotu 🤖 bayrağa 🚩 götür! Kaç adım sağa, kaç adım yukarı gitmeli?", "➡️ Sağa", "⬆️ Yukarı", 0..5, 0..4,
                "Önce bayrağın kaç sütun sağda, sonra kaç satır yukarıda olduğunu say.", visual = grid,
                render = { a, b -> "➡️ $a adım, ⬆️ $b adım" },
                check = { a, b -> a == dx && b == dy })
        }
    }

    private fun robotGrid(): Triple<String, Int, Int> {
        val w = r(3..5)
        val h = r(3..4)
        val dx = r(1 until w)
        val dy = r(1 until h)
        val grid = (h - 1 downTo 0).joinToString("\n") { y ->
            (0 until w).joinToString("") { x ->
                when {
                    x == 0 && y == 0 -> "🤖"
                    x == dx && y == dy -> "🚩"
                    else -> "⬜"
                }
            }
        }
        return Triple(grid, dx, dy)
    }

    // MAT.2.1.8–2.1.11
    private fun olcme(level: BloomLevel): Question = when (level) {
        HATIRLA -> {
            val (q, a, w) = listOf(
                Triple("1 metre kaç santimetredir?", "100", listOf("10", "50", "60")),
                Triple("1 saat kaç dakikadır?", "60", listOf("100", "30", "24")),
                Triple("Yarım saat kaç dakikadır?", "30", listOf("50", "15", "60")),
                Triple("Çeyrek saat kaç dakikadır?", "15", listOf("25", "30", "4")),
                Triple("1 gün kaç saattir?", "24", listOf("12", "60", "7")),
                Triple("1 hafta kaç gündür?", "7", listOf("5", "10", "30")),
                Triple("1 yıl kaç aydır?", "12", listOf("10", "4", "7")),
                Triple("1 yılda kaç mevsim vardır?", "4", listOf("3", "12", "2")),
                Triple("Bir mevsim kaç aydır?", "3", listOf("4", "2", "12")),
                Triple("1 Türk lirası kaç kuruştur?", "100", listOf("10", "50", "25")),
            ).one()
            text(q, a, w, "1 m = 100 cm · 1 saat = 60 dk · 1 gün = 24 saat · 1 hafta = 7 gün · 1 yıl = 12 ay = 4 mevsim · 1 TL = 100 kuruş")
        }
        ANLA -> when (r(0..2)) {
            0 -> {
                val h = r(1..12)
                val m = listOf(0, 15, 30, 45).one()
                fun t(hour: Int, minute: Int) = "Saat $hour:${minute.toString().padStart(2, '0')}"
                text("Saat kaçı gösteriyor?", t(h, m),
                    listOf(t(h % 12 + 1, m), t(h, (m + 15) % 60), t(h, (m + 30) % 60)),
                    "Kısa kol (akrep) saati, uzun kol (yelkovan) dakikayı gösterir. Yelkovan 12'de ise saat tam, 3'te ise çeyrek geçe, 6'da ise buçuk, 9'da ise çeyrek var.",
                    visual = clockVisual(h, m))
            }
            1 -> {
                val (obj, unit) = listOf(
                    "Bir silginin uzunluğunu" to "Santimetre",
                    "Bir kalemin uzunluğunu" to "Santimetre",
                    "Okul koridorunun uzunluğunu" to "Metre",
                    "Sınıfın uzunluğunu" to "Metre",
                    "Bir karpuzun kütlesini" to "Kilogram",
                    "Bir çuval patatesin kütlesini" to "Kilogram",
                    "Bir silginin kütlesini" to "Gram",
                    "Bir kalemin kütlesini" to "Gram",
                ).one()
                text("$obj ölçmek için hangi birim daha uygundur?", unit, listOf("Metre", "Santimetre", "Kilogram", "Gram"),
                    "Kısa uzunluklar santimetre, uzun uzunluklar metre ile; hafif nesneler gram, ağır nesneler kilogram ile ölçülür.")
            }
            else -> {
                val (longer, shorter) = listOf(
                    "Bir gece uyumak" to "Dişlerini fırçalamak",
                    "Bir film izlemek" to "Bir bardak su içmek",
                    "Yaz tatili" to "Hafta sonu tatili",
                    "Okulda bir gün" to "Ayakkabı bağlamak",
                    "Bir mevsim" to "Bir hafta",
                ).one()
                text("Hangisi daha uzun sürer?", longer, listOf(shorter),
                    "“$longer” daha uzun sürer, “$shorter” ise kısa sürer.")
            }
        }
        UYGULA -> when (r(0..4)) {
            0 -> {
                val c = listOf(1, 5, 10, 20).one()
                val k = r(2..5)
                num("$k tane $c liralık para kaç lira eder?", c * k, "${List(k) { c }.joinToString(" + ")} = ${c * k} TL", unit = " TL",
                    near = listOf(c + k, c * k + c, c * k - c))
            }
            1 -> {
                val (coin, k) = listOf(25 to 2, 25 to 4, 50 to 2, 25 to 3).one()
                val total = coin * k
                text("$k tane $coin kuruş kaç eder?", if (total == 100) "1 TL" else "$total kuruş",
                    listOf("50 kuruş", "75 kuruş", "1 TL", "2 TL", "${coin + k} kuruş"),
                    "${List(k) { coin }.joinToString(" + ")} = $total kuruş. 100 kuruş = 1 TL.")
            }
            2 -> {
                val a = r(5..15)
                val b = r(3..12)
                num("$a cm'lik bir kalem ile $b cm'lik bir silgi uç uca konuldu. Toplam uzunluk kaç cm olur?", a + b,
                    "$a + $b = ${a + b} cm", unit = " cm", near = listOf(a + b + 1, a + b - 1, a + b + 10))
            }
            3 -> {
                val a = r(2..9)
                val b = r(2..9)
                num("Manavdan $a kg elma ve $b kg armut aldık. Toplam kaç kg meyve aldık?", a + b,
                    "$a + $b = ${a + b} kg", unit = " kg", near = listOf(a + b + 1, a + b - 1, a * b))
            }
            else -> {
                val start = r(1..9)
                if (chance()) {
                    val dur = r(1..3)
                    text("Film başladığında saat $start:00 idi. Film $dur saat sürdü. Film bittiğinde saat kaç olur?",
                        "Saat ${start + dur}:00",
                        listOf("Saat ${start + dur + 1}:00", "Saat ${start + dur}:30", "Saat ${start + dur - 1}:00"),
                        "$start + $dur = ${start + dur}. Film bittiğinde saat ${start + dur}:00 olur.")
                } else {
                    text("Ders başladığında saat $start:00 idi. Ders 30 dakika sürdü. Ders bittiğinde saat kaç olur?",
                        "Saat $start:30", listOf("Saat ${start + 1}:00", "Saat $start:15", "Saat ${start + 1}:30"),
                        "30 dakika yarım saattir. Yarım saat sonra saat $start:30 olur.")
                }
            }
        }
        COZUMLE -> when (r(0..2)) {
            0 -> {
                val cms = mutableSetOf<Int>()
                while (cms.size < 3) cms += r(20..99)
                val longest = chance()
                val ans = if (longest) "1 m" else "${cms.min()} cm"
                val all = cms.map { "$it cm" } + "1 m"
                text("Hangisi en ${if (longest) "uzundur" else "kısadır"}?", ans, all.filter { it != ans },
                    "1 m = 100 cm olduğunu unutma! Sonra uzunlukları karşılaştırırız.")
            }
            1 -> {
                val t = listOf(15, 20, 25, 30, 35).one()
                fun combo() = List(r(2..3)) { listOf(5, 10, 20).one() }.sortedDescending()
                fun fmt(c: List<Int>) = c.joinToString(" + ") { "$it TL" }
                var right = combo()
                while (right.sum() != t) right = combo()
                val wrong = mutableSetOf<String>()
                while (wrong.size < 3) {
                    val c = combo()
                    if (c.sum() != t) wrong += fmt(c)
                }
                text("Hangisi $t TL eder?", fmt(right), wrong.toList(), "${fmt(right)} = $t TL")
            }
            else -> {
                fun combo() = List(r(2..4)) { listOf(25, 50).one() }.sortedDescending()
                fun fmt(c: List<Int>) = c.joinToString(" + ") { "$it kuruş" }
                var right = combo()
                while (right.sum() != 100) right = combo()
                val wrong = mutableSetOf<String>()
                while (wrong.size < 3) {
                    val c = combo()
                    if (c.sum() != 100) wrong += fmt(c)
                }
                text("Hangisi 1 TL eder?", fmt(right), wrong.toList(), "${fmt(right)} = 100 kuruş = 1 TL")
            }
        }
        DEGERLENDIR -> when (r(0..3)) {
            0 -> {
                val len = r(8..25)
                val (start, from) = listOf(1 to "1'den", 2 to "2'den", 3 to "3'ten").one()
                text("${NAMES.one()} kitabının boyunu ölçerken cetveli 0'dan değil $from başlattı ve ${len + start} cm okudu. Kitabın gerçek boyu kaç cm?",
                    "$len cm", listOf("${len + start} cm", "${len + 2 * start} cm", "${len - 1 - start} cm"),
                    "Ölçmeye 0'dan başlamalıyız. ${len + start} − $start = $len cm")
            }
            1 -> {
                val price = r(1..8) * 5
                val paid = if (price < 20) 20 else 50
                val change = paid - price
                val honest = chance()
                val given = if (honest) change else change + 10
                claim(said("$paid TL verdim, $price TL'lik oyuncak aldım. Kasiyer bana $given TL para üstü verdi, bu doğru."),
                    honest, "Para üstü: $paid − $price = $change TL")
            }
            2 -> {
                val real = r(40..90)
                val guess = (real + r(-15..15)).coerceIn(30, 100)
                val ans = when {
                    guess < real -> "Tahmini ölçüm sonucundan az"
                    guess > real -> "Tahmini ölçüm sonucundan fazla"
                    else -> "Tahmini ölçüm sonucuna eşit"
                }
                text("${NAMES.one()} sırasının boyunu $guess cm tahmin etti. Cetvelle ölçünce $real cm buldu. Tahmini için ne söylenebilir?",
                    ans, listOf("Tahmini ölçüm sonucundan az", "Tahmini ölçüm sonucundan fazla", "Tahmini ölçüm sonucuna eşit"),
                    "Tahmin $guess cm, ölçüm $real cm. Aradaki fark ${abs(real - guess)} cm.")
            }
            else -> claim(said("Masayı ben 8 karış, arkadaşım 6 karış ölçtü. Demek ki masa ikimiz için farklı uzunluktadır."), false,
                "Masa aynı masadır! Karışlarımız farklı büyüklükte olduğu için farklı sonuç bulduk. Bu yüzden metre ve cetvel gibi standart ölçme araçlarına ihtiyaç vardır.")
        }
        OLUSTUR -> if (chance()) {
            val n = r(3..12) * 5
            build("$n TL'yi 10 TL'lik ve 5 TL'lik paralarla oluştur.", "10 TL", "5 TL", 0..6, 0..12,
                "İki tane 5 TL, bir tane 10 TL eder. Farklı yollarla oluşturabilirsin!",
                render = { a, b -> "10 TL'lik: $a tane\n5 TL'lik: $b tane\nToplam: ${10 * a + 5 * b} TL" },
                check = { a, b -> 10 * a + 5 * b == n })
        } else {
            val (what, total) = listOf("1 TL'yi" to 100, "75 kuruşu" to 75, "2 TL'yi" to 200).one()
            build("$what 50 kuruşluk ve 25 kuruşluk paralarla oluştur.", "50 kuruş", "25 kuruş", 0..4, 0..8,
                "100 kuruş = 1 TL. İki tane 25 kuruş, bir tane 50 kuruş eder.",
                render = { a, b ->
                    val sum = 50 * a + 25 * b
                    "50 kuruşluk: $a tane\n25 kuruşluk: $b tane\nToplam: " + if (sum % 100 == 0 && sum > 0) "${sum / 100} TL" else "$sum kuruş"
                },
                check = { a, b -> 50 * a + 25 * b == total })
        }
    }

    // MAT.2.4.1: shape graph and frequency table, at most two data groups
    private class Chart(val title: String, val items: List<Pair<String, String>>, val counts: List<Int>, val table: Boolean) {
        val names get() = items.map { it.second }
        fun visual() = if (table) {
            "$title\nSıklık tablosu\n" + items.indices.joinToString("\n") { "${items[it].first} ${items[it].second}: ${counts[it]}" }
        } else {
            "$title\nŞekil grafiği (■ = 1 öğrenci)\n" + items.indices.joinToString("\n") { "${items[it].first} ${items[it].second}: ${"■".repeat(counts[it])}" }
        }
    }

    private fun chart(table: Boolean = r(0..2) == 0): Chart {
        val (title, all) = CHARTS.one()
        val items = all.shuffled(random).take(3)
        val counts = (2..9).shuffled(random).take(3)
        return Chart(title, items, counts, table)
    }

    private fun veri(level: BloomLevel): Question {
        if (level == OLUSTUR) {
            val n = listOf(10, 12, 14, 16).one()
            val d = listOf(2, 4).one()
            return build("Sınıfta $n öğrenci var. Herkes kedi ya da köpek seçti. Köpek seçenler, kedi seçenlerden $d fazla olacak şekilde şekil grafiğini oluştur.",
                "🐱 Kedi", "🐶 Köpek", 0..16, 0..16,
                "Toplam $n olmalı ve köpekler kedilerden $d fazla olmalı.",
                render = { a, b -> "🐱 Kedi: ${"■".repeat(a)} ($a)\n🐶 Köpek: ${"■".repeat(b)} ($b)\nToplam: ${a + b}" },
                check = { a, b -> a + b == n && b - a == d })
        }
        if (level == UYGULA && chance()) return twoGroups()
        if (level == DEGERLENDIR && r(0..2) == 0) {
            val good = listOf(
                "Sınıfımızda en çok hangi meyve seviliyor?",
                "Sınıfımızdaki öğrenciler okula nasıl geliyor?",
                "Sınıfımızda en çok hangi mevsim seviliyor?",
            ).one()
            return text("Hangisi, sınıftan veri toplayarak cevaplayabileceğimiz bir araştırma sorusudur?", good,
                listOf("Elma kırmızı mıdır?", "Bugün günlerden ne?", "Kedi bir hayvan mıdır?", "Okulumuzun adı nedir?"),
                "Araştırma sorusunun cevabı kişiden kişiye değişir; bu yüzden veri toplamamız gerekir.")
        }
        val c = chart()
        val v = c.visual()
        val i = r(0..2)
        val j = (i + r(1..2)) % 3
        return when (level) {
            HATIRLA -> num("${if (c.table) "Tabloya" else "Grafiğe"} göre “${c.names[i]}” kaç oy almıştır?", c.counts[i],
                if (c.table) "Tabloda “${c.names[i]}” karşısında ${c.counts[i]} yazıyor." else "“${c.names[i]}” satırında ${c.counts[i]} kare var.",
                visual = v, near = c.counts)
            ANLA -> {
                val most = chance()
                val idx = if (most) c.counts.indexOf(c.counts.max()) else c.counts.indexOf(c.counts.min())
                text("En ${if (most) "çok" else "az"} oyu hangisi almıştır?", c.names[idx], c.names,
                    "Satırları karşılaştırırız: “${c.names[idx]}” ${c.counts[idx]} oy almış.", visual = v)
            }
            UYGULA -> num("Toplam kaç öğrenci oy vermiştir?", c.counts.sum(),
                "${c.counts.joinToString(" + ")} = ${c.counts.sum()}", visual = v,
                near = listOf(c.counts.sum() + 1, c.counts.sum() - 1, c.counts.max()))
            COZUMLE -> num("“${c.names[i]}” ile “${c.names[j]}” arasında kaç oy fark vardır?", abs(c.counts[i] - c.counts[j]),
                "${max(c.counts[i], c.counts[j])} − ${min(c.counts[i], c.counts[j])} = ${abs(c.counts[i] - c.counts[j])}",
                visual = v, near = listOf(c.counts[i] + c.counts[j], c.counts[i], c.counts[j]))
            DEGERLENDIR -> if (chance()) {
                val k = c.counts.indexOf(c.counts.max())
                val (a, b) = (0..2).filter { it != k }
                val sum = c.counts[a] + c.counts[b]
                claim(said("“${c.names[a]}” ve “${c.names[b]}” oylarının toplamı, “${c.names[k]}” oylarından fazladır."),
                    sum > c.counts[k],
                    "${c.counts[a]} + ${c.counts[b]} = $sum. “${c.names[k]}” ise ${c.counts[k]} oy almış.", visual = v)
            } else {
                val minIdx = c.counts.indexOf(c.counts.min())
                claim(said("En az oyu “${c.names[i]}” almıştır."), i == minIdx,
                    "En az oyu “${c.names[minIdx]}” almıştır (${c.counts[minIdx]} oy).", visual = v)
            }
            OLUSTUR -> error("handled above")
        }
    }

    private fun twoGroups(): Question {
        val (a, b) = listOf("Yaz", "Kış", "İlkbahar", "Sonbahar").shuffled(random).take(2)
        var counts = List(4) { r(2..9) }
        while (counts[0] == counts[2] || counts[1] == counts[3] || counts[0] == counts[1] || counts[2] == counts[3]) counts = List(4) { r(2..9) }
        val (aA, bA, aB, bB) = counts
        val visual = "En sevdiğimiz mevsim\nŞekil grafiği (■ = 1 öğrenci)\n" +
            "2/A $a: ${"■".repeat(aA)}\n2/A $b: ${"■".repeat(bA)}\n2/B $a: ${"■".repeat(aB)}\n2/B $b: ${"■".repeat(bB)}"
        return if (chance()) {
            text("“$a” mevsimini seçenler hangi sınıfta daha çoktur?", if (aA > aB) "2/A" else "2/B", listOf("2/A", "2/B"),
                "2/A sınıfında $aA, 2/B sınıfında $aB öğrenci “$a” mevsimini seçmiş.", visual)
        } else {
            text("2/B sınıfında hangi mevsimi seçen öğrenciler daha çoktur?", if (aB > bB) a else b, listOf(a, b),
                "2/B sınıfında “$a” $aB, “$b” $bB oy almış.", visual)
        }
    }

    // MAT.2.1.4, 2.1.5: patterns given in three steps, continued at most three more
    private fun oruntu(level: BloomLevel): Question = when (level) {
        HATIRLA -> {
            val pool = listOf("🔴", "🔵", "🟢", "🟡", "⭐", "🌙", "🔺", "🟦").shuffled(random)
            val unit = listOf(
                listOf(pool[0], pool[1]),
                listOf(pool[0], pool[1], pool[1]),
                listOf(pool[0], pool[0], pool[1]),
                listOf(pool[0], pool[1], pool[2]),
            ).one()
            val full = List(7) { unit[it % unit.size] }
            text("Örüntüde ❓ yerine hangisi gelmelidir?", full[6], unit.distinct() + pool[3],
                "Tekrar eden birim: ${unit.joinToString("")}. Bu birim tekrar ettikçe sıradaki ${full[6]} olur.",
                visual = full.take(6).joinToString("") + "❓")
        }
        ANLA -> {
            val s = listOf(2, 3, 5, 10).one()
            val up = chance()
            val start = if (up) r(1..20) else r(4..9) * 10
            val seq = List(3) { if (up) start + it * s else start - it * s }
            fun rule(step: Int, isUp: Boolean) = "${ritmik(step)} ${if (isUp) "artıyor" else "azalıyor"}"
            text("${seq.joinToString(", ")} örüntüsünün kuralı nedir?", rule(s, up),
                listOf(rule(s, !up)) + listOf(2, 3, 5, 10).filter { it != s }.map { rule(it, up) },
                "Ardışık iki terim arasındaki fark $s. Sayılar ${if (up) "büyüyor" else "küçülüyor"}.")
        }
        UYGULA -> {
            val s = listOf(2, 3, 4, 5, 10).one()
            val start = r(1..10)
            val seq = List(3) { start + it * s }
            val k = r(4..6)
            val ans = start + (k - 1) * s
            num("${seq.joinToString(", ")}, ... örüntüsünün $k. adımı kaçtır?", ans,
                "Her adımda $s artıyor: ${List(k) { start + it * s }.joinToString(", ")}",
                near = listOf(ans - s, ans + s, ans + 1))
        }
        COZUMLE -> if (chance()) {
            val s = listOf(2, 3, 4, 5, 10).one()
            val start = r(1..20)
            val seq = List(5) { start + it * s }
            val hole = r(1..3)
            num("Örüntüdeki eksik sayı kaçtır?\n" + seq.mapIndexed { idx, x -> if (idx == hole) "?" else "$x" }.joinToString(", "),
                seq[hole], "Örüntü ${ritmik(s)} artıyor: ${seq[hole - 1]} + $s = ${seq[hole]}",
                near = listOf(seq[hole] + 1, seq[hole] - 1, seq[hole] + s))
        } else {
            val start = r(1..3)
            val s = r(1..3)
            val steps = List(3) { start + it * s }
            val k = r(4..5)
            val ans = start + (k - 1) * s
            num("Şekil örüntüsünde ${k}. adımda kaç nokta olur?", ans,
                "Her adımda $s nokta ekleniyor: ${List(k) { start + it * s }.joinToString(", ")}",
                visual = steps.mapIndexed { idx, n -> "${idx + 1}. adım: ${"●".repeat(n)}" }.joinToString("\n"),
                near = listOf(ans - s, ans + s, ans + 1))
        }
        DEGERLENDIR -> {
            val s = listOf(2, 3, 5, 10).one()
            val start = r(1..20)
            val seq = List(4) { start + it * s }.toMutableList()
            val honest = chance()
            if (!honest) seq[3] += 1
            claim(said("${seq.joinToString(", ")} örüntüsü ${ritmik(s)} artan bir örüntüdür."), honest,
                if (honest) "Her terim bir öncekinden $s fazla. Kural doğru."
                else "${seq[2]} + $s = ${seq[2] + s} olmalıydı, ${seq[3]} değil.")
        }
        OLUSTUR -> {
            val t = r(10..30)
            build("Kendi sayı örüntünü oluştur! Başlangıç sayısını ve artış miktarını seç. Örüntünün 4. adımı $t olsun.",
                "Başlangıç", "Artış", 0..30, 1..10,
                "4. adım = başlangıç + 3 × artış. Farklı başlangıç ve artışlarla deneyebilirsin!",
                render = { a, b -> List(4) { a + it * b }.joinToString(", ") },
                check = { a, b -> a + 3 * b == t })
        }
    }

    companion object {
        private val NAMES = listOf("Ali", "Elif", "Mert", "Zeynep", "Can", "Ecrin", "Yusuf", "Defne", "Ömer", "Asel")
        private val OBJECTS = listOf("🍎", "🍓", "⚽", "🎈", "🐟", "⭐", "🌸")
        private val SHAPES = listOf("Üçgen", "Kare", "Dikdörtgen", "Daire")
        private val SOLIDS = listOf("Küp", "Küre", "Silindir", "Kare prizma", "Dikdörtgen prizma", "Üçgen prizma")
        private val CHARTS = listOf(
            "Sınıfımızın en sevdiği meyveler" to listOf("🍎" to "Elma", "🍌" to "Muz", "🍓" to "Çilek", "🍊" to "Portakal"),
            "Sınıfımızın en sevdiği hayvanlar" to listOf("🐱" to "Kedi", "🐶" to "Köpek", "🐦" to "Kuş", "🐟" to "Balık"),
            "Sınıfımızın en sevdiği sporlar" to listOf("⚽" to "Futbol", "🏀" to "Basketbol", "🏐" to "Voleybol", "🎾" to "Tenis"),
            "Okula nasıl geliyoruz?" to listOf("🚶" to "Yürüyerek", "🚌" to "Servisle", "🚗" to "Arabayla", "🚲" to "Bisikletle"),
        )

        private val BIRLER = listOf("", "bir", "iki", "üç", "dört", "beş", "altı", "yedi", "sekiz", "dokuz")
        private val ONLAR = listOf("", "on", "yirmi", "otuz", "kırk", "elli", "altmış", "yetmiş", "seksen", "doksan")

        fun oku(n: Int): String = when (n) {
            0 -> "sıfır"
            100 -> "yüz"
            else -> listOf(ONLAR[n / 10], BIRLER[n % 10]).filter { it.isNotEmpty() }.joinToString(" ")
        }

        fun round10(n: Int) = (n + 5) / 10 * 10

        private fun ters(n: Int) = (n % 10) * 10 + n / 10

        private fun ritmik(s: Int) = when (s) {
            2 -> "2'şer"
            3 -> "3'er"
            4 -> "4'er"
            5 -> "5'er"
            10 -> "10'ar"
            else -> "$s'er"
        }

        private fun String.cap() = replaceFirstChar { it.titlecase(Locale.forLanguageTag("tr")) }
    }
}
