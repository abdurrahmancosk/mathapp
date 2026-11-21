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
        near.filter { it >= 0 && it != answer }.shuffled(random).forEach { if (set.size < 4) set += it }
        var spread = 2
        while (set.size < 4) {
            val c = answer + (if (chance()) 1 else -1) * r(1..spread)
            if (c >= 0) set += c
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

    // endregion

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
            if (chance()) {
                num("$n sayısında kaç onluk vardır?", o, "$n = $o onluk + $b birlik", near = listOf(b, o + 1, o - 1))
            } else {
                num("$o onluk ve $b birlik hangi sayıyı oluşturur?", n, "$o onluk = ${o * 10}. ${o * 10} + $b = $n",
                    visual = "🟦".repeat(o) + "  " + "🟡".repeat(b) + "\n🟦 = 10    🟡 = 1",
                    near = listOf(ters(n), o + b, n + 10))
            }
        }
        UYGULA -> {
            val s = listOf(2, 3, 4, 5, 10).one()
            val start = if (s == 10) r(1..9) else s * r(1..6)
            val seq = List(4) { start + it * s }
            val ans = start + 4 * s
            num("${ritmik(s)} ritmik say: ${seq.joinToString(", ")}, ?", ans,
                "Her adımda $s ekleriz: ${seq.last()} + $s = $ans",
                near = listOf(seq.last() + 1, ans + 1, ans + s))
        }
        COZUMLE -> if (chance()) {
            val a = r(1..9)
            var b = r(1..9)
            while (b == a) b = r(1..9)
            val nums = linkedSetOf(a * 10 + b, b * 10 + a)
            while (nums.size < 4) nums += r(10..99)
            val big = chance()
            val ans = if (big) nums.max() else nums.min()
            text("Hangisi en ${if (big) "büyük" else "küçük"} sayıdır?", "$ans", nums.filter { it != ans }.map { "$it" },
                "Önce onlar basamaklarını karşılaştırırız; eşitse birler basamağına bakarız. Cevap: $ans")
        } else {
            val even = chance()
            fun pick(wantEven: Boolean): Int {
                var x = r(10..99)
                while ((x % 2 == 0) != wantEven) x = r(10..99)
                return x
            }
            val ans = pick(even)
            val wrong = mutableSetOf<Int>()
            while (wrong.size < 3) wrong += pick(!even)
            text("Hangisi ${if (even) "çift" else "tek"} sayıdır?", "$ans", wrong.map { "$it" },
                "Birler basamağı 0, 2, 4, 6 veya 8 olan sayılar çifttir; 1, 3, 5, 7 veya 9 olanlar tektir.")
        }
        DEGERLENDIR -> if (chance()) {
            val a = r(10..99)
            var b = r(10..99)
            while (b == a) b = r(10..99)
            val why = if (a / 10 != b / 10) {
                "Onlar basamaklarına bakarız: ${a / 10} ve ${b / 10}."
            } else {
                "Onlar basamakları eşit, birler basamaklarına bakarız: ${a % 10} ve ${b % 10}."
            }
            claim(said("$a, $b sayısından büyüktür."), a > b, "$why Bu yüzden ${max(a, b)} > ${min(a, b)}.")
        } else {
            val n = r(10..99)
            val even = n % 2 == 0
            claim(said("$n bir çift sayıdır."), even,
                "$n sayısının birler basamağı ${n % 10}. " +
                    if (even) "0, 2, 4, 6, 8 ile biten sayılar çifttir." else "1, 3, 5, 7, 9 ile biten sayılar tektir.")
        }
        OLUSTUR -> {
            val lo = r(2..6) * 10
            val (prompt, rule) = listOf<Pair<String, (Int) -> Boolean>>(
                "$lo ile ${lo + 20} arasında bir sayı oluştur." to { n -> n > lo && n < lo + 20 },
                "50'den büyük bir tek sayı oluştur." to { n -> n > 50 && n % 2 == 1 },
                "30'dan küçük bir çift sayı oluştur." to { n -> n < 30 && n % 2 == 0 },
                "Rakamlarının toplamı 9 olan bir sayı oluştur." to { n -> n / 10 + n % 10 == 9 },
                "Onlar basamağı, birler basamağından büyük olan bir sayı oluştur." to { n -> n / 10 > n % 10 },
            ).one()
            build(prompt, "Onluk", "Birlik", 1..9, 0..9,
                "Onlukları ve birlikleri değiştirerek koşula uyan sayıyı bulabilirsin. Birden fazla doğru cevap olabilir!",
                render = { a, b -> "$a onluk + $b birlik = ${a * 10 + b}" },
                check = { a, b -> rule(a * 10 + b) })
        }
    }

    private fun toplama(level: BloomLevel): Question = when (level) {
        HATIRLA -> {
            val a = r(2..9)
            val b = r(2..9)
            num("$a + $b = ?", a + b, "$a + $b = ${a + b}", near = listOf(a + b + 1, a + b - 1, a + b + 10))
        }
        ANLA -> {
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
        }
        UYGULA -> {
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
        COZUMLE -> {
            val tot = r(30..90)
            val a = r(10..tot - 10)
            num("$a + ? = $tot", tot - a,
                "Eksik sayıyı bulmak için çıkarma yaparız: $tot − $a = ${tot - a}. Kontrol: $a + ${tot - a} = $tot",
                near = listOf(tot - a + 10, tot - a - 10, tot - a + 1))
        }
        DEGERLENDIR -> {
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
                listOf("Yanlış, sonuç $forgot olmalı", "Yanlış, sonuç ${correct + 10} olmalı")
            } else {
                listOf("Doğru", "Yanlış, sonuç ${correct + 10} olmalı")
            }
            text("${NAMES.one()}, $a + $b işleminin sonucunu $claimed buldu. Bu sonuç doğru mu?", answer, wrong,
                "Birler: $ao + $bo = ${ao + bo}. ${ao + bo - 10} yazılır, 1 onluk elde onlar basamağına eklenir. $a + $b = $correct")
        }
        OLUSTUR -> {
            val t = r(15..60)
            build("Toplamı $t olan bir toplama işlemi oluştur.", "1. sayı", "2. sayı", 0..t, 0..t,
                "Toplamı $t olan pek çok işlem vardır. Sayılardan biri büyürken diğeri küçülmeli!",
                render = { a, b -> "$a + $b = ${a + b}" },
                check = { a, b -> a + b == t && a > 0 && b > 0 })
        }
    }

    private fun cikarma(level: BloomLevel): Question = when (level) {
        HATIRLA -> {
            val a = r(10..18)
            val b = r(2..9)
            num("$a − $b = ?", a - b, "$a − $b = ${a - b}", near = listOf(a - b + 1, a - b - 1, a + b))
        }
        ANLA -> {
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
        }
        UYGULA -> {
            val a = r(40..95)
            val b = r(11..a - 10)
            val prompt = listOf(
                "Otobüste $a yolcu vardı. Durakta $b yolcu indi. Otobüste kaç yolcu kaldı?",
                "Manavda $a karpuz vardı. $b tanesi satıldı. Kaç karpuz kaldı?",
                "Elif'in $a lirası vardı. $b liralık bir kitap aldı. Kaç lirası kaldı?",
                "Bir ağaçta $a elma vardı. $b tanesi toplandı. Ağaçta kaç elma kaldı?",
            ).one()
            num(prompt, a - b, "$a − $b = ${a - b}", near = listOf(a - b + 10, a - b - 10, a + b))
        }
        COZUMLE -> {
            val b = r(10..40)
            val d = r(10..50)
            num("? − $b = $d", b + d,
                "Eksik sayıyı bulmak için toplama yaparız: $d + $b = ${b + d}. Kontrol: ${b + d} − $b = $d",
                near = listOf(d - b, b + d + 10, b + d - 10))
        }
        DEGERLENDIR -> {
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
                listOf("Yanlış, sonuç $mistake olmalı", "Yanlış, sonuç ${correct + 10} olmalı")
            } else {
                listOf("Doğru", "Yanlış, sonuç ${correct + 10} olmalı")
            }
            text("${NAMES.one()}, $a − $b işleminin sonucunu $claimed buldu. Bu sonuç doğru mu?", answer, wrong,
                "Birler basamağında $ao, $bo sayısından küçük olduğu için onlar basamağından 1 onluk bozarız. $a − $b = $correct")
        }
        OLUSTUR -> {
            val d = r(5..40)
            build("Farkı $d olan bir çıkarma işlemi oluştur.", "Eksilen", "Çıkan", 0..99, 0..99,
                "İki sayı arasındaki fark $d olmalı. İpucu: Çıkana $d eklersen eksileni bulursun.",
                render = { a, b -> "$a − $b = ${a - b}" },
                check = { a, b -> a - b == d && b > 0 })
        }
    }

    private fun carpma(level: BloomLevel): Question = when (level) {
        HATIRLA -> {
            val a = r(1..5)
            val b = r(1..10)
            num("$a × $b = ?", a * b, "$a × $b = ${a * b} ($a tane $b)", near = listOf(a * b + a, a * b - b, a + b))
        }
        ANLA -> {
            val g = r(2..5)
            val n = r(2..5)
            text("${List(g) { n }.joinToString(" + ")} işlemi hangi çarpma işlemiyle gösterilir?", "$g × $n",
                listOf("$g + $n", "${g + 1} × $n", "$g × ${n + 1}"),
                "$g tane $n var. Bu, $g × $n = ${g * n} demektir.")
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
            when (r(0..2)) {
                0 -> claim(said("$a × $b ile $b × $a aynı sonucu verir."), true,
                    "Çarpanların yeri değişse de sonuç değişmez: $a × $b = $b × $a = ${a * b}")
                1 -> claim(said("$a × $b = ${a + b}"), false,
                    "$a × $b, $a tane $b demektir: ${List(a) { b }.joinToString(" + ")} = ${a * b}. ${a + b} toplama sonucudur.")
                else -> claim(said("$a × $b işlemi $a tane $b demektir."), true,
                    "Evet! $a × $b = ${List(a) { b }.joinToString(" + ")} = ${a * b}")
            }
        }
        OLUSTUR -> {
            val t = listOf(6, 8, 10, 12, 15, 16, 18, 20, 24).one()
            build("Sonucu $t olan bir çarpma işlemi oluştur.", "1. çarpan", "2. çarpan", 1..12, 1..12,
                "$t sayısını eşit gruplara ayırmayı düşün. Birden fazla doğru cevap var!",
                render = { a, b -> "$a × $b = ${a * b}" },
                check = { a, b -> a * b == t })
        }
    }

    private fun bolme(level: BloomLevel): Question = when (level) {
        HATIRLA -> if (r(0..3) == 0) {
            text("Bölme işleminin işareti hangisidir?", "÷", listOf("×", "+", "−"), "Bölme işareti ÷ şeklindedir.")
        } else {
            val g = r(2..5)
            val q = r(1..5)
            num("${g * q} ÷ $g = ?", q, "${g * q} nesne $g eş gruba ayrılırsa her grupta $q nesne olur.",
                near = listOf(q + 1, g, g * q - g))
        }
        ANLA -> {
            val g = r(2..5)
            val q = r(2..5)
            val total = g * q
            text("$total ÷ $g işlemi ne anlama gelir?", "$total nesneyi $g eş gruba ayırmak",
                listOf("$total nesneye $g nesne eklemek", "$total nesneden $g nesne çıkarmak", "$total nesneyi $g kez saymak"),
                "$total ÷ $g: $total nesne $g eş gruba ayrılır, her grupta $q nesne olur.")
        }
        UYGULA -> {
            val g = r(2..5)
            val q = r(2..6)
            val (e, item, place) = listOf(
                Triple("🍪", "kurabiye", "tabağa"),
                Triple("🍎", "elma", "sepete"),
                Triple("⚽", "top", "kutuya"),
                Triple("🌸", "çiçek", "vazoya"),
            ).one()
            val total = g * q
            num("$total $item $g $place eşit olarak paylaştırılıyor. Her birine kaç $item düşer?", q,
                "$total ÷ $g = $q, çünkü $g × $q = $total",
                visual = if (total <= 20) e.repeat(total) else null,
                near = listOf(q + 1, q - 1, g))
        }
        COZUMLE -> {
            val g = r(2..5)
            val q = r(2..9)
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
                else "Her arkadaşa ${n / k} şeker düşer ama ${n % k} şeker artar.")
        }
        OLUSTUR -> {
            val n = listOf(12, 16, 18, 20, 24).one()
            build("$n kalemi eşit gruplara ayır. Kaç grup olsun, her grupta kaç kalem olsun?",
                "Grup sayısı", "Gruptaki kalem", 1..12, 1..12,
                "En az 2 grup ve her grupta en az 2 kalem olmalı. Grup sayısı × gruptaki kalem = $n",
                render = { a, b -> "$a grup × $b kalem = ${a * b} kalem" },
                check = { a, b -> a * b == n && a > 1 && b > 1 })
        }
    }

    private fun kesirler(level: BloomLevel): Question = when (level) {
        HATIRLA -> {
            val (q, a, w) = listOf(
                Triple("Bir bütünün 2 eş parçasından her birine ne denir?", "Yarım", listOf("Çeyrek", "Bütün")),
                Triple("Bir bütünün 4 eş parçasından her birine ne denir?", "Çeyrek", listOf("Yarım", "Bütün")),
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

    private fun geometri(level: BloomLevel): Question = when (level) {
        HATIRLA -> {
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
        }
        ANLA -> {
            val (q, a, w) = listOf(
                Triple("Kareyi dikdörtgenden ayıran özellik nedir?", "Karenin dört kenarı da eşittir",
                    listOf("Karenin 3 kenarı vardır", "Karenin köşesi yoktur", "Karenin kenarları eğridir")),
                Triple("Hangi şeklin köşesi yoktur?", "Daire", listOf("Üçgen", "Kare", "Dikdörtgen")),
                Triple("Hangi şeklin 3 kenarı vardır?", "Üçgen", listOf("Kare", "Daire", "Dikdörtgen")),
                Triple("Dikdörtgenin karşılıklı kenarları için ne söylenebilir?", "Eşit uzunluktadır",
                    listOf("Hepsi farklı uzunluktadır", "Eğridir", "Hiç yoktur")),
                Triple("Küp şeklindeki bir zarın kaç yüzü vardır?", "6", listOf("4", "8", "12")),
            ).one()
            text(q, a, w, "Cevap: $a.")
        }
        UYGULA -> {
            val shapes = listOf("Üçgen", "Kare", "Dikdörtgen", "Daire")
            val solids = listOf("Küre", "Küp", "Silindir", "Koni")
            val (e, obj, ans) = listOf(
                Triple("🍕", "Bir pizza dilimi", "Üçgen"),
                Triple("🧀", "Üçgen peynir dilimi", "Üçgen"),
                Triple("🚪", "Bir kapı", "Dikdörtgen"),
                Triple("🍪", "Yuvarlak bir kurabiye", "Daire"),
                Triple("🎲", "Bir zar", "Küp"),
                Triple("🎁", "Küp şeklinde bir hediye kutusu", "Küp"),
                Triple("⚽", "Bir futbol topu", "Küre"),
                Triple("🥁", "Bir davul", "Silindir"),
                Triple("🍦", "Bir dondurma külahı", "Koni"),
            ).one()
            val isShape = ans in shapes
            text("$obj hangi geometrik ${if (isShape) "şekle" else "cisme"} benzer?", ans,
                if (isShape) shapes else solids, "$obj → $ans", visual = e)
        }
        COZUMLE -> {
            val a = r(1..4)
            val b = r(1..4)
            num("$a üçgen ve $b karenin toplam kaç kenarı vardır?", 3 * a + 4 * b,
                "Üçgenler: $a × 3 = ${3 * a}. Kareler: $b × 4 = ${4 * b}. Toplam: ${3 * a + 4 * b}",
                visual = "🔺".repeat(a) + "  " + "🟥".repeat(b),
                near = listOf(3 * a + 4 * b + 1, 4 * a + 3 * b, a + b))
        }
        DEGERLENDIR -> {
            val (c, truth, why) = listOf(
                Triple("Her kare aynı zamanda bir dikdörtgendir.", true,
                    "Karenin karşılıklı kenarları eşit ve köşeleri diktir; bu yüzden kare özel bir dikdörtgendir."),
                Triple("Dairenin bir köşesi vardır.", false, "Dairenin köşesi yoktur."),
                Triple("Üçgenin 4 köşesi vardır.", false, "Üçgenin 3 köşesi ve 3 kenarı vardır."),
                Triple("Dikdörtgenin 4 kenarı vardır.", true, "Dikdörtgenin 4 kenarı ve 4 köşesi vardır."),
                Triple("Futbol topu küre şeklindedir.", true, "Top her yönden yuvarlaktır, küreye benzer."),
                Triple("Bir zarın 4 yüzü vardır.", false, "Zar küp şeklindedir ve 6 yüzü vardır."),
            ).one()
            claim(said(c), truth, why)
        }
        OLUSTUR -> {
            val n = listOf(7, 10, 11, 13, 14, 17, 18).one()
            build("Üçgen ve karelerden bir grup oluştur. Toplam kenar sayısı $n olsun.", "🔺 Üçgen", "🟥 Kare", 0..8, 0..6,
                "Her üçgenin 3, her karenin 4 kenarı vardır.",
                render = { a, b -> (if (a + b == 0) "Henüz şekil yok" else "🔺".repeat(a) + "🟥".repeat(b)) + "\n${3 * a + 4 * b} kenar" },
                check = { a, b -> 3 * a + 4 * b == n })
        }
    }

    private fun olcme(level: BloomLevel): Question = when (level) {
        HATIRLA -> {
            val (q, a, w) = listOf(
                Triple("1 metre kaç santimetredir?", "100", listOf("10", "50", "1000")),
                Triple("1 saat kaç dakikadır?", "60", listOf("100", "30", "24")),
                Triple("1 gün kaç saattir?", "24", listOf("12", "60", "7")),
                Triple("1 hafta kaç gündür?", "7", listOf("5", "10", "30")),
                Triple("1 Türk lirası kaç kuruştur?", "100", listOf("10", "50", "1000")),
                Triple("Yarım saat kaç dakikadır?", "30", listOf("50", "15", "60")),
            ).one()
            text(q, a, w, "1 m = 100 cm · 1 saat = 60 dakika · 1 gün = 24 saat · 1 hafta = 7 gün · 1 TL = 100 kuruş")
        }
        ANLA -> if (chance()) {
            val h = r(1..12)
            val half = chance()
            val cp = if (half) 0x1F55C + h - 1 else 0x1F550 + h - 1
            fun t(hour: Int, isHalf: Boolean) = "Saat $hour:${if (isHalf) "30" else "00"}"
            text("Saat kaçı gösteriyor?", t(h, half),
                listOf(t(h % 12 + 1, half), t(h, !half), t((h + 4) % 12 + 1, half)),
                "Kısa kol (akrep) saati, uzun kol (yelkovan) dakikayı gösterir. Yelkovan 12'yi gösteriyorsa saat tamdır, 6'yı gösteriyorsa buçuktur.",
                visual = String(Character.toChars(cp)))
        } else {
            val (obj, unit) = listOf(
                "Bir silginin uzunluğunu" to "Santimetre",
                "Bir kalemin uzunluğunu" to "Santimetre",
                "Okul koridorunun uzunluğunu" to "Metre",
                "Sınıfın uzunluğunu" to "Metre",
                "Bir defterin uzunluğunu" to "Santimetre",
            ).one()
            text("$obj ölçmek için hangi birim daha uygundur?", unit, listOf("Metre", "Santimetre", "Saat"),
                "Kısa nesneleri santimetre, uzun mesafeleri metre ile ölçeriz.")
        }
        UYGULA -> when (r(0..2)) {
            0 -> {
                val c = listOf(1, 5, 10, 20).one()
                val k = r(2..5)
                num("$k tane $c liralık para kaç lira eder?", c * k, "$k × $c = ${c * k} TL", unit = " TL",
                    near = listOf(c + k, c * k + c, c * k - c))
            }
            1 -> {
                val a = r(5..15)
                val b = r(3..12)
                num("$a cm'lik bir kalem ile $b cm'lik bir silgi uç uca konuldu. Toplam uzunluk kaç cm olur?", a + b,
                    "$a + $b = ${a + b} cm", unit = " cm", near = listOf(a + b + 1, a + b - 1, a + b + 10))
            }
            else -> {
                val start = r(1..9)
                val dur = r(1..3)
                text("Film başladığında saat $start:00 idi. Film $dur saat sürdü. Film bittiğinde saat kaç olur?",
                    "Saat ${start + dur}:00",
                    listOf("Saat ${start + dur + 1}:00", "Saat ${start + dur}:30", "Saat ${start + dur - 1}:00"),
                    "$start + $dur = ${start + dur}. Film bittiğinde saat ${start + dur}:00 olur.")
            }
        }
        COZUMLE -> if (chance()) {
            val cms = mutableSetOf<Int>()
            while (cms.size < 3) cms += r(20..99)
            val longest = chance()
            val ans = if (longest) "1 m" else "${cms.min()} cm"
            val all = cms.map { "$it cm" } + "1 m"
            text("Hangisi en ${if (longest) "uzundur" else "kısadır"}?", ans, all.filter { it != ans },
                "1 m = 100 cm olduğunu unutma! Sonra uzunlukları karşılaştırırız.")
        } else {
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
        DEGERLENDIR -> if (chance()) {
            val len = r(8..25)
            val (start, from) = listOf(1 to "1'den", 2 to "2'den", 3 to "3'ten").one()
            text("${NAMES.one()} kitabının boyunu ölçerken cetveli 0'dan değil $from başlattı ve ${len + start} cm okudu. Kitabın gerçek boyu kaç cm?",
                "$len cm", listOf("${len + start} cm", "${len + 2 * start} cm", "${len - 1 - start} cm"),
                "Ölçmeye 0'dan başlamalıyız. ${len + start} − $start = $len cm")
        } else {
            val price = r(1..8) * 5
            val paid = if (price < 20) 20 else 50
            val change = paid - price
            val honest = chance()
            val given = if (honest) change else change + 10
            claim(said("$paid TL verdim, $price TL'lik oyuncak aldım. Kasiyer bana $given TL para üstü verdi, bu doğru."),
                honest, "Para üstü: $paid − $price = $change TL")
        }
        OLUSTUR -> {
            val n = r(3..12) * 5
            build("$n TL'yi 10 TL'lik ve 5 TL'lik paralarla oluştur.", "10 TL", "5 TL", 0..6, 0..12,
                "İki tane 5 TL, bir tane 10 TL eder. Farklı yollarla oluşturabilirsin!",
                render = { a, b -> "10 TL × $a + 5 TL × $b = ${10 * a + 5 * b} TL" },
                check = { a, b -> 10 * a + 5 * b == n })
        }
    }

    private class Chart(val title: String, val items: List<Pair<String, String>>, val counts: List<Int>) {
        val names get() = items.map { it.second }
        fun visual() = "$title\n(Her resim 1 öğrenciyi gösterir.)\n" +
            items.indices.joinToString("\n") { "${items[it].second}: ${items[it].first.repeat(counts[it])}" }
    }

    private fun chart(): Chart {
        val (title, all) = CHARTS.one()
        val items = all.shuffled(random).take(3)
        val counts = (2..9).shuffled(random).take(3)
        return Chart(title, items, counts)
    }

    private fun veri(level: BloomLevel): Question {
        if (level == OLUSTUR) {
            val n = listOf(10, 12, 14, 16).one()
            val d = listOf(2, 4).one()
            return build("Sınıfta $n öğrenci var. Herkes kedi ya da köpek seçti. Köpek seçenler, kedi seçenlerden $d fazla olacak şekilde grafiği oluştur.",
                "🐱 Kedi", "🐶 Köpek", 0..16, 0..16,
                "Toplam $n olmalı ve köpekler kedilerden $d fazla olmalı.",
                render = { a, b -> "Kedi: ${"🐱".repeat(a)} ($a)\nKöpek: ${"🐶".repeat(b)} ($b)\nToplam: ${a + b}" },
                check = { a, b -> a + b == n && b - a == d })
        }
        val c = chart()
        val v = c.visual()
        val i = r(0..2)
        val j = (i + r(1..2)) % 3
        return when (level) {
            HATIRLA -> num("Grafiğe göre “${c.names[i]}” kaç oy almıştır?", c.counts[i],
                "“${c.names[i]}” satırında ${c.counts[i]} resim var.", visual = v, near = c.counts)
            ANLA -> {
                val most = chance()
                val idx = if (most) c.counts.indexOf(c.counts.max()) else c.counts.indexOf(c.counts.min())
                text("En ${if (most) "çok" else "az"} oyu hangisi almıştır?", c.names[idx], c.names,
                    "Satırlardaki resimleri karşılaştırırız: “${c.names[idx]}” satırında ${c.counts[idx]} resim var.", visual = v)
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
            val seq = List(4) { if (up) start + it * s else start - it * s }
            fun rule(step: Int, isUp: Boolean) = "${ritmik(step)} ${if (isUp) "artıyor" else "azalıyor"}"
            text("${seq.joinToString(", ")} örüntüsünün kuralı nedir?", rule(s, up),
                listOf(rule(s, !up)) + listOf(2, 3, 5, 10).filter { it != s }.map { rule(it, up) },
                "Ardışık iki terim arasındaki fark $s. Sayılar ${if (up) "büyüyor" else "küçülüyor"}.")
        }
        UYGULA -> {
            val s = listOf(2, 3, 4, 5, 10).one()
            val start = r(1..10)
            val seq = List(4) { start + it * s }
            val ans = start + 5 * s
            num("${seq.joinToString(", ")}, ... örüntüsünün 6. terimi kaçtır?", ans,
                "5. terim ${start + 4 * s}, 6. terim ${start + 4 * s} + $s = $ans",
                near = listOf(ans - s, ans + s, ans + 1))
        }
        COZUMLE -> {
            val s = listOf(2, 3, 4, 5, 10).one()
            val start = r(1..20)
            val seq = List(5) { start + it * s }
            val hole = r(1..3)
            num("Örüntüdeki eksik sayı kaçtır?\n" + seq.mapIndexed { idx, x -> if (idx == hole) "?" else "$x" }.joinToString(", "),
                seq[hole], "Örüntü ${ritmik(s)} artıyor: ${seq[hole - 1]} + $s = ${seq[hole]}",
                near = listOf(seq[hole] + 1, seq[hole] - 1, seq[hole] + s))
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
            val t = r(12..34)
            build("Kendi sayı örüntünü oluştur! Başlangıç sayısını ve artış miktarını seç. Örüntünün 5. terimi $t olsun.",
                "Başlangıç", "Artış", 0..30, 1..10,
                "5. terim = başlangıç + 4 × artış. Farklı başlangıç ve artışlarla deneyebilirsin!",
                render = { a, b -> List(5) { a + it * b }.joinToString(", ") },
                check = { a, b -> a + 4 * b == t })
        }
    }

    companion object {
        private val NAMES = listOf("Ali", "Elif", "Mert", "Zeynep", "Can", "Ecrin", "Yusuf", "Defne", "Ömer", "Asel")
        private val OBJECTS = listOf("🍎", "🍓", "⚽", "🎈", "🐟", "⭐", "🌸")
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
