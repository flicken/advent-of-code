import Direction.*

fun main() {
    val day = "Day16"

    data class SearchState(val location: Location, val direction: Direction)

    operator fun List<String>.get(location: Location): Char = this[location.row][location.col]
    fun Direction.validDirections(): List<Direction> = listOf(turn(Right), turn(Left))

    fun part1(input: List<String>): Long {
        val start = input.findLocationOrThrow('S')
        val end = input.findLocationOrThrow('E')

        val graph = object : Graph<SearchState> {
            override fun neighborsOf(node: SearchState): Iterable<Cost<SearchState>> {
                val nextLocation = node.location.go(node.direction)
                val turns = node.direction.validDirections().map{Cost(node.copy(direction = it), 1000)}
                return if (input[nextLocation] != '#') {
                    listOf(Cost(node.copy(location = nextLocation), 1)) + turns
                } else {
                    turns
                }
            }
        }

        val result = graph.search(SearchState(start, Right),
            goalFunction = { it.location == end })

        return result.path()?.cost?.toLong() ?: throw IllegalStateException("Cannot find end")
    }


    fun List<String>.show(paths: Set<Location>): String =
        this.mapIndexed { row, line ->
            line.mapIndexed { col, c ->
                if (Location(row, col) in paths) 'O' else c
            }.joinToString("")
        }.joinToString("\n")


    fun part2(input: List<String>): Long {
        val start = input.findLocationOrThrow('S')
        val end = input.findLocationOrThrow('E')

        val graph = object : Graph<SearchState> {
            override fun neighborsOf(node: SearchState): Iterable<Cost<SearchState>> {
                val nextLocation = node.location.go(node.direction)
                val turns = node.direction.validDirections().map{Cost(node.copy(direction = it), 1000)}
                return if (input[nextLocation] != '#') {
                    listOf(Cost(node.copy(location = nextLocation), 1)) + turns
                } else {
                    turns
                }
            }
        }

        val shortestPath = graph.search(SearchState(start, Right),
            goalFunction = { it.location == end })

        val locations = graph.findAllPaths(shortestPath).map{it.location}

        return locations.toSet().size.toLong()
    }

    val testInput = readInput("${day}_test")
    val testInput2 = readInput("${day}_test2")
    part1(testInput).assertEqual(7036L)
    part1(testInput2).assertEqual(11048L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(45L)
    part2(testInput2).assertEqual(64L)
    timeAndPrint { part2(input) }
}
