import kotlin.math.max
import kotlin.math.min

fun lcm(a: Long, b: Long): Long {
    val larger = max(a, b)
    val smaller = min(a, b)
    var lcm = larger
    while (lcm % smaller != 0L) {
        lcm += larger
    }
    return lcm
}

fun Iterable<Long>.lcm(): Long {
    return this.reduce { a, b -> lcm(a, b) }
}

fun main() {
    data class Maze(val paths: Map<String, Pair<String, String>>, val moves: CharArray, val names: Array<String>)

    val day = "Day08"

    fun nextMove(location: String, step: Long, maze: Maze): String {
        val next = maze.paths[location] ?: throw Exception("Cannot find ${location}");
        val movePosition = (step % maze.moves.size).toInt()
        return when (maze.moves[movePosition]) {
            'L' -> next.first
            else -> next.second
        }
    }

    fun countMoves(
        from: String,
        to: String,
        maze: Maze,
        stepStart: Long = 0
    ): Long {
        tailrec fun iter(location: String, step: Long): Long {
            if (location == to) {
                return step;
            } else {
                return iter(nextMove(location, step, maze), step + 1)
            }
        }

        return iter(from, stepStart);
    }

    fun parseMovesAndPaths(input: List<String>): Maze {
        val moves = input.first().toCharArray()

        val paths = input.drop(2).map {
            val (name, left, right) = "[^A-Z0-9]+".toRegex().split(it)
            name to Pair(left, right)
        }

        return Maze(paths.toMap(), moves, paths.map { it.first }.toTypedArray())
    }

    fun part1(input: List<String>): Long {
        val maze = parseMovesAndPaths(input)

        return countMoves("AAA", "ZZZ", maze)
    }


    fun findCycles(from: String, to: String, maze: Maze): List<Long> {
        val starts = maze.names.filter { it.endsWith(from) }
        val ends = maze.names.filter { it.endsWith(to) }

        val foundStarts = mutableMapOf<String, Pair<String, Long>>()
        tailrec fun iterStarts(locations: List<String>, step: Long) {
            locations.forEachIndexed<String> { i, e ->
                if (ends.contains(e)) {
                    foundStarts.put(starts[i], Pair(e, step));
                }
            }

            if (foundStarts.size == starts.size) {
                return;
            } else {
                return iterStarts(locations.map { nextMove(it, step, maze) }, step + 1)
            }
        }
        iterStarts(starts, 0)

        return foundStarts.values.map<Pair<String, Long>, Long> { (end, originalStep) ->
            val foundMoveEnds = mutableMapOf<Pair<String, Long>, Long>();
            tailrec fun iterEnds(location: String, step: Long): Long {
                if (ends.contains(location)) {
                    val move = Pair(location, step % maze.moves.size)
                    if (foundMoveEnds.contains(move)) {
                        return step - foundMoveEnds.getOrDefault(move, Long.MAX_VALUE)
                    } else {
                        foundMoveEnds.put(move, step)
                    }
                }
                return iterEnds(nextMove(location, step, maze), step + 1)
            }

            iterEnds(nextMove(end, originalStep, maze), originalStep + 1)
        }
    }

    fun part2(input: List<String>): Long {
        val maze = parseMovesAndPaths(input)

        return findCycles("A", "Z", maze).lcm()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(6L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    val test2Input = readInput("${day}_test2")
    part2(test2Input).assertEqual(6L)
    timeAndPrint { part2(input) }
}
