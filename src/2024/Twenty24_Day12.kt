fun main() {
    val day = "Day12"

    data class SearchState(
        val c: Char,
        val toSearch: List<Location>,
        val searched: Set<Location>,
    ) {
        fun next(
            nextLocations: List<Location>,
        ): SearchState = copy(
            toSearch = toSearch.drop(1) + nextLocations,
            searched = searched + toSearch.first(),
        )

        fun calcPerimeter(): Long =
            searched.sumOf { l -> Direction.entries.count { !searched.contains(l.go(it)) } }.toLong()

        fun calcSides(): Long {
            return searched.sumOf { l -> Direction.entries.count { isCorner(l, it) } }.toLong()
        }

        fun isCorner(l: Location, direction: Direction): Boolean {
            val left = searched.contains(l.go(direction))
            val right = searched.contains(l.go(direction.turn(Direction.Right)))
            return (!left && !right) || (left && right && !searched.contains(
                l.go(direction).go(direction.turn(Direction.Right))
            ))
        }
    }

    operator fun List<String>.get(location: Location): Char? = this.getOrNull(location.row)?.getOrNull(location.col)

    fun List<String>.findRegions(): MutableList<SearchState> {
        val input = this
        val seen = mutableSetOf<Location>()
        val regions = mutableListOf<SearchState>()

        val graph = object : Graph<SearchState> {
            override fun neighborsOf(node: SearchState): Iterable<Cost<SearchState>> {
                val next = node.toSearch.first()
                val nextLocations = Direction.entries.map { next.go(it) }.filter {
                    !seen.contains(it) && input[it] == node.c
                }

                if (nextLocations.size == 0 && node.toSearch.size == 1) {
                    regions += node.next(listOf())
                    return listOf()
                }

                seen += nextLocations

                return listOf(Cost(node.next(nextLocations), 1))
            }
        }

        input.allLocations().forEach { location ->
            if (!seen.contains(location)) {
                val c = input[location] ?: throw NullPointerException()

                graph.search(
                    SearchState(c, listOf(location), setOf()), 1000,
                    { },
                    { 0 },
                    { false }
                )
            }
        }
        return regions
    }

    fun part1(input: List<String>) = input.findRegions().sumOf { it.searched.size * it.calcPerimeter() }

    fun part2(input: List<String>) =  input.findRegions().sumOf { it.searched.size * it.calcSides() }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(140L)
    val testInputb = readInput("${day}_testb")
    part1(testInputb).assertEqual(1930L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(80L)
    part2(readInput("${day}_teste")).assertEqual(236L)
    part2(testInputb).assertEqual(1206L)
    val result = timeAndPrint { part2(input) }
    if (result <= 5930) {
        println("Too low")
    }
}
