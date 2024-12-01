import Direction.Right
import Direction.entries

class ClumsyCrucible(val costs: Map<Location, Int>) {
    val allLocations = costs.keys

    private data class SearchState(
        val pos: Location,
        val dir: Direction,
        val straightLineCount: Int,
    )

    fun find(start: Location, end: Location, withUltraCrucible: Boolean): List<Cost<Location>> {
        val straightRange = if (withUltraCrucible) 4..10 else 1..<4

        val graph = object : Graph<SearchState> {
            override fun neighborsOf(node: SearchState): Iterable<Cost<SearchState>> {
                val possibleDirs = entries.toMutableList()
                
                possibleDirs -= node.dir.reverse()
                if (node.straightLineCount == straightRange.last) {
                    possibleDirs -= node.dir
                }
                if (node.straightLineCount > 0 && node.straightLineCount < straightRange.first) {
                    possibleDirs.removeIf { it != node.dir }
                }
                return possibleDirs.mapNotNull { direction ->
                    val nextPos = node.pos.go(direction)
                    if (nextPos in allLocations)
                        SearchState(
                            pos = nextPos,
                            dir = direction,
                            straightLineCount = if (node.dir == direction) node.straightLineCount + 1 else 1,
                        ) to costs.getValue(nextPos)
                    else null
                }
            }
        }

        val search = graph.search(
            start = SearchState(start, Right, 0),
            goalFunction = { it.pos == end && it.straightLineCount in straightRange },
        )

        return search.path()!!.map { it.pos to search.searchTree[it]!!.second }
    }
}

private fun ClumsyCrucible.solve(withUltraCrucible: Boolean): Int {
    val start = allLocations.minBy { it.row + it.col }
    val end = allLocations.maxBy { it.row + it.col }
    return find(start, end, withUltraCrucible).last().second
}


fun main() {
    val day = "Day17"

    fun parseInput(input: List<String>) = input
        .flatMapIndexed { row, line -> line.mapIndexed { col, n -> Location(row, col) to n.digitToInt() } }
        .let { ClumsyCrucible(it.toMap()) }

    fun part1(input: List<String>): Long {
        return parseInput(input).solve(withUltraCrucible = false).toLong()
    }

    fun part2(input: List<String>): Long {
        return parseInput(input).solve(withUltraCrucible = true).toLong()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(102L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(94)
    timeAndPrint { part2(input) }
}

