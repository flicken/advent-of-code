
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

    data class Cheat(val start: Location, val end: Location)

    fun part1(input: List<String>, minSavings: Long): Long {
        val start = input.findLocationOrThrow('S')
        val end = input.findLocationOrThrow('E')

        val path = input.shortestPath(start, end)

        val locationToTime = path.mapIndexed { index, location ->  location to index}.toMap()

        val pathSet = path.toSet()
        val cheats = path.flatMap { l ->
            Direction.entries.map{l.go(it).go(it)}.filter {
                pathSet.contains(it)
            }.map{ end ->
                Cost(Cheat(l, end), locationToTime.getValue(end) - locationToTime.getValue(l) - 2)
            }
        }.filter{it.second >= minSavings}

        return cheats.size.toLong()
    }

    fun part2(input: List<String>, minSavings: Int): Long {
        val start = input.findLocationOrThrow('S')
        val end = input.findLocationOrThrow('E')

        val path = input.shortestPath(start, end)

        val locationToTime = path.mapIndexed { index, location ->  location to index}.toMap()

        val cheats = path.flatMap { cheatStart ->
            path.mapNotNull { cheatEnd ->
                val cheatTime = Math.abs(cheatStart.row - cheatEnd.row) + Math.abs(cheatStart.col - cheatEnd.col)
                if (cheatTime <= 20)
                    Cost(Cheat(cheatStart, cheatEnd), locationToTime.getValue(cheatEnd) - locationToTime.getValue(cheatStart) - cheatTime)
                else
                    null
            }
        }.filter{it.second >= minSavings}

        return cheats.size.toLong()
    }

    val testInput = readInput("${day}_test")
    part1(testInput, 12).assertEqual(8L)

    val input = readInput(day)
    timeAndPrint { part1(input, 100) }

    part2(testInput, 50).assertEqual(285L)
    timeAndPrint { part2(input, 100) }
}
