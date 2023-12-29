import Direction.*;

fun main() {
    val day = "Day23"

    data class Tile(val location: Location, val slope: Direction?)
    data class Intersection(val location: Location, val neighbors: Iterable<Cost<Location>>)

    fun slopeOf(c: Char, useSlopes: Boolean): Direction? {
        return if (useSlopes) when (c) {
            '<' -> Left
            '>' -> Right
            '^' -> Up
            'v' -> Down
            else -> null
        } else null
    }

    fun parseInput(input: List<String>, useSlopes: Boolean = true): List<Tile> = input.flatMapIndexed { row, line ->
        line.mapIndexedNotNull { col, c ->
            when (c) {
                '#' -> null
                else -> Tile(Location(row, col), slopeOf(c, useSlopes))
            }
        }
    }


    fun List<Tile>.solve(): Long {
        val trailMap = this.associateBy { it.location }
        val locations = trailMap.keys

        val start = locations.minBy { it.row + it.col }
        val end = locations.maxBy { it.row + it.col }

        val intersections = setOf(start, end) +
                locations.filter { loc ->
                    Direction.entries.map { loc.go(it) }.filter { it in trailMap }.size >= 3
                }

        fun costToNextIntersection(from: Location, towards: Direction): Cost<Location>? {
            tailrec fun doWalk(prev: Location, dir: Direction, cost: Int): Cost<Location>? {
                val loc = prev.go(dir)

                if (loc in intersections) return loc to cost + 1
                if (loc !in trailMap) return null

                val slope = trailMap.getValue(loc).slope
                if (slope != null && slope != dir) return null

                val nextDir = slope ?: dir.allButReverse().find { loc.go(it) in trailMap }
                if (nextDir == null) return null

                return doWalk(loc, nextDir, cost + 1)
            }

            return doWalk(from, towards, 0)
        }

        val intersectionMap = intersections.associateWith { poi ->
            Intersection(poi, entries.mapNotNull { costToNextIntersection(poi, it) })
        }

        val graph = object : Graph<Location> {
            override fun neighborsOf(node: Location): Iterable<Cost<Location>> =
                intersectionMap.getValue(node).neighbors
        }
        
        return graph.dfsMaximize(start) { it == end }.toLong()
    }

    fun part1(input: List<String>): Long {
        return parseInput(input).solve()
    }

    fun part2(input: List<String>): Long {
        return parseInput(input, false).solve()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(94L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(154L)
    timeAndPrint { part2(input) }
}
