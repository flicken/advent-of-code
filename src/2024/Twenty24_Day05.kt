fun main() {
    val day = "Day05"

    fun splitUpdates(input: List<String>, rules: Map<Int, List<Int>>): Pair<List<List<Int>>, List<List<Int>>> {
        val updates = input.dropWhile { it.trim().length > 0 }.drop(1).map { it.split(",").map { it.toInt() } }

        return updates.partition { u ->
            val seen = HashSet<Int>()
            u.all { page ->
                seen.add(page)
                val mustBeBefore = rules.getOrDefault(page, listOf())
                mustBeBefore.none { seen.contains(it) }
            }
        }
    }

    fun parseRules(input: List<String>) = input.takeWhile { it.trim().length > 0 }
        .map {
            it.split("|")
                .map { it.toInt() }
        }.groupBy { it[0] }
        .mapValues { (k, v) -> v.map { it[1] } }

    fun part1(input: List<String>): Long {
        val rules = parseRules(input)

        val (validUpdates, _)= splitUpdates(input, rules)
        return validUpdates.map {
            it[it.size / 2]
        }.sum().toLong()
    }

    fun part2(input: List<String>): Long {
        val rules = parseRules(input)
        val (_, invalidUpdates)= splitUpdates(input, rules)

        return invalidUpdates.map {
            it.sortedWith{a, b ->
                if (rules.getOrDefault(a, listOf()).contains(b)) -1 else {
                    if (rules.getOrDefault(b, listOf()).contains(b)) {
                        1
                    } else {
                        0
                    }
                }}
        }.map {
            it[it.size / 2]
        }.sum().toLong()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(143L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(123L)
    timeAndPrint { part2(input) }
}
