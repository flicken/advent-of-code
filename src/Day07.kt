import java.math.BigInteger

enum class Op {
    Add, Multiply, Concatenate,
}

fun main() {
    val day = "Day07"

    data class Equation(val testVal: BigInteger, val numbers: List<BigInteger>, val result: BigInteger) {
        fun test(op: Op): Equation =
            when (op) {
                Op.Add -> Equation(testVal, numbers.drop(1), result + numbers[0])
                Op.Multiply -> Equation(testVal, numbers.drop(1), result * numbers[0])
                Op.Concatenate -> Equation(testVal, numbers.drop(1), "${result}${numbers[0]}".toBigInteger())
            }
    }

    fun String.parse(): Equation {
        val (testValS, rest) = this.split(": ")
        val numbers = rest.split(" ").map { it.toBigInteger() }
        return Equation(testValS.toBigInteger(), numbers.drop(1), numbers[0])
    }

    fun isValid(e: Equation, ops: List<Op>): Boolean {
        if (e.numbers.isEmpty()) {
            return e.testVal == e.result
        }

        return ops.any {
            isValid(e.test(it), ops)
        }
    }

    fun part1(input: List<String>): Long {
        val equations = input.map{it.parse()}

        return equations.filter{isValid(it, listOf(Op.Add, Op.Multiply))}
            .map{it.testVal}
            .map{it.toLong()}.sum()
    }

    fun part2(input: List<String>): Long {
        val equations = input.map{it.parse()}

        return equations.filter{isValid(it, listOf(Op.Add, Op.Multiply, Op.Concatenate))}
            .map{it.testVal}
            .map{it.toLong()}.sum()
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
