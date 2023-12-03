import kotlin.math.max

fun main() {
    val day = "";

    fun part1(input: List<String>): Int {
       return -1;
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val testInput = readInput("${day}_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 0)

    val input = readInput("${day}")
    part1(input).println()
    part2(input).println()
}
