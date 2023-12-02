import kotlin.math.max

fun main() {
    fun showingToMap(showing: String): Map<String, Int> {
        return showing.split(", ").associate {
            val (countS, color) = it.split(" ");
            color to countS.toInt();
        };
    }

    val part1Max = showingToMap("12 red, 13 green, 14 blue");

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val parts = line.split("; ", ": ");
            val game = parts[0].split(" ")[1].toInt();

            val allPass = parts.drop(1).map{showingToMap(it)}.all { showing ->
                showing.all { (color, count) ->
                    part1Max.getValue(color) >= count
                }
            };

            if (allPass) game else 0
        };
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val parts = line.split("; ", ": ")

            val maximums = parts.drop(1).map{showingToMap(it)}.reduce { maxNeeded, cur ->
                part1Max.keys.map { color ->
                    color to max(maxNeeded.getOrDefault(color, 0), cur.getOrDefault(color, 0))
                }.toMap()
            };

            maximums.values.reduce{a, b -> a * b}
        };
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
