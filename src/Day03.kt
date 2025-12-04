import kotlin.streams.toList

fun main() {
    val day = "Day03"

    fun digits(chars: List<Char>, choose: Int): List<Char> {
        if (choose <= 0) {
            return listOf();
        }
        val maxChar = chars.dropLast(choose - 1).max()
        return listOf(maxChar) + digits(chars.dropWhile { it < maxChar }.drop(1), choose - 1)
    }

    fun part1(input: List<String>): Long {
        return input.sumOf {
            digits(it.toList(), 2).joinToString("").toLong()
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf {
            digits(it.toList(), 12).joinToString("").toLong()
        }
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(357L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(3121910778619L)
    timeAndPrint { part2(input) }
}
