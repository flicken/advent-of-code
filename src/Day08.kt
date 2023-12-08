data class Maze(val paths: Map<String, Pair<String, String>>, val moves: CharArray)

fun main() {
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
    ): Long {
        tailrec fun iter(location: String, step: Long): Long {
            if (location == to) {
                return step;
            } else {
                return iter(nextMove(location, step, maze), step + 1)
            }
        }

        return iter(from, 0);
    }


    fun parseMovesAndPaths(input: List<String>): Maze {
        val moves = input.first().toCharArray()

        val paths = input.drop(2).map {
            val (name, left, right) = "[^A-Z0-9]+".toRegex().split(it)
            name to Pair(left, right)
        }.toMap()

        return Maze(paths, moves)
    }

    fun part1(input: List<String>): Long {
        val maze = parseMovesAndPaths(input)

        return countMoves("AAA", "ZZZ", maze)
    }

    fun part2(input: List<String>): Long {
        return -1
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(6L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    val test2Input = readInput("${day}_test2")
    part2(test2Input).assertEqual(6L)
    timeAndPrint { part2(input) }
}
