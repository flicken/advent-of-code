fun main() {
    val day = "Day__DAY__"

    fun part1(input: List<String>): Long {
        return -1
    }

    fun part2(input: List<String>): Long {
        return -1
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(-42L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(-43L)
    timeAndPrint { part2(input) }
}
