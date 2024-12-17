fun main() {
    val day = "Day17"

    data class Computer(val a: Long = 0,
                        val b: Long = 0,
                        val c: Long = 0,
                        val pointer: Int = 0,
                        val out: List<Int> = listOf()) {
        fun pointerAdvance() = copy(pointer = pointer + 2)
    }

    fun Computer.combo(operand: Int): Long = when (operand) {
        0 -> 0
        1 -> 1
        2 -> 2
        3 -> 3
        4 -> a
        5 -> b
        6 -> c
        else -> throw IllegalArgumentException("Illegal operand $operand")
    }

    fun Computer.div(operand: Int): Long {
        return a shr combo(operand).toInt()
    }

    fun Computer.next(program: List<Int>): Computer {
            val (opcode, operand) = program.subList(pointer, pointer + 2)

            return when(opcode) {
                0 -> { // adv
                    copy(a = div(operand))
                }
                1 -> { // bxl
                    copy(b = (b xor operand.toLong()))
                }
                2 -> { // bst
                    copy(b = combo(operand) % 8)
                }
                3 -> { // jnz
                    if (a != 0L) {
                        copy(pointer = operand - 2)
                    } else {
                        this
                    }
                }
                4 -> { // bxc
                    copy(b = b xor c)
                }
                5 -> { // out
                    copy(out = out + (combo(operand) % 8).toInt())
                }
                6 -> { // bdv
                    copy(b = div(operand))
                }
                7 -> { // cdv
                    copy(c = div(operand))
                }
                else ->
                    throw IllegalArgumentException("Illegal opcode ${opcode}")
            }.pointerAdvance()
    }

    fun Computer.run(program: List<Int>): Computer {
        var c = this
        while (c.pointer < program.size) {
            c = c.next(program)
        }

        return c
    }

    fun List<String>.parse(): Pair<Computer, List<Int>> = this.splitBy { it == "" }.let{(registerS, programS)  ->
        val (a, b, c) = registerS.map{it.split(": ")[1].toLong()}
        val program = programS[0].split(": ")[1].split(",").map{it.toInt()}
        Computer(a, b, c) to program
    }

    fun part1(input: List<String>): String {
        val (computer, program) = input.parse()

        return computer.run(program).out.joinToString(",")
    }

    fun List<Int>.octalToLong(): Long = this.joinToString("").toLong(8)

    fun Long.toOctalString(): String {
        return this.toString(8)
    }

    data class QuineSearch(val c: Computer, val a: List<Int>) {
        fun next(aPart: Int): QuineSearch = copy(a = a + aPart)
    }

    fun QuineSearch.isQuine(program: List<Int>): Boolean {
        val result = this.c.copy(a = a.octalToLong()).run(program)

        return result.out == program.takeLast(a.size)
    }

    fun part2(input: List<String>): Long {
        val (computer, program) = input.parse()

        val start = QuineSearch(computer, listOf())

        val graph = object : Graph<QuineSearch> {
            override fun neighborsOf(node: QuineSearch): Iterable<Cost<QuineSearch>> {
                return (0..7).map { aPart ->
                    node.next(aPart)
                }.filter{it.isQuine(program)}
                    .map{Cost(it, 1)}
            }
        }

        val results = mutableListOf<QuineSearch>();

        graph.search(start, goalFunction = {
            if(it.a.size == program.size) {
                results.add(it)
            }
            false
        })

        val answers = results.map { it.a.octalToLong() }
        return answers.min()
    }

    Computer(c = 9).next(listOf(2,6)).b.assertEqual(1)
    Computer(b = 29).next(listOf(1,7)).b.assertEqual(26)
    Computer(b = 2024, c = 43690).next(listOf(4,0)).b.assertEqual(44354)
    Computer(a = 10).run(listOf(5,0,5,1,5,4)).out.assertEqual(listOf(0,1,2))
    Computer(a = 2024).run(listOf(0,1,5,4,3,0)).let{c ->
        c.a.assertEqual(0)
        c.out.assertEqual(listOf(4,2,5,6,7,7,7,7,3,1,0))
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual("4,6,3,5,6,3,5,2,1,0")

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(readInput("${day}_test2")).assertEqual(117440L)
    timeAndPrint { part2(input) }
}
