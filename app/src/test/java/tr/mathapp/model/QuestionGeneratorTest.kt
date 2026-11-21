package tr.mathapp.model

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionGeneratorTest {

    private val generator = QuestionGenerator(Random(42))

    @Test
    fun everyActivityProducesValidQuestions() {
        for (topic in Topic.entries) for (level in BloomLevel.entries) {
            repeat(500) {
                val q = generator.generate(topic, level)
                val where = "$topic/$level: ${q.prompt}"
                assertTrue("empty prompt $where", q.prompt.isNotBlank())
                assertTrue("empty explanation $where", q.explanation.isNotBlank())
                when (q) {
                    is ChoiceQuestion -> {
                        assertTrue("answer missing $where ${q.options}", q.answer in q.options)
                        assertEquals("duplicate options $where ${q.options}", q.options.size, q.options.toSet().size)
                        assertTrue("option count $where ${q.options}", q.options.size in 2..4)
                        assertTrue("negative option $where ${q.options}", q.options.none { it.startsWith("-") })
                    }
                    is BuildQuestion -> {
                        assertNotNull("no solution $where", q.exampleSolution())
                        assertTrue("start is already a solution $where", !q.check(q.start.first, q.start.second))
                    }
                }
            }
        }
    }

    @Test
    fun quizzesHaveFiveDistinctQuestions() {
        for (topic in Topic.entries) for (level in BloomLevel.entries) {
            repeat(50) {
                val quiz = generator.quiz(topic, level)
                assertEquals(5, quiz.size)
                assertEquals("$topic/$level repeats a question", 5, quiz.map { it.signature() }.toSet().size)
            }
        }
    }

    @Test
    fun staysWithinGradeTwoProgramme() {
        val division = Regex("(\\d+) ÷ (\\d+)")
        val multiplication = Regex("(\\d+) × (\\d+)")
        for (topic in Topic.entries) for (level in BloomLevel.entries) {
            repeat(500) {
                val q = generator.generate(topic, level)
                val shown = listOfNotNull(q.prompt, q.visual?.takeIf { parseClockVisual(it) == null }) +
                    (if (q is ChoiceQuestion) q.options else emptyList())
                val all = shown + q.explanation
                val where = "$topic/$level: ${q.prompt}"
                shown.forEach { text ->
                    Regex("\\d+").findAll(text).forEach { assertTrue("number over 100 in $where: $text", it.value.toInt() <= 100) }
                }
                all.forEach { text ->
                    listOf("tek sayı", "çift sayı", "koni").forEach { word ->
                        assertTrue("grade-3 or out-of-programme content '$word' in $where", !text.lowercase().contains(word))
                    }
                    division.findAll(text).forEach {
                        val (dividend, divisor) = it.destructured
                        assertTrue("division beyond 20 ÷ 2..5 in $where: $text", dividend.toInt() <= 20 && divisor.toInt() in 2..5)
                    }
                    multiplication.findAll(text).forEach {
                        val (x, y) = it.destructured.toList().map(String::toInt)
                        assertTrue("multiplication beyond 1..5 × 0..10 in $where: $text", min(x, y) <= 5 && max(x, y) <= 10)
                    }
                }
            }
        }
    }

    @Test
    fun turkishNumberNames() {
        assertEquals("kırk yedi", QuestionGenerator.oku(47))
        assertEquals("on", QuestionGenerator.oku(10))
        assertEquals("doksan dokuz", QuestionGenerator.oku(99))
    }
}
