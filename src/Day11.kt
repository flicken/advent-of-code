fun main() {
    val day = "Day11"

    fun List<String>.countStones(blinks: Int): Long {
        val cache = mutableMapOf<Pair<String, Int>, Long>()

        fun countToEnd(stone: String, blink: Int): Long {
            val key = stone to blink
            return cache.getOrPut(key) {
                if (blink == 0) {
                    1L
                } else if (stone == "0") {
                    countToEnd("1", blink - 1)
                } else if (stone.length % 2 == 0) {
                    val (stone1, stone2) = stone.splitAt(stone.length / 2)
                    countToEnd(stone1, blink - 1) + countToEnd(stone2.toLong().toString(), blink - 1)
                } else {
                    countToEnd((stone.toLong() * 2024L).toString(), blink - 1)
                }
            }
        }

        return this[0].split(" ").sumOf { countToEnd(it, blinks) }
    }

    fun part1(input: List<String>) = input.countStones(25)
    fun part2(input: List<String>) = input.countStones(75)

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(55312L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    timeAndPrint { part2(testInput) }
    timeAndPrint { part2(input) }
}
