import kotlin.math.max
import kotlin.math.min

data class Part(val value: String, val line: Int, val range: IntRange)

fun main() {
    val day = "Day03";

    fun hasSymbol(range: IntRange, possibleLines: List<String>): Boolean {
        return possibleLines.any {
            it.substring(max(range.start - 1, 0), min(range.endInclusive + 2, it.length)).contains(Regex("[^.0-9]"))
        }
    }

    fun part1(input: List<String>): Int {
        val allParts = input.flatMapIndexed { i, line ->
            Regex("[0-9]+").findAll(line).map { match ->
                Part(match.value, i, match.range)
            }
        }
        println("Found parts ${allParts.map{it.value}}")
        val validParts = allParts.filter { part ->
            hasSymbol(part.range, input.subList(max(0, part.line - 1), min(part.line + 2, input.size)))
        }
        println("Valid")
        validParts.forEach{println("\t" + it)}
        return validParts.sumOf { it.value.toInt() }

    }

    fun adjacent(a: Part, b: Part): Boolean {
        return IntRange(a.range.start - 1, a.range.endInclusive + 1)
            .intersect(b.range)
            .isNotEmpty() &&
                IntRange(a.line - 1, a.line + 1).contains(b.line);
    }

    fun part2(input: List<String>): Int {
        val allParts = input.flatMapIndexed { i, line ->
            Regex("[0-9]+").findAll(line).map { match ->
                Part(match.value, i, match.range)
            }
        }

        val allStars = input.flatMapIndexed { i, line ->
            Regex("\\*").findAll(line).map { match ->
                Part(match.value, i, match.range)
            }
        }

        return allStars.sumOf {star ->
            val adjacentParts = allParts.filter{
                adjacent(it, star)
            }

            if (adjacentParts.size == 2) {
                adjacentParts[0].value.toInt() * adjacentParts[1].value.toInt()
            } else {
                0;
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${day}_test")
    check(part1(testInput) == 4361)

    val input = readInput("${day}")
    timeAndPrint { part1(input) }

    check(part2(testInput) == 467835)
    timeAndPrint { part2(input) }
}
