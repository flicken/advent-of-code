import kotlin.math.abs

private fun <T> List<T>.pairsOf(): List<Pair<T, T>> {
    val max = this.size - 1
    val pairs = mutableListOf<Pair<T, T>>()
    for (i in (0..max)) {
        for (j in (i..max)) {
            pairs.add(Pair(this[i], this[j]))
        }
    }
    return pairs
}

fun main() {
    data class Galaxy(val row: Int, val col: Int)

    val day = "Day11"

    fun expand(rowOrCol: Set<Int>, expansionFactor: Long): Map<Int, Long> {
        var extra = 0L
        return (0..(rowOrCol.max())).associateWith { row ->
            if (!rowOrCol.contains(row)) {
                extra += expansionFactor
            }
            row + extra
        }
    }

    fun calcDiff(a: Int, b: Int, expanded: Map<Int, Long>): Long {
        val ax = expanded.getValue(a)
        val bx = expanded.getValue(b)
        return abs(ax - bx)
    }

    fun calculateDistances(input: List<String>, expansionSize: Long): Long {
        val originalGalaxies = mutableListOf<Galaxy>()


        input.forEachIndexed { row, line ->
            line.forEachIndexed { col, c ->
                if (c == '#') {
                    originalGalaxies.add(Galaxy(row, col))
                }
            }
        }

        val rows = originalGalaxies.map { it.row }.toSet()
        val cols = originalGalaxies.map { it.col }.toSet()

        val expandedRows = expand(rows, expansionSize)
        val expandedCols = expand(cols, expansionSize)

        return originalGalaxies.pairsOf()
            .sumOf { (a, b) -> (calcDiff(a.row, b.row, expandedRows) + calcDiff(a.col, b.col, expandedCols)) }
    }

    fun part1(input: List<String>): Long {
        return calculateDistances(input, 1L)
    }

    fun part2(input: List<String>): Long {
        return calculateDistances(input, 1000000 - 1)
    }

    val testInput = readInput("${day}_test")
    val input = readInput(day)

    part1(testInput).assertEqual(374L)
    timeAndPrint { part1(input) }

    calculateDistances(testInput, 10 - 1).assertEqual(1030L)
    calculateDistances(testInput, 100 - 1).assertEqual(8410L)
    timeAndPrint { part2(input) }
}

