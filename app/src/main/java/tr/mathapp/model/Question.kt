package tr.mathapp.model

sealed interface Question {
    val prompt: String
    val visual: String?
    val explanation: String
}

data class ChoiceQuestion(
    override val prompt: String,
    val options: List<String>,
    val answer: String,
    override val explanation: String,
    override val visual: String? = null,
) : Question

/** The child builds an answer with two number steppers; any pair passing [check] is correct. */
class BuildQuestion(
    override val prompt: String,
    val labelA: String,
    val labelB: String,
    val rangeA: IntRange,
    val rangeB: IntRange,
    val render: (Int, Int) -> String,
    val check: (Int, Int) -> Boolean,
    override val explanation: String,
    override val visual: String? = null,
) : Question {
    /** Initial stepper values: the first pair that is not already a solution, so the child has to act. */
    val start: Pair<Int, Int> by lazy {
        for (a in rangeA) for (b in rangeB) if (!check(a, b)) return@lazy a to b
        rangeA.first to rangeB.first
    }

    fun exampleSolution(): Pair<Int, Int>? {
        for (a in rangeA) for (b in rangeB) if (check(a, b)) return a to b
        return null
    }
}

/** Two questions with the same signature look identical to the child. */
fun Question.signature(): String = when (this) {
    is ChoiceQuestion -> "$prompt|$visual|${options.sorted()}"
    is BuildQuestion -> "$prompt|$visual"
}

private const val CLOCK = "clock:"

/** A visual that the UI draws as an analog clock instead of showing as text. */
fun clockVisual(hour: Int, minute: Int) = "$CLOCK$hour:$minute"

fun parseClockVisual(visual: String): Pair<Int, Int>? {
    if (!visual.startsWith(CLOCK)) return null
    val (h, m) = visual.removePrefix(CLOCK).split(":").map { it.toInt() }
    return h to m
}
