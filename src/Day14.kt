fun main() {
    val day = "Day14"
    fun List<String>.asPlatform() = map { it.toCharArray() }.transposeCharArray()

    fun tilt(platform: List<CharArray>): List<CharArray> {
        return platform.map { column ->
            var placeAt = -1
            val out = CharArray(column.size, { '.' })
            column.mapIndexed { i, c ->
                when (c) {
                    'O' -> {
                        placeAt += 1
                        out[placeAt] = c
                    }

                    '#' -> {
                        placeAt = i
                        out[placeAt] = c
                    }

                    else -> out[i] = c
                }
            }
            out
        }
    }


    fun calculateLoad(platform: List<CharArray>): Int {
        return platform.sumOf { column ->
            column.mapIndexed { i, c ->
                when (c) {
                    'O' -> column.size - i
                    else -> 0
                }
            }.sum()
        }
    }

    fun part1(input: List<String>): Int {
        val platform = input.asPlatform()
        return calculateLoad(tilt(platform))
    }


    fun rotate(platform: List<CharArray>): List<CharArray> {
        // rotate by -90 degrees
        val copy = platform.map {
            val copy = it.clone()
            copy.reverse()
            copy
        }.transposeCharArray()
        return copy
    }


    fun printPlatform(platform: List<CharArray>) {
        println("\n------")
        println(platform.transposeCharArray().map { String(it) }.joinToString("\n"))
    }

    fun cycle(platform: List<CharArray>): List<CharArray> {
        val northTilted = tilt(platform)
        val westTilted = tilt(rotate(northTilted))
        val southTilted = tilt(rotate(westTilted))
        val eastTilted = tilt(rotate(southTilted))
        val rotate = rotate(eastTilted)
        return rotate
    }

    fun part2(input: List<String>): Int {
        val platform = input.asPlatform()
        val calcCycles = 1_000_000_000

        // Hack for advent of code inputs
        val cycleSize = if (platform.size == 100) 14 else 7
        var currentCycle = platform

        val loads = mutableListOf<Int>()
        while (loads.size < calcCycles) {
            currentCycle = cycle(currentCycle)
            loads.add(calculateLoad(currentCycle))
            val lastCycle = loads.takeLast(cycleSize)
            val prevCycle = loads.takeLast(2 * cycleSize).dropLast(cycleSize)
            if (prevCycle.isNotEmpty() && lastCycle == prevCycle) {
                println("Found cycle at ${loads.size} of length ${prevCycle.size} ${prevCycle} ")
                break
            }
        }

        printPlatform(currentCycle)

        val cycleNum = loads.size + 1
        val findAt = (calcCycles - cycleNum) % cycleSize + (cycleNum - cycleSize) - 1
        return loads[findAt]
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(136)


    val input = readInput(day)
    val platform = input.asPlatform()
    val completedRotated = rotate(rotate(rotate(rotate(platform))))
    platform.map { String(it) }.assertEqual(completedRotated.map { String(it) })

    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(64)
    timeAndPrint { part2(input) }
}
