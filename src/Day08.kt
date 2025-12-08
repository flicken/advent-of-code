import java.math.BigInteger
import kotlin.math.max

fun main() {
    data class Location3D(val x: Int, val y: Int, val z: Int) {
        override fun toString(): String = "$x,$y,$z"
    }

    fun Location3D.dist(b: Location3D): BigInteger =
        (this.x - b.x).toBigInteger().let { dx ->
            (this.y - b.y).toBigInteger().let { dy ->
                (this.z - b.z).toBigInteger().let { dz ->
                    dx*dx + dy*dy + dz*dz
                }
            }
        }

    val day = "Day08"


    fun allPairsByShortestDistance(points: List<Location3D>): List<Pair<Location3D, Location3D>> {
        return points.allPairs().map{ Triple(it.first, it.second, it.first.dist(it.second)) }
            .toList()
            .sortedBy { it.third }
            .map{ it.first to it.second}
    }

    class DisjointSet<T> {
        private val parent = mutableMapOf<T, T>()
        private val size = mutableMapOf<T, Int>()
        private var maxSize = 0


        private fun find(a: T): T {
            if (a !in parent) {
                parent[a] = a
                size[a] = 1
            }

            var p = parent[a]!!
            if (p != a) {
                p = find(p)!!
                parent[a] = p
            }
            return p
        }

        fun union(a: T, b: T) {
            val rootA = find(a)
            val rootB = find(b)
            if (rootA == rootB) return

            val sizeA = size[rootA]!!
            val sizeB = size[rootB]!!

            // attach smaller tree to larger for quicker access later
            if (sizeA < sizeB) {
                merge(rootA, rootB, sizeA, sizeB)
            } else {
                merge(rootB, rootA, sizeA, sizeB)
            }
        }

        private fun merge(childRoot: T, parentRoot: T, sizeA: Int, sizeB: Int) {
            parent[childRoot] = parentRoot
            val groupSize = sizeA + sizeB
            size[parentRoot] = groupSize
            maxSize = max(maxSize, groupSize)
        }

        fun connected(a: T, b: T): Boolean = find(a) == find(b)

        fun groups(): List<List<T>> {
            val result = mutableMapOf<T, MutableList<T>>()
            for (x in parent.keys) {
                val root = find(x)
                result.getOrPut(root) { mutableListOf() }.add(x)
            }
            return result.values.toList()
        }

        fun maxSize(): Int = maxSize
    }


    fun part1(input: List<String>, connectionCount: Int): Long {
        val boxes = input.map{it.split(',').map{it.toInt()}}.map{Location3D(it[0], it[1], it[2])}

        /*
        Given list of closest pairs in order
          1. Add to set together
          2. Check if either is in pre-existing set, if so then combine sets
        Repeat for first "connectionCount" pairs
         */
        val disjointSet = DisjointSet<Location3D>()

        val pairs = allPairsByShortestDistance(boxes)

        pairs.take(connectionCount).forEach { pair ->
            disjointSet.union(pair.first, pair.second)
        }

        return disjointSet.groups().sortedByDescending { it.size }.take(3).fold(1) { acc, n -> acc * n.size }
            .toLong()
    }

    fun part2(input: List<String>): Long {
        val boxes = input.map{it.split(',').map{it.toInt()}}.map{Location3D(it[0], it[1], it[2])}

        val disjointSet = DisjointSet<Location3D>()

        val pairs = allPairsByShortestDistance(boxes)
        val pairsIt = pairs.iterator()
        var pair: Pair<Location3D, Location3D>

        do {
            pair = pairsIt.next()
            disjointSet.union(pair.first, pair.second)
        } while (disjointSet.maxSize() < boxes.size)

        return (pair.first.x.toLong() * pair.second.x.toLong())
    }

    val testInput = readInput("${day}_test")
    part1(testInput, 10).assertEqual(40L)

    val input = readInput(day)
    timeAndPrint { part1(input, 1000) }

    part2(testInput).assertEqual(25272L)
    timeAndPrint { part2(input) }
}
