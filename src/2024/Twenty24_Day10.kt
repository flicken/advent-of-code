fun main() {
    val day = "Day10"

    operator fun List<String>.get(location: Location): Char? = this.getOrNull(location.row)?.getOrNull(location.col)

    data class SearchState(
        val path: List<Location>
    )

    fun findPathEnds(
        input: List<String>,
        zero: Location,
    ): List<Location> {
        val ends = mutableListOf<Location>()
        val graph = object : Graph<SearchState> {
            override fun neighborsOf(node: SearchState): Iterable<Cost<SearchState>> {
                val next = (input[node.path.first()] ?: '.').inc()
                return Direction.entries.mapNotNull { direction ->
                    val go = node.path.first().go(direction)
                    if (input[go] == next) go else null
                }.map { Cost(node.copy(path = listOf(it) + node.path), 1) }
            }
        }

        graph.search(
            SearchState(listOf(zero)), 9, { v ->
                if (v.path.size == 10) {
                    ends.add(v.path.first())
                }
            },
            { 0 },
            { false }
        )

        return ends
    }

    fun List<String>.getZeroLocations() = this.flatMapIndexed { row, line ->
        line.mapIndexedNotNull { col, c ->
            if (c == '0') Location(row, col) else null
        }
    }

    fun part1(input: List<String>): Long {
        return input.getZeroLocations().map {
            findPathEnds(input, it).toSet().size
        }.sum().toLong()
    }

    fun part2(input: List<String>): Long {
        return input.getZeroLocations().map {
            findPathEnds(input, it).size
        }.sum().toLong()
    }

    part1(readInput("${day}_test0")).assertEqual(2L)
    part1(readInput("${day}_test1")).assertEqual(4L)
    part1(readInput("${day}_test2")).assertEqual(3L)

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(36L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(81L)
    timeAndPrint { part2(input) }
}
