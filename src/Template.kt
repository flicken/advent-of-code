fun main() {
    val day = "Day___DAY__"

    fun part1(input: List<String>): Int {
        return -1
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(-42)

    val input = readInput("${day}")
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(-43)
    timeAndPrint { part2(input) }
}
