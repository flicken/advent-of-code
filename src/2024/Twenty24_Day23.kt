fun main() {
    val day = "Day23"

    fun List<String>.parseNetwork(): MutableMap<String, MutableSet<String>> {
        val network = mutableMapOf<String, MutableSet<String>>()

        this.forEach {
            it.split("-").sorted().let {
                network.getOrPut(it[0], { mutableSetOf() }).add(it[1])
                network.getOrPut(it[1], { mutableSetOf() }).add(it[0])
            }
        }
        return network
    }

    fun part1(input: List<String>): Long {
        val network = input.parseNetwork()

        return network.entries.filter{it.key.startsWith('t')}.flatMap { (first, value) ->
            value.flatMap { second ->
                val thirds = network.getValue(second)
                thirds.filter { network.getValue(it).contains(first) }.map { third ->
                    setOf(first, second, third)
                }
            }
        }.toSet().size.toLong()
    }

    fun part2(input: List<String>): String {
        val network = input.parseNetwork()

        return network.entries.map { (first, value) ->
            value.filter { second ->
                val thirds = network.getValue(second)
                thirds.filter { network.getValue(it).contains(first) }.map { third ->
                    setOf(first, second, third)
                }.isNotEmpty()
            }.toSet() + first
        }.toSet()
            .filter { it.toList().allPairs().all { network.getValue(it.first).contains(it.second) } }
            .maxBy { it.size }
            .sorted()
            .joinToString(",")
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(7L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual("co,de,ka,ta")
    timeAndPrint { part2(input) }
}
