import java.util.HashSet

fun main() {
    val day = "Day07"

    fun part1(input: List<String>): Long {
        val initialLocation = input[0].indexOf('S')
        var split = 0L

        input.drop(1).fold<String, Set<Int>>(HashSet(listOf(initialLocation)), { acc, line ->
            acc.flatMap { n ->
                when (line[n]) {
                    '.' -> listOf(n)
                    '^' ->{ split += 1; listOf(n - 1, n + 1) }
                    else -> throw Exception("unexpected value: ${line[n]}")
                }
            }.toSet()
        }).size.toLong()

        return split;
    }

    fun part2(input: List<String>): Long {
        val initialLocation = input[0].indexOf('S')

        val size = input[0].length
        val start = MutableList(size, { 0L })
        start[initialLocation] = 1

        return input.drop(1).fold(start, { acc, line ->
            val next = MutableList(size, { 0L })

            acc.forEachIndexed { index, count ->
                when (line[index]) {
                    '.' -> { next[index] += count }
                    '^' -> { next[index - 1] += count; next[index + 1] += count}
                    else -> throw Exception("unexpected value: ${line[index]}")
                }
            }

            next
        }).sum()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(21L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(40L)
    timeAndPrint { part2(input) }
}
