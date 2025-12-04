fun makeWayForDucklings(n: Long): Long {
    return if (n < 10) {
        10L
    } else if (n < 100) {
        100L
    } else if (n < 1000) {
        1000L
    } else {
        throw Exception("Too big!")
    }
}

fun main() {
    val day = "Day07"

    data class Equation(val testVal: Long, val numbers: List<Long>)

    fun String.parse(): Equation {
        val (testValS, rest) = this.split(": ")
        val numbers = rest.split(" ").map { it.toLong() }
        return Equation(testValS.toLong(), numbers)
    }

    fun isValid(lhs: Long, reverseNumbers: List<Long>): Boolean {
        val next = reverseNumbers.first()

        if (reverseNumbers.size == 1) {
            return next == lhs
        }

        return ((lhs % next) == 0L &&  isValid(lhs / next, reverseNumbers.drop(1))) ||
                ((lhs > next) && isValid(lhs - next, reverseNumbers.drop(1)))
    }

    fun isValidPart2(lhs: Long, reverseNumbers: List<Long>): Boolean {
        val next = reverseNumbers.first()

        if (reverseNumbers.size == 1) {
            return next == lhs
        }

        return (lhs.toString().endsWith(next.toString()) && isValidPart2((lhs - next) / makeWayForDucklings(next), reverseNumbers.drop(1))) ||
                ((lhs % next) == 0L && isValidPart2(lhs / next, reverseNumbers.drop(1))) ||
                (lhs > next && isValidPart2(lhs - next, reverseNumbers.drop(1)))
    }

    fun part1(input: List<String>): Long {
        val equations = input.map{it.parse()}

        return equations.filter{isValid(it.testVal, it.numbers.asReversed())}.map{it.testVal}.sum()
    }

    fun part2(input: List<String>): Long {
        val equations = input.map{it.parse()}

        return equations.filter{isValidPart2(it.testVal, it.numbers.asReversed())}.map{it.testVal}.sum()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(3749L)

    val input = readInput(day)
    val result = timeAndPrint { part1(input) }

    if (result >= 14711933467290) {
        println("Too high!")
    }

    part2(testInput).assertEqual(11387L)
    timeAndPrint { part2(input) }
}
