import kotlin.math.abs

fun main() {
    val day = "Day20"

    operator fun List<String>.get(location: Location): Char? = this.getOrNull(location.row)?.getOrNull(location.col)

    fun List<String>.inBounds(location: Location) = this[location] !== null
    fun List<String>.show(paths: Set<Location>): String =
        this.mapIndexed { row, line ->
            line.mapIndexed { col, c ->
                if (Location(row, col) in paths) '.' else c
            }.joinToString("")
        }.joinToString("\n")

    fun List<String>.shortestPath(start: Location, end: Location): List<Location> {
        val maze = this
        val graph = object : Graph<Location> {
            override fun neighborsOf(node: Location): Iterable<Cost<Location>> {
                return Direction.entries
                    .mapNotNull {
                    val next = node.go(it)
                    if (maze.inBounds(next) && maze[next] != '#') {
                        Cost(next, 1)
                    } else {
                        null
                    }
                }
            }
        }

        val result = graph.search(start, goalFunction = {it == end})

        return result.path()!!.path
    }

    fun countCheats(input: List<String>, minSavings: Int, maxCheatLength: Int): Long {
        val path = input.shortestPath(input.findLocationOrThrow('S'), input.findLocationOrThrow('E'))

        val locationToTime = path.mapIndexed { index, location -> location to index }.toMap()

        val count = path.indices.sumOf { i ->
            ((i + 1)..<path.size).count { j ->
                val a = path[i]
                val b = path[j]

                val cheatLength = abs(a.row - b.row) + Math.abs(a.col - b.col)

                (maxCheatLength >= cheatLength &&
                        minSavings <= abs(locationToTime.getValue(a) - locationToTime.getValue(b)) - cheatLength)
            }
        }

        return count.toLong()
    }

    fun part1(input: List<String>, minSavings: Int): Long {
        return countCheats(input, minSavings, 2)
    }

    fun part2(input: List<String>, minSavings: Int): Long {
        return countCheats(input, minSavings, 20)
    }

    val testInput = readInput("${day}_test")
    part1(testInput, 12).assertEqual(8L)

    val input = readInput(day)
    timeAndPrint { part1(input, 100) }

    part2(testInput, 50).assertEqual(285L)
    timeAndPrint { part2(input, 100) }
}
