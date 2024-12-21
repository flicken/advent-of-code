import Direction.*

private fun <A, B>Pair<A, B>.swap(): Pair<B, A> = this.second to this.first

fun main() {
    val day = "Day21"

    val letterToDirection = mapOf(
        '>' to Right,
        '<' to Left,
        '^' to Up,
        'v' to Down
    )

    val directionToLetter = letterToDirection.map { (k, v) ->
        v to k
    }.toMap()


    data class SearchState(val buttonPresses: List<Char>, val location: Location, val direction: Direction, val lettersLeft: String) {
        fun press(): SearchState = copy(buttonPresses = buttonPresses + 'A', lettersLeft = lettersLeft.drop(1))
        fun go(direction: Direction): SearchState = copy(buttonPresses = buttonPresses + directionToLetter.getValue(direction), location = location.go(direction), direction = direction)
    }

    operator fun List<String>.get(location: Location): Char? = this.getOrNull(location.row)?.getOrNull(location.col)
    fun List<String>.inBounds(location: Location) = this[location] !== null

    val distanceBetweenDirections = mutableMapOf(
        (Left to Down) to 1,
        (Left to Right) to 2,
        (Left to Up) to 2,

        (Right to Up) to 2,
        (Right to Down) to 1,

        (Up to Down) to 1
    )

    distanceBetweenDirections.toList().forEach { entry ->
        distanceBetweenDirections[entry.first.swap()] = entry.second
    }
    Direction.entries.forEach{distanceBetweenDirections[it to it] = 0}

    val directionToActivate = mapOf(
        Up to 1,
        Right to 1,
        Down to 2,
        Left to 3,
    )

    val expansion = mapOf(
        "<<" to "",
        "<>" to ">>",
        "<v" to ">",
        "<^" to ">^",
        "<A" to ">>^", // >^>

        "v<" to "<",
        "v>" to ">",
        "vv" to "",
        "v^" to "^",
        "vA" to ">^", // ^>

        "><" to "<<",
        ">>" to "",
        ">v" to "<",
        ">^" to "<^", // ^<
        ">A" to "^",

        "^<" to "v<",
        "^>" to "v>", // >v
        "^v" to "v",
        "^^" to "",
        "^A" to ">",

        "A<" to "v<<", // v<< <v<
        "A>" to "v",
        "Av" to "<v", // v<
        "A^" to "<",
        "AA" to "",
    )

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

    fun String.expand(): String =
        ("A" + this).windowed(2, 1).map{expansion.getValue(it)}.joinToString("A") + "A"

    val numericKeypad = listOf("789", "456", "123", "#0A")

    val counts = mutableMapOf<Pair<String, Int>, Long>()

    (expansion.entries).forEach {
        counts[it.key to 1] = it.value.length.toLong() + 1
    }

    fun String.countKeypresses(robotCount: Int): Long =
        counts.getOrPut(this to robotCount, {
            return ("A" + this.expand()).windowed(2, 1).sumOf {
                it.countKeypresses(robotCount - 1)
            }
        })


    fun List<String>.countKeypresses(robotCount: Int): Long {
        return this.sumOf { codeS ->
            println("${codeS} counting ${robotCount}")
            val paths = numericKeypad.shortestPaths(codeS, Location(3, 2))

            paths.map { numericPresses ->
                //                numericPresses.expand().expand().replace("v<<A", "<v<A").p()
                //                numericPresses.expand().p()
                //                numericPresses.p()
                //                codeS.p()
                val length = numericPresses.countKeypresses(robotCount)
                val code = codeS.dropLast(1).toLong()
                println("${codeS} = ${length} * $code")
                code to length
            }.minOf { it.first * it.second }
        }
    }

    fun part1(input: List<String>): Long = input.countKeypresses(2)
    fun part2(input: List<String>): Long = input.countKeypresses(25)

    val testInput = readInput("${day}_test")
    val input = readInput(day)

    part1(testInput).assertEqual(126384L)
    timeAndPrint { part1(input) }.also{check(it > 207912, {  "Must be larger than 207912"}) }

    println("Part 2")
//    timeAndPrint {  part2(testInput) }
    timeAndPrint { part2(input) }
}
