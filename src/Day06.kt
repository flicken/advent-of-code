import Direction.*

fun main() {
    fun printPath(input: List<String>, moves: Set<Move>): String {
        val path = moves.map{it.location}
        val directions = moves.groupBy{it.location}
        val out = mutableListOf<String>()
        out += String(CharArray(input[0].length + 2, { 'x' }))
        input.forEachIndexed { row, line ->
            out += line.mapIndexed { col, c ->
                val location = Location(row, col)
                if (c != '.') {
                    c
                } else if (path.contains(location)) {
                    val d = directions.getValue(location)
                    if (d.size > 1) {
                        "+"
                    } else if (d[0].direction == Up || d[0].direction == Down) {
                        "."
                    } else {
                        "."
                    }
                } else {
                    " "
                }

            }.joinToString("").let { "${row % 10}${it}${row % 10}" }
        }
        out += String(CharArray(input[0].length + 2, { i -> (i % 10).toString().get(0) }))
        return out.joinToString("\n")
    }

    fun findStart(maze: List<String>, startSymbol: Char): Location {
        val row = maze.indexOfFirst { it.contains(startSymbol) }
        val col = maze[row].indexOf(startSymbol)

        return Location(row, col)
    }

    operator fun List<String>.get(location: Location): Char? = this.getOrNull(location.row)?.getOrNull(location.col)

    fun Direction.ninetyRight() = when (this) {
        Up -> Right
        Right -> Down
        Down -> Left
        Left -> Up
    }

    fun List<String>.hasObstacle(location: Location) = this[location] == '#' || this[location] == 'O'
    fun List<String>.inBounds(location: Location) = this[location] !== null

    fun findNextMove(maze: List<String>, move: Move): Move? {
        val possibleMove = move.move()

        if (!maze.inBounds(possibleMove.location)) {
            return null
        }

        return if (maze.hasObstacle(possibleMove.location)) {
            val nextMove = move.move(move.direction.ninetyRight())
            if (maze.hasObstacle(nextMove.location)) {
                move.move(nextMove.direction.ninetyRight())
            } else {
                nextMove
            }
        } else {
            possibleMove
        }
    }

    fun findMoves(maze: List<String>): List<Move> {
        val start = findStart(maze, '^')
        val moves = mutableListOf(Move(start, Up))

        while (true) {
            val move = findNextMove(maze, moves.last())
            if (move === null) {
                return moves
            }
            moves.add(move)
        }
    }

    val day = "Day06"

    fun part1(input: List<String>): Long {
        val locations = findMoves(input).map { it.location }
        return locations.toSet().size.toLong()
    }

    fun countLoops(maze: List<String>): Long {
        var count = 0L
        val start = findStart(maze, '^')

        maze.forEachIndexed{rowNum, row ->
            row.forEachIndexed { colNum, c ->
                if (c != '^' && c != '#') {
                    val moves = mutableListOf(Move(start, Up))
                    val moveSet = moves.toMutableSet();
                    val obstacleLocation = Location(rowNum, colNum)

                    val newMaze = listOf(*maze.toTypedArray()).toMutableList()
                    newMaze[rowNum] = newMaze[rowNum].replaceRange(colNum, colNum  + 1, "O")

                   loop@while (true) {

                       val prevMove = moves.last()
                       val move = if (prevMove.move().location == obstacleLocation) {
                           findNextMove(newMaze, prevMove)
                       } else {
                           findNextMove(newMaze, prevMove)
                       }

                        if (move === null) {
                            break@loop
                        }

                        if (moveSet.contains(move)) {
                            count += 1
                            break@loop
                        }

                        moves.add(move)
                        moveSet.add(move)
                    }
                }
            }
        }

        return count;
    }

    fun part2(input: List<String>): Long {
        return countLoops(input)
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(41L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(6L)
    timeAndPrint { part2(input) }.let { if (it >= 1535) println("too high!") }
}
