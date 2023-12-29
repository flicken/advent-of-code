import java.util.*

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
