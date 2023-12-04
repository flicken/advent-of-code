fun main() {
    val day = ""

    fun part1(input: List<String>): Int {
        return -1
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(0)

    val input = readInput("${day}")
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(0)
    timeAndPrint { part2(input) }
}
