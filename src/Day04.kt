data class Game(val picks: Set<Int>, val winning: Set<Int>, val matchCount: Int)

fun parseGame(it: String): Game {
    val (picks, winning) = it.substringAfter(": ").split(" | ").map {
        it.split("   ", "  ", " ")
            .filter{it.contains(Regex("[0-9]"))}
            .map{it.toInt()}.toSet()
    }
    val matchCount = picks.intersect(winning).size
    return Game(picks, winning, matchCount)
}

fun main() {
    val day = "Day04";

    fun part1(input: List<String>): Int {
        return input.map{parseGame(it)}
            .sumOf{
                if (it.matchCount == 0) {
                    0
                } else {
                    1 shl (it.matchCount - 1)
                }
        }
    }

    fun part2(input: List<String>): Int {
        val counts = MutableList(input.size) { i -> 1 }
        input.map{parseGame(it)}.forEachIndexed{i, game ->
            IntRange(1, game.matchCount).forEach{
                counts[i + it] += counts[i]
            }
        }

        return counts.sum()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(13)

    val input = readInput("${day}")
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(30)
    timeAndPrint { part2(input) }

}
