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
    check(part1(testInput) == Int.MAX_VALUE)

    val input = readInput("${day}")
    timeAndPrint { part1(input) }

    check(part2(testInput) == Int.MAX_VALUE)
    timeAndPrint { part2(input) }

}
