import java.awt.geom.Line2D.linesIntersect
import kotlin.math.abs

typealias Line = Pair<Location, Location>;

fun main() {
    val day = "Day09"

    fun asLines(pointInSequence: List<Location>): List<Line> = pointInSequence.zipWithNext { a, b -> a to b } +
            (pointInSequence.last() to pointInSequence.first())

    fun part1(input: List<String>): Long {
        val locations = input.map{it.split(",")}.map{Location(it[0].toInt(), it[1].toInt())}

        return locations.allPairs().map { it.areaOf() }.max()
    }

    fun rectangleInsideLines(corners: Line): List<Line> {
        val (a, b) = corners
        val minCol = minOf(a.col, b.col)
        val maxCol = maxOf(a.col, b.col)
        val minRow = minOf(a.row, b.row)
        val maxRow = maxOf(a.row, b.row)

        return asLines(
            listOf(
                Location(minRow + 1, minCol + 1),
                Location(minRow + 1, maxCol - 1),
                Location(maxRow - 1, maxCol - 1),
                Location(maxRow - 1, minCol + 1),
            )
        )
    }

    fun intersects(rectangleLine: Line, polygonLine: Line) = linesIntersect(
        rectangleLine.first.row.toDouble(),
        rectangleLine.first.col.toDouble(),
        rectangleLine.second.row.toDouble(),
        rectangleLine.second.col.toDouble(),
        polygonLine.first.row.toDouble(),
        polygonLine.first.col.toDouble(),
        polygonLine.second.row.toDouble(),
        polygonLine.second.col.toDouble(),
    )

    fun part2(input: List<String>): Long {
        val locations = input.map{it.split(",")}.map{Location(it[1].toInt(), it[0].toInt())}
        val polygonLines = asLines(locations)
        return locations.allPairs().sortedByDescending { it.areaOf() }
            .filterNot { rectangleCorners ->
                rectangleInsideLines(rectangleCorners).any { rectangleLine ->
                    polygonLines.any { intersects(rectangleLine, it) }
                }
            }
            .first()
            .areaOf()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(50L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(24L)
    val t = timeAndPrint {
        part2(input)
    }
    if (t >= 3299452380) {
        throw Error("Too high")
    }
    if (t <= 258114870) {
        throw Error("Too low")
    }
}

private fun Pair<Location, Location>.areaOf(): Long {
    return (abs(this.first.col.toLong() - this.second.col.toLong()).plus(1) *
            abs(this.first.row.toLong() - this.second.row.toLong()).plus(1)).toLong()
}
