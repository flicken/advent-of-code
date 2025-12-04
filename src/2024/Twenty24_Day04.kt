fun main() {
    val day = "Day04"

    fun part1(input: List<String>): Long {
        var count = 0
        val charArrays = input.map { it.toCharArray() }

        charArrays.forEachIndexed{ i, line ->
            line.forEachIndexed { j, c ->
                if (c == 'X') {
                    count += listOf(charArrays.checkMas(i,j, 0, 1),
                    charArrays.checkMas(i,j, 0, -1),
                    charArrays.checkMas(i,j, 1, 0),
                    charArrays.checkMas(i,j, 1, 1),
                    charArrays.checkMas(i,j, 1, -1),
                    charArrays.checkMas(i,j, -1, 0),
                    charArrays.checkMas(i,j, -1, 1),
                    charArrays.checkMas(i,j, -1, -1)).count { it }
                }
            }
        };
        return count.toLong()
    }

    fun part2(input: List<String>): Long {
        var count = 0
        val charArrays = input.map { it.toCharArray() }

        charArrays.forEachIndexed{ i, line ->
            line.forEachIndexed { j, c ->
                if (c == 'A') {
                    if (isMsOrSm(charArrays.safeGet(i - 1, j - 1),
                            charArrays.safeGet(i + 1, j + 1)) &&
                        isMsOrSm(charArrays.safeGet(i - 1, j + 1),
                            charArrays.safeGet(i + 1, j - 1))) {
                        count += 1
                    }
                }
            }
        }
        return count.toLong()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(18L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(9L)

    val result = timeAndPrint { part2(input) }
    if (result >= 1976L) {
        println("too high!!!")
    }
}


private fun List<CharArray>.checkMas(startI: Int, startJ: Int, iDirection: Int, jDirection: Int) =
    this.safeGet(startI + 1 * iDirection, startJ + 1 * jDirection) == 'M' &&
        this.safeGet(startI +  2 * iDirection, startJ + 2 * jDirection) == 'A' &&
        this.safeGet(startI +  3 * iDirection, startJ + 3 * jDirection) == 'S';

fun isMsOrSm(a: Char?, b: Char?) =
    (a == 'M' && b == 'S') ||
    (a == 'S' && b == 'M')

private fun List<CharArray>.safeGet(i: Int, j: Int): Char? {
    if (i >= 0 && i < this.size) {
        val line = this[i]
        if (j >= 0 && j < line.size) {
            return line[j]
        }
    }
    return null;
}
