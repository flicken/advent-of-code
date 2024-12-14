import java.io.File

fun main() {
    val day = "Day14"

    data class Robot(val p: LocationL, val v: LocationL)

    fun LocationL.wrapInto(grid: LocationL): LocationL {
        fun wrap(p: Long, size: Long) = ((p % size) + size) % size
        return LocationL(wrap(this.row, grid.row), wrap(this.col, grid.col))
    }

    fun LocationL.sector(grid: LocationL): LocationL {
        val rowSplit = grid.row / 2
        val colSplit = grid.col / 2

        if (this.row == rowSplit || this.col == colSplit) {
            return LocationL(-1, -1)
        } else {
            return LocationL(if (this.row < rowSplit) 0 else 1, if (this.col < colSplit) 0 else 1)
        }
    }

    fun LocationL.show(robots: List<Robot>, showSplit: Boolean = false): String {
        val locationCounts = robots.groupBy { it.p }.mapValues { it.value.size.toLong() }
        return (0..<this.row).map { row ->
            (0..<this.col).map { col ->
                val count = locationCounts[LocationL(row, col)]
                if (showSplit && (row == this.row / 2 || col == this.col / 2) ) {
                    " "
                } else if (count == null) {
                    "."
                } else if (count > 9L) {
                    throw IllegalStateException("Count > 9")
                } else {
                    count.toString()
                }
            }.joinToString("")
        }.joinToString("\n")
    }

    fun Robot.move(grid: LocationL, count: Long) =
        this.copy(p = (p + (v * count)).wrapInto(grid))


    fun List<String>.parse(): List<Robot> = this.map { line ->
        line.split(" ")
            .map {
                it.split("=")[1]
                    .split(",")
                    .map { it.toLong() }
            }
            .map { LocationL(it[1], it[0]) }
            .let { Robot(it[0], it[1]) }
    }

    fun part1(input: List<String>, grid: LocationL): Long {
        val robots = input.parse()

        println("\nInitial")
        grid.show(robots, false).p()

        return robots.map { it.move(grid, 100) }
            .tap {
                println("\nAfter 100")
                grid.show(it, false).p()

                println("\nAfter 100 with sectors")
                grid.show(it, true).p()
            }
            .groupBy { it.p.sector(grid) }.tap {
                it.forEach {
                    println(it.key)
                    it.value.forEach { println("\t${it}") }
                }
            }
            .mapValues { it.value.size.toLong() }
            .filterKeys { it.row != -1L }
            .values
            .reduce { a, b -> a * b }
    }

    fun part2(input: List<String>, grid: LocationL): Long {
        val robots = input.parse()

        println("\nInitial")
        grid.show(robots, false).p()

        var highWatermark = 0
        (0..10000L).forEach { count ->
            val moved = robots.map { it.move(grid, count) }

            val locations = moved.map{it.p}.toSet()
            fun hasNeighbor(l: LocationL): Boolean {
                return Direction.entries.any {
                    val oneAway = l.go(it)
                    locations.contains(oneAway) || locations.contains(oneAway.go(it.turn(Direction.Right)))
                }
            }

            val withNeighbors = moved.count { hasNeighbor(it.p) }
            if (withNeighbors > highWatermark) {
                println("${count} high water mark: ${withNeighbors} / ${robots.size} neighbors")
                highWatermark = withNeighbors
            }

            // If "most of the robots" have neighbors, it might be a picture of an Xmas tree
            // Write it out to disk for manual review
            if (withNeighbors > (robots.size - (robots.size / 2))) {
                println("${count} has ${withNeighbors}")
                File("${count.toString().padStart(5, '0')}.txt").writeText(grid.show(moved, false))
            }
            if (locations.size == moved.size) {
                println("${count} No robots overlap")
                throw IllegalStateException("No robots overlap")
            }
        }
        return -1
    }

    val testGrid = LocationL(7, 11)
    val testRobot = Robot(LocationL(4, 2), LocationL(-3, 2))
    (0..5).forEach {
//        println(it)
//        testGrid.show(listOf(testRobot.move(testGrid, it.toLong()))).p()
//        println("${it}: ${testRobot.move(testGrid, it.toLong())}")
    }



    val testInput = readInput("${day}_test")
    println("Test")
    part1(testInput, testGrid).assertEqual(12L)

    val input = readInput(day)

    println("Real")
    val realGrid = LocationL(103, 101)
    val part1Result = timeAndPrint { part1(input, realGrid) }
    if (part1Result <= 191803248) {
        throw IllegalStateException("Too low")
    }

    timeAndPrint { part2(input, realGrid) }
}
