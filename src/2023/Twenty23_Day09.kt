fun main() {
    val day = "Day09"

    fun List<Long>.expand(): List<List<Long>> {
        val lists = mutableListOf(this);
        while (!lists.last().all { it == 0L }) {
            lists.add(lists.last().windowed(2).map { (a, b) -> b - a })
        }
        return lists
    }

    fun part1(input: List<String>): Long {
        return input.map {
            it.split(" +".toRegex())
                .map { it.toLong() }
                .expand()
                .map { it.last() }.sum()
        }.sum()
    }

    fun part2(input: List<String>): Long {
        return input.map {
            it.split(" +".toRegex())
                .map { it.toLong() }
                .expand()
                .map { it.first() }.reversed().reduce { a, b ->
                    b - a
                }
        }.sum()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(114L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(2L)
    timeAndPrint { part2(input) }
}
