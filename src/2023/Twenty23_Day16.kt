import Direction.*
import java.util.*
import kotlin.math.max

fun main() {
    val day = "Day16"

    operator fun List<String>.get(location: Location): Char = this[location.row][location.col]

    fun Location.inBounds(input: List<String>): Boolean =
        row >= 0 && col >= 0 && row < input.size && col < input[0].length

    fun computeTilesEnergized(input: List<String>, initialMove: Move): Long {
        val visited = mutableMapOf<Location, MutableList<Direction>>()
        val queue = ArrayDeque<Move>()
        queue.add(initialMove)
        while (queue.isNotEmpty()) {
            val move = queue.remove()
            if (!move.location.inBounds(input)) {
                continue
            }

            val prevDirections = visited.getOrPut(move.location) { mutableListOf() }
            if (!prevDirections.contains(move.direction)) {
                prevDirections.add(move.direction)

                when (input[move.location]) {
                    '.' -> queue.add(move.move(move.direction))
                    '/' -> queue.add(
                        move.move(
                            when (move.direction) {
                                Right -> Up
                                Left -> Down
                                Down -> Left
                                Up -> Right
                            }
                        )
                    )

                    '\\' -> queue.add(
                        move.move(
                            when (move.direction) {
                                Right -> Down
                                Left -> Up
                                Down -> Right
                                Up -> Left
                            }
                        )
                    )

                    '-' -> {
                        when (move.direction) {
                            Right, Left -> queue.add(move.move(move.direction))
                            Up, Down -> {
                                queue.add(move.move(Left))
                                queue.add(move.move(Right))
                            }
                        }
                    }

                    '|' -> {
                        when (move.direction) {
                            Right, Left -> {
                                queue.add(move.move(Up))
                                queue.add(move.move(Down))
                            }

                            Up, Down -> queue.add(move.move(move.direction))
                        }
                    }

                    else -> throw Exception("Unknown character ${input[move.location]} at ${move.location}")
                }
            }
        }

        return visited.size.toLong()
    }

    fun part1(input: List<String>): Long {
        return computeTilesEnergized(input, Move(Location(0, 0), Right))
    }

    fun part2(input: List<String>): Long {
        val maxCol = input[0].length - 1
        return max(input.indices.maxOf { row ->
            listOf(
                computeTilesEnergized(input, Move(Location(row, 0), Right)),
                computeTilesEnergized(input, Move(Location(row, maxCol), Left)),
            ).max()
        }, input[0].indices.maxOf { col ->
            listOf(
                computeTilesEnergized(input, Move(Location(0, col), Down)),
                computeTilesEnergized(input, Move(Location(input.size - 1, col), Up)),
            ).max()
        })
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(46L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(51L)
    timeAndPrint { part2(input) }
}
