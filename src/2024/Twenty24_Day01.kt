fun main() {
    val day = "Day01"

    fun listOfTwoInts(input: List<String>): Pair<ArrayList<Int>, ArrayList<Int>> {
        val left = ArrayList<Int>();
        val right = ArrayList<Int>();

        input.forEach { line ->
            val (a, b) = line.split(" +".toRegex()).map { it.toInt() }
            left.add(a)
            right.add(b)
        }
        return Pair(left, right)
    }

    fun part1(input: List<String>): Long {
        val (left, right) = listOfTwoInts(input)

        left.sort();
        right.sort();

        return left.zip(right).map{(l, r) ->
            Math.abs(l - r)
        }.sum().toLong();
    }

    fun part2(input: List<String>): Long {
        val (left, right) = listOfTwoInts(input)

        return left.map{l -> l * right.count{it == l}}.sum().toLong()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(11L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(31L)
    timeAndPrint { part2(input) }
}
