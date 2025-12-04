fun main() {
    val day = "Day01"

    fun part1(input: List<String>): Long {
        var dial = 50;
        return input.count { line ->
            val direction = line[0];
            val amount = line.drop(1).toInt()

            if (direction == 'L') {
                dial -= amount
            } else {
                dial += amount
            }

            dial %= 100

            dial == 0
        }.toLong()
    }

    fun part2(input: List<String>): Long {
        // The 0x4C49434B "LICK" method
        var total = 0L;
        var count = 0L
        var dial = 50

        // Very slow and naive, but correct
        input.forEach { line ->
            val direction = line[0];
            val amount = line.drop(1).toInt()
            total += amount

            if (direction == 'L') {
                (1..amount).forEach {
                    dial -= 1
                    dial %= 100
                    if (dial == 0) {
                        count += 1
                    }
                }
            } else {
                (1..amount).forEach {
                    dial += 1
                    dial %= 100
                    if (dial == 0) {
                        count += 1
                    }
                }
            }
        }
        return count;
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(3L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(6L)
    timeAndPrint { part2(input) }
}
