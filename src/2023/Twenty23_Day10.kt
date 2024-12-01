import Direction.*

data class Location(val row: Int, val col: Int) {
    fun go(direction: Direction) = when (direction) {
        Up -> this.copy(row = this.row - 1)
        Down -> this.copy(row = this.row + 1)
        Left -> this.copy(col = this.col - 1)
        Right -> this.copy(col = this.col + 1)
    }
}

enum class Direction {
    Up, Down, Left, Right
}

fun Direction.allButReverse() = Direction.entries - reverse()


val letterToDirection = mapOf(
    'R' to Right,
    'L' to Left,
    'U' to Up,
    'D' to Down
)
val directionToLetter = letterToDirection.map { (k, v) ->
    v to k
}.toMap()

fun Direction.reverse() = when (this) {
    Up -> Down
    Down -> Up
    Left -> Right
    Right -> Left
}

data class Move(val location: Location, val direction: Direction) {
    fun move(d: Direction) = this.copy(this.location.go(d), d)
}

fun main() {
    val day = "Day10"

    fun printPath(input: List<String>, path: Set<Location>, outside: Set<Location>? = null) {
        println(String(CharArray(input[0].length + 2, { 'x' })))
        input.forEachIndexed { row, line ->
            line.mapIndexed { col, c ->
                val location = Location(row, col)
                if (path.contains(location)) {
                    c
                } else {
                    if (outside == null || outside.contains(location)) " " else '.'
                }

            }.joinToString("").let { "${row % 10}${it}${row % 10}" }.println()
        }
        println(String(CharArray(input[0].length + 2, { i -> (i % 10).toString().get(0) })))
    }

    fun findStart(maze: List<String>): Location {
        val row = maze.indexOfFirst { it.contains('S') }
        val col = maze[row].indexOf('S')

        return Location(row, col)
    }

    operator fun List<String>.get(location: Location): Char = this[location.row][location.col]

    fun findNextMove(maze: List<String>, move: Move, startSymbol: Char): Move {
        val cameVia = move.direction
        return when (maze[move.location]) {
            'S' -> if (startSymbol == '7') move.move(Down) else move.move(Right) // handles all puzzle inputs
            'L' -> if (cameVia == Down) move.move(Right) else move.move(Up)
            '7' -> if (cameVia == Right) move.move(Down) else move.move(Left)
            'J' -> if (cameVia == Right) move.move(Up) else move.move(Left)
            '-' -> if (cameVia == Right) move.move(Right) else move.move(Left)
            '|' -> if (cameVia == Down) move.move(Down) else move.move(Up)
            'F' -> if (cameVia == Up) move.move(Right) else move.move(Down)
            else -> throw Exception("Cannot find next location at ${move.location}: ${maze[move.location]}")
        }
    }

    fun findMoves(maze: List<String>, startSymbol: Char = 'F'): List<Move> {
        val start = findStart(maze)
        val moves = mutableListOf(Move(start, Right))

        while (true) {
            val next = findNextMove(maze, moves.last(), startSymbol)
            if (next.location == start) {
                break
            }
            moves.add(next)
        }
        return moves
    }

    fun part1(input: List<String>): Long {
        val path = findMoves(input)
        printPath(input, path.map { it.location }.toSet())

        return (path.size / 2).toLong()
    }

    fun part2(maze: List<String>, startSymbol: Char = 'F'): Long {
        val pathSet = findMoves(maze, startSymbol).map { it.location }.toSet()

        val insides = mutableListOf<Location>()
        val outsides = mutableSetOf<Location>()
        maze.forEachIndexed { row, line ->
            var crossings = 0
            line.forEachIndexed { col, c ->
                val l = Location(row, col)
                if (pathSet.contains(l)) {
                    if ("|".contains(maze[l])) {
                        crossings += 2
                    }
                    if ("LFJ7S".contains(maze[l])) {
                        crossings += 1
                    }
                } else {
                    // Use raycasting inside outside test
                    // See https://iq.opengenus.org/inside-outside-test/
                    val crossings = line.substring(0, col)
                        .mapIndexed<Any> { cl, it ->
                            val el = Location(row, cl)
                            if (outsides.contains(el) || insides.contains(el)) " " else it
                        }
                        .joinToString("")
                        .replace("S", startSymbol.toString())
                        .replace("[-. ]".toRegex(), "")
                        .replace("FJ|L7".toRegex(), "|")
                        .replace("F7|LJ".toRegex(), "||")
                    if (crossings.length % 2 == 0) {
                        outsides.add(l)
                    } else {
                        insides.add(l)
                    }
                }
            }
        }

        printPath(maze, findMoves(maze).map { it.location }.toSet(), outsides)
        return insides.size.toLong()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(4L)

    println("Starting part 1")
    val input = readInput(day)
    timeAndPrint { part1(input) }

    println("Part 2 test")
    part2(testInput).assertEqual(1L)
    println("Part 2 2")
    part2(readInput("Day10_test2")).assertEqual(4L)
    println("Part 2 2b")
    part2(readInput("Day10_test2b")).assertEqual(4L)
    println("Part 2 3")
    part2(readInput("Day10_test3")).assertEqual(8L)
    println("Part 2 4")
    part2(readInput("Day10_test4"), '7').assertEqual(10L)
    timeAndPrint { part2(input, 'L') }
}
