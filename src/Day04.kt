
operator fun List<String>.get(row: Int, col: Int): Char? =
    this.getOrNull(row)?.getOrNull(col)

fun List<String>.replaceAll(
    locations: Collection<Pair<Int, Int>>,
    ch: Char = '.'
): List<String> {
    val locationsSet = locations.toSet()

    return mapIndexed { row, line ->
        buildString(line.length) {
            for (col in line.indices) {
                if ((row to col) in locationsSet) append(ch)
                else append(line[col])
            }
        }
    }
}

fun main() {
    val day = "Day04"

    fun <T, R> iterateSequence(
        initial: T,
        step: (T) -> Pair<T, R>,
        done: (R) -> Boolean
    ): Sequence<R> = sequence {
        var state = initial

        while (true) {
            val (next, r) = step(state)
            if (done(r)) break
            yield(r)
            state = next
        }
    }

    fun part1(input: List<String>): Long {
        val found = input.flatMapIndexed { x, line ->
            line.mapIndexedNotNull { y, char ->
                if (char == '@' && listOfNotNull(
                        input[x - 1, y - 1], input[x - 1, y], input[x - 1, y + 1],
                        input[x, y - 1],                      input[x, y + 1],
                        input[x + 1, y - 1], input[x + 1, y], input[x + 1, y + 1]
                    ).count { it == '@' } < 4
                ) x to y else null
            }
        }

        return found.count().toLong()
    }

    fun part2(originalInput: List<String>): Long {
        return iterateSequence(originalInput, { input ->
            val canRemove = input.flatMapIndexed { x, line ->
                line.mapIndexedNotNull { y, char ->
                    if (char == '@' && listOfNotNull(
                            input[x - 1, y - 1], input[x - 1, y], input[x - 1, y + 1],
                            input[x, y - 1],                      input[x, y + 1],
                            input[x + 1, y - 1], input[x + 1, y], input[x + 1, y + 1]
                        ).count { it == '@' } < 4
                    ) Pair(x, y) else null
                }
            }

            Pair(input.replaceAll(canRemove, '.'), canRemove.size.toLong())
        },
        { it == 0L }
        ).sum()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(13L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(43L)
    timeAndPrint { part2(input) }
}
