fun main() {
    val day = "Day03"

    fun part1(input: List<String>): Long {
        return input.flatMap{"mul\\(([0-9]+),([0-9]+)\\)".toRegex().findAll(it).map{matchResult ->
            matchResult.groupValues[1].toInt() * matchResult.groupValues[2].toInt()
        }}.sum().toLong()
    }

    fun part2(input: List<String>): Long {
        var canDo = true
        return input.flatMap{"(mul|do|don't)\\((?:([0-9]+),([0-9]+))?\\)".toRegex().findAll(it).map{matchResult ->
            var result = 0L
            val op = matchResult.groupValues[1]
            when(op) {
                "mul" -> if (canDo) {result = matchResult.groupValues[2].toLong() * matchResult.groupValues[3].toLong()}
                "don't" ->
                    canDo = false
                "do" ->
                    canDo = true
            }
            result
        }}.sum()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(161L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(readInput("${day}_test2")).assertEqual(48)
    timeAndPrint { part2(input) }
}
