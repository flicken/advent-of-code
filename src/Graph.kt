import java.util.*
import Direction.*

typealias Cost<T> = Pair<T, Int>

// From https://github.com/Jadarma/advent-of-code-kotlin-solutions
interface Graph<T : Any> {
    fun neighborsOf(node: T): Iterable<Cost<T>>
}

data class SearchResult<T>(
    val start: T,
    val destination: T?,
    val searchTree: Map<T, Cost<T>>,
)

data class SearchPath<T>(
    val path: List<T>,
    val cost: Int,
) : List<T> by path


fun <T : Any> SearchResult<T>.path(): SearchPath<T>? = when (destination) {
    null -> null
    else -> pathTo(destination)
}

fun <T : Any> SearchResult<T>.pathTo(node: T): SearchPath<T>? {
    val cost = searchTree[node]?.second ?: return null
    val path = buildList {
        var current = node
        while (true) {
            add(current)
            val previous = searchTree.getValue(current).first
            if (previous == current) break
            current = previous
        }
    }.asReversed()
    return SearchPath(path, cost)
}

fun <T : Any> Graph<T>.search(
    start: T,
    maximumCost: Int = Int.MAX_VALUE,
    onVisited: (T) -> Unit = {},
    heuristic: (T) -> Int = { 0 },
    goalFunction: (T) -> Boolean = { false },
): SearchResult<T> {
    val queue = PriorityQueue(compareBy<Cost<T>> { it.second })
    queue.add(start to 0)
    val searchTree = mutableMapOf(start to (start to 0))

    while (true) {
        val (node, costSoFar) = queue.poll() ?: return SearchResult(start, null, searchTree)
        onVisited(node)

        if (goalFunction(node)) return SearchResult(start, node, searchTree)

        neighborsOf(node)
            .filter { it.first !in searchTree }
            .forEach { (next, cost) ->
                val nextCost = costSoFar + cost
                if (nextCost <= maximumCost && nextCost <= (searchTree[next]?.second ?: Int.MAX_VALUE)) {
                    queue.add(next to heuristic(next).plus(nextCost))
                    searchTree[next] = node to nextCost
                }
            }
    }
}

fun <T : Any> Graph<T>.dfsMaximize(
    start: T,
    onVisited: (T) -> Unit = {},
    goalFunction: (T) -> Boolean = { false },
): Int {
    val route = mutableSetOf(start)

    fun maximize(point: T): Int {
        var maxCost = Int.MIN_VALUE
        onVisited(point)

        if (goalFunction(point)) return 0

        for (next in neighborsOf(point)) {
            val (node, cost) = next

            if (!route.add(node)) continue

            maxCost = maxOf(maxCost, maximize(node) + cost)
            route.remove(node)
        }

        return maxCost
    }

    return maximize(start)
}

fun List<String>.allLocations(): List<Location> = this.flatMapIndexed { row, line ->
    line.mapIndexed { col, c -> Location(row, col) }
}

data class LocationL(val row: Long, val col: Long) {
    fun go(direction: Direction) = when (direction) {
        Up -> this.copy(row = this.row - 1)
        Down -> this.copy(row = this.row + 1)
        Left -> this.copy(col = this.col - 1)
        Right -> this.copy(col = this.col + 1)
    }
}

operator fun LocationL.plus(b: LocationL) = LocationL(this.row + b.row, this.col + b.col)
operator fun LocationL.times(m: Long): LocationL = LocationL(this.row * m, this.col * m)

fun Iterable<String>.findLocationOrThrow(char: Char): Location {
    this.forEachIndexed { row, line ->
        line.forEachIndexed { col, c ->
            if (c == char) {
                return Location(row, col)
            }
        }
    }
    throw IllegalStateException("Cannot find ${char}")
}
