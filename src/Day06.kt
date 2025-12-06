fun main() {
    val day = "Day06"

    fun <T> List<List<T>>.transpose(): List<List<T>> {
        if (isEmpty()) return emptyList()
        val maxCols = this.maxOf { it.size }
        return List(maxCols) { col ->
            this.mapNotNull { row -> row.getOrNull(col) }
        }
    }

    fun part1(input: List<String>): Long {
        return input.map{it.trim().split(" +".toRegex())}.transpose().sumOf { problem ->
            val operator = problem.last()
            val values = problem.dropLast(1).map{it.toLong()}

            if (operator == "+") {
                values.fold(0L, { acc, v -> acc + v })
            } else if (operator == "*") {
                values.fold(1L, { acc, v -> acc * v })
            } else {
                -1L
            }
        }
    }

    fun part2(input: List<String>): Long {
         return input.transpose().splitBy { it.trim() === "" }.sumOf { problem ->
             val operator = problem[0].last()
             val values =  problem.map{it.replace(operator, ' ').trim().toLong()}
             if (operator == '+') {
                 values.fold(0L, { acc, v -> acc + v })
             } else if (operator == '*') {
                 values.fold(1L, { acc, v -> acc * v })
             } else {
                 -1L
             }
         }
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(4277556L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(3263827L)
    timeAndPrint { part2(input) }
}
