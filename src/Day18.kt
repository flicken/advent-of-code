import Direction.Down
import Direction.Up

fun main() {
    val day = "Day18"

    operator fun <T> Array<Array<T>>.set(location: Location, value: T) {
        this[location.row][location.col] = value
    }

    operator fun <T> Array<Array<T>>.get(location: Location) = this[location.row][location.col]

    fun <T> Array<Array<T>>.toStringMaze(block: (Location, T) -> Char): List<String> {
        return this.mapIndexed { i, row ->
            row.mapIndexed { j, col ->
                block(Location(i, j), col)
            }.joinToString("")
        }
    }

    fun part1(input: List<String>): Long {
        val maze = Array(500, { Array(500, { 0 }) })

        val locations = mutableListOf(Location(maze.size / 2, maze[0].size / 2))

        input.forEach { line ->
            val (directionS, countS, colorS) = line.split(" ")
            val direction = letterToDirection.getValue(directionS.first())
            val count = countS.toInt()
            val color = colorS.replace("[#()]".toRegex(), "").toInt(16)
            (1..count).forEach {
                val next = locations.last().go(direction)
                if (maze[next] != 0) {
                    throw Exception("Already have something at ${next}")
                }
                maze[next] = color
                locations.add(next)
            }
        }
        println("Location count ${locations.size}")
        println("Min ${locations.minBy { it.row }} ${locations.minBy { it.col }}")
        println("Max ${locations.maxBy { it.row }} ${locations.maxBy { it.col }}")

//        val first = locations.first()
//        maze.toStringMaze { location, v ->
//            if (v > 0) {
//                if (location == first) 'S' else '#'
//            } else ' '
//        }.joinToString("\n").println()

        val locationSet = locations.toSet()
        val insides = mutableListOf<Location>()
        val outsides = mutableSetOf<Location>()
        maze.forEachIndexed { row, line ->
            var above = 0
            var below = 0
            line.forEachIndexed { col, c ->
                val l = Location(row, col)
                if (locationSet.contains(l)) {
                    if (locationSet.contains(l.go(Up))) {
                        above += 1
                    }
                    if (locationSet.contains(l.go(Down))) {
                        below += 1
                    }
                } else {
                    // Use raycasting inside outside test
                    // See https://iq.opengenus.org/inside-outside-test/
                    if (above % 2 == 0 || below % 2 == 0) {
                        outsides.add(l)
                    } else {
                        insides.add(l)
                    }
                }
            }
        }

//        maze.toStringMaze { location, v ->
//            if (locationSet.contains(location)) {
//                '#'
//            } else if (insides.contains(location)) {
//                '.'
//            } else {
//                ' '
//            }
//        }.joinToString("\n").println()

        return (insides.toSet() + locations).size.toLong()
    }

    fun part2(input: List<String>): Long {
        return -1
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(62L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(952408144115)
    timeAndPrint { part2(input) }
}
