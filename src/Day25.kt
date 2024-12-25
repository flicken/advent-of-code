fun main() {
    val day = "Day25"

    fun part1(input: List<String>): Long {
        val (locks, keys) = input.splitBy { it == "" }
            .filter{it.isNotEmpty()}
            .partition { it[0] == "#####" }
            .let { (locksS, keysS) ->
                locksS.map{
                    it.transpose().map { it.count { it == '#' } - 1}
                } to keysS.map {
                    it.transpose().map { it.count { it == '#' } - 1}
                }
            }

        return locks.sumOf { lock ->
            keys.count { key ->
               lock.zip(key).all { (l, k) -> l + k <= 5}
            }
        }.toLong()
    }

    fun part2(input: List<String>): Long {
        // No part 2, it's Xmas day!
        return -1
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(3L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(-43L)
    timeAndPrint { part2(input) }
}
