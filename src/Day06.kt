fun main() {
    val day = "Day06"

    fun List<String>.toRaces(): List<Pair<Long, Long>> {
        return this.map {
            it.substringAfter(":")
                .trim()
                .split(" ")
                .filter { it.isNotEmpty() }
                .map { it.trim().toLong() }
        }.let { (time, distance) ->
            time.zip(distance)
        }
    }

    fun part1(input: List<String>): Int {
        return input.toRaces().map { (time, recordDistance) ->
            (1..time).count {
                val distance = it * (time - it)
                distance > recordDistance
            }
        }.reduce { a, b -> a * b }
    }

    fun part2(input: List<String>): Int {
        return part1(input.map { it.replace(" ", "") })
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(288)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(71503)
    timeAndPrint { part2(input) }
}
