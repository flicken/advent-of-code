fun main() {
    val day = "Day05"

    fun mergeRanges(ranges: Iterable<LongRange>): List<LongRange> =
        ranges
            .sortedBy { it.first }
            .fold(mutableListOf()) { acc, range ->
                when {
                    acc.isEmpty() -> acc += range

                    // overlap or touch? merge with last
                    range.first <= acc.last().last + 1 -> {
                        val last = acc.removeLast()
                        acc += last.first..maxOf(last.last, range.last)
                    }

                    else -> acc += range
                }
                acc
            }

    fun part1(input: List<String>): Long {
        val (freshS, stockS) = input.splitBy { it == "" }

        val ranges = mergeRanges(freshS.map{
            val (start, end) = it.split('-').map{it.toLong()}
            start..end
        })

        val stock = stockS.map{it.toLong()}

        return stock.count { s -> ranges.any { it.contains(s) } }.toLong()
    }

    fun part2(input: List<String>): Long {
        val (freshS) = input.splitBy { it == "" }

        val ranges = freshS.map{
            val (start, end) = it.split('-').map{it.toLong()}
            start..end
        }.sortedBy { it.first }

        return mergeRanges(ranges).sumOf{ it.last - it.first + 1 }
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(3L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(14L)
    timeAndPrint { part2(input) }
}
