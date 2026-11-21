package tr.mathapp.model

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
    fun turkishNumberNames() {
        assertEquals("kırk yedi", QuestionGenerator.oku(47))
        assertEquals("on", QuestionGenerator.oku(10))
        assertEquals("doksan dokuz", QuestionGenerator.oku(99))
    }
}
