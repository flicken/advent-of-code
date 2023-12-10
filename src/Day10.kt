data class Location(val line: Int, val col: Int)

fun main() {
    val day = "Day10"


    fun findStart(maze: List<String>): Location {
        val line = maze.indexOfFirst { it.contains('S') }
        val col = maze[line].indexOf('S')

        return Location(line, col);
    }

    val m = "-L|F7\n" +
            "7S-7|\n" +
            "L|7||\n" +
            "-L-J|\n" +
            "L|-JF\n"

    fun findConnectingLocations(maze: List<String>, location: Location): List<Location> {

        return listOf();
    }

    fun Location.up() = this.copy(line = this.line - 1)
    fun Location.down() = this.copy(line = this.line + 1)
    fun Location.left() = this.copy(col = this.col - 1)
    fun Location.right() = this.copy(col = this.col + 1)

    fun findNext(maze: List<String>, location: Location, prev: Location): Location {
        return when (maze[location.line][location.col]) {
            'S' -> location.right() // special case for puzzle
            'L' -> if (location.up() == prev) location.right() else location.up()
            '7' -> if (location.left() == prev) location.down() else location.left()
            'J' -> if (location.left() == prev) location.up() else location.left()
            '-' -> if (location.left() == prev) location.right() else location.left()
            '|' -> if (location.up() == prev) location.down() else location.up()
            'F' -> if (location.down() == prev) location.right() else location.down()
            else -> throw Exception("Cannot find next location")
        }
    }

    fun part1(input: List<String>): Long {
        val maze = input
        val start = findStart(maze);
        var location = start;
        var prev = start;
        var count = 0;

        do {
            count += 1
            val next = findNext(maze, location, prev)
            prev = location
            location = next
        } while (location != start)

        return (count / 2).toLong()
    }

    fun part2(input: List<String>): Long {

        return -1
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(4L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(-43)
    timeAndPrint { part2(input) }
}
