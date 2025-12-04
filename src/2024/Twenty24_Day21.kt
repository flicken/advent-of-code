import Direction.*

fun main() {
    val day = "Day21"

    val directionToLetter = mapOf(
        Right to '>',
        Left to '<',
        Up to '^',
        Down to 'v',
    )

    data class SearchState(val buttonPresses: List<Char>, val location: Location, val direction: Direction, val lettersLeft: String) {
        fun press(): SearchState = copy(buttonPresses = buttonPresses + 'A', lettersLeft = lettersLeft.drop(1))
        fun go(direction: Direction): SearchState = copy(buttonPresses = buttonPresses + directionToLetter.getValue(direction), location = location.go(direction), direction = direction)
    }

    operator fun List<String>.get(location: Location): Char? = this.getOrNull(location.row)?.getOrNull(location.col)
    fun List<String>.inBounds(location: Location) = this[location] !== null

    val expansion = mapOf(
        "<<" to "A",
        "<>" to ">>A",
        "<v" to ">A",
        "<^" to ">^A",
        "<A" to ">>^A", // >^>

        "v<" to "<A",
        "v>" to ">A",
        "vv" to "A",
        "v^" to "^A",
        "vA" to "^>A", // >^

        "><" to "<<A",
        ">>" to "A",
        ">v" to "<A",
        ">^" to "<^A", // ^<
        ">A" to "^A",

        "^<" to "v<A",
        "^>" to "v>A", // >v
        "^v" to "vA",
        "^^" to "A",
        "^A" to ">A",

        "A<" to "v<<A", // v<< <v<
        "A>" to "vA",
        "Av" to "<vA", // v<
        "A^" to "<A",
        "AA" to "A",
    )

    fun String.expand(): String =
        ("A" + this).windowed(2).map{expansion.getValue(it)}.joinToString("")

    fun List<String>.shortestPaths(buttonPresses: String, start: Location): List<String> {
        val keypad = this
        val graph = object : Graph<SearchState> {
            override fun neighborsOf(node: SearchState): Iterable<Cost<SearchState>> =
                if (node.lettersLeft.isEmpty()) {
                    listOf()
                } else if (keypad[node.location] == node.lettersLeft.first()) {
                    listOf(Cost(node.press(), 1))
                } else {
                    Direction.entries.map{node.go(it)}
                        .filter{keypad.inBounds(it.location) && keypad[it.location] != '#'}
                        .map{Cost(it, 1)
                    }
                }
        }

        val result = graph.search(
            SearchState(listOf(), start, direction = Up, lettersLeft = buttonPresses),
            goalFunction = {it.lettersLeft.isEmpty()}
        )

        val results = mutableListOf<String>()
        graph.search(
            SearchState(listOf(), start, direction = Up, lettersLeft = buttonPresses),
            maximumCost = result.searchTree[result.destination]!!.second,
            goalFunction = {
                if (it.lettersLeft.isEmpty()) {
                    results+=it.buttonPresses.joinToString("")
                }
                false
            }
        )

        return results
    }

    val numericKeypad = listOf("789", "456", "123", "#0A")

    val counts = mutableMapOf<Pair<String, Int>, Long>()
    fun String.countKeypresses(robotCount: Int): Long {
        if (robotCount == 0) {
            return this.length.toLong()
        }

        return counts.getOrPut(this to robotCount, {
            ("A" + this).windowed(2).sumOf {
                expansion.getValue(it).countKeypresses(robotCount - 1)
            }
        })
    }

    fun List<String>.countKeypresses(robotCount: Int): Long {
        return this.sumOf { codeS ->
            println("${codeS} counting ${robotCount}")
            val paths = numericKeypad.shortestPaths(codeS, Location(3, 2))

            paths.map { numericPresses ->
                val length = numericPresses.countKeypresses(robotCount)
                val code = codeS.dropLast(1).toLong()
                println("${codeS} = ${length} * $code")
                code to length
            }.map { it.second.times(it.first) }.min()
        }
    }

    fun part1(input: List<String>): Long = input.countKeypresses(2)
    fun part2(input: List<String>): Long = input.countKeypresses(25)

    val testInput = readInput("${day}_test")
    val input = readInput(day)

    part1(testInput).assertEqual(126384L)
    timeAndPrint { part1(input) }.also{check(it >207912, {  "Must be larger than 207912"}) }

    println("Part 2")
    timeAndPrint { part2(input) }.also{ check(it > 0, {"Must be non-negative"}) }
        .also{check(it != 117419486641522L, {"Already tried 117419486641522"})}
        .also{check(it != 293923285495924L, {"Already tried 293923285495924"})}
        .also{check(it != 735745809159558L, {"Already tried 735745809159558"})}
}
