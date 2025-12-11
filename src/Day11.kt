fun main() {
    val day = "Day11"

    fun parseGraph(input: List<String>): Graph<String> {
        val connections = input.map { line ->
            line.split(": ").let { line ->
                val from = line.first()
                val rest = line.drop(1)[0].split(" ")
                from to rest
            }
        }.toMap()

        return object : Graph<String> {
            override fun neighborsOf(node: String): Iterable<Cost<String>> =
                (connections[node] ?: listOf()).map { Cost(it, 1) }

        }
    }

    fun part1(input: List<String>): Long {
        return parseGraph(input).countPaths("you", "out")
    }

    fun part2(input: List<String>): Long {
        val graph = parseGraph(input)

        return listOf(
            graph.countPaths("svr", "fft"),
            graph.countPaths("fft", "dac"),
            graph.countPaths("dac", "out"),
        ).fold(1L) { acc, n -> acc * n }
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(5L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(readInput("${day}_test2")).assertEqual(2L)

    val result = timeAndPrint { part2(input) }
    if (result >= 91527708011709886L) {
        throw Exception("Too high ${result}")
    }
}
