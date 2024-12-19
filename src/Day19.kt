fun main() {
    val day = "Day19"

    fun List<String>.parse(): Pair<List<String>, List<String>> =
        this.splitBy { it == "" }.let { (towelStr, patterns) ->
            towelStr[0].split(", ").sorted() to patterns
        }

    fun List<String>.isPossible(pattern: String): Boolean {
        val towels = this

        val graph = object : Graph<String> {
            override fun neighborsOf(node: String): Iterable<Cost<String>> =
                towels.mapNotNull { towel ->
                    val next = node + towel
                    if (pattern.startsWith(next)) {
                        Cost(next, towel.length)
                    } else {
                        null
                    }
                }
        }

        val result = graph.search("", goalFunction = {it == pattern})

        return result.destination != null
    }

    fun part1(input: List<String>): Long {
        val (towels, patterns) = input.parse()

        return patterns.count {
            towels.isPossible(it)
        }.toLong()
    }


    fun String.countPossibilities(towels: List<String>): Long {
        val possibilities = mutableMapOf("" to 1L)

        fun count(s: String): Long =
            possibilities.getOrPut(s, {
                towels.filter { s.startsWith(it) }
                    .sumOf { count(s.substring(it.length))}
            })

        return count(this)
    }

    fun part2(input: List<String>): Long {
        val (towels, patterns) = input.parse()

        return patterns.sumOf { it.countPossibilities(towels) }
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(6L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(16L)
    timeAndPrint { part2(input) }
}
