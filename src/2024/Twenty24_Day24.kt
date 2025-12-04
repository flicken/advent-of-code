import java.io.File
import java.util.*

fun main() {
    val day = "Day24"


    data class Gate(val inputs: List<String>, val operation: String, val output: String)

    fun Map<String, Boolean>.show(): String =
        this.toSortedMap().map {
            ("${it.key}: ${ if (it.value) "1" else "0"}")
        }.joinToString("\n")


    fun SortedMap<String, Boolean>.binaryValueOf(prefix: String) =
        this.filter { it.key.startsWith(prefix) }
            .values
            .reversed()
            .fold(0L, { acc, v -> (acc shl 1) + if (v) 1 else 0 })

    fun List<String>.parse(): Pair<SortedMap<String, Boolean>, List<Gate>> {
        return this.splitBy { it == "" }.let { (valuesS, gatesS) ->
            val values = valuesS.map { it.split(": ").let { (gate, binS) -> gate to (binS == "1") } }
                .toMap().toMutableMap().toSortedMap()
            val gates = gatesS.map {
                it.split(" ").let {
                    Gate(listOf(it[0], it[2]), it[1], it[4])
                }
            }

            values to gates
        }
    }

    fun List<Gate>.runMachine(inValues: SortedMap<String, Boolean>): SortedMap<String, Boolean> {
//        println("Running")
        val values = TreeMap(inValues)
        val newInputs = TreeSet<String>()
        newInputs.addAll(values.keys.sorted())

        val outputsInOrder = mutableListOf<String>()

        while (newInputs.isNotEmpty()) {
            val input = newInputs.removeFirst()

            this.filter { it.inputs.contains(input) }
                .filter { it.inputs.all { values.containsKey(it) } }
                .forEach {
                    if (!outputsInOrder.contains(it.output)) {
                        val inputValues = it.inputs.map { values.getValue(it) }
                        val output = when (it.operation) {
                            "AND" -> inputValues[0] and inputValues[1]
                            "OR" -> inputValues[0] or inputValues[1]
                            "XOR" -> inputValues[0] xor inputValues[1]
                            else -> throw IllegalStateException(it.operation)
                        }
                        values[it.output] = output
                        newInputs.add(it.output)
                        outputsInOrder.add(it.output)
                    }
                }
        }

//        outputsInOrder.forEach{it.p()}

        return values
    }

    fun part1(input: List<String>): Long {
        val (values, gates) = input.parse()

        return gates.runMachine(values).binaryValueOf("z")
    }

    fun List<Gate>.swap(o: Pair<String, String>): List<Gate> =
        this.map {
            val (o1, o2) = o
            if (it.output == o1) {
                it.copy(output = o2)
            } else if (it.output == o2) {
                it.copy(output = o1)
            } else {
                it
            }
        }

    fun List<Gate>.dot(): String {
        return "digraph Machine {\n" +
        this.map {
            "${it.inputs[0]} -> ${it.output} [label=\"${it.operation}\"]; \n" +
            "${it.inputs[1]} -> ${it.output} [label=\"${it.operation}\"]; \n"
        }.joinToString("") + "\n}"
    }


    fun part2(input: List<String>,
              zExpectedF: (Long, Long) -> Long,
              zExpectedInputsF: (Int) -> List<Int>): String {
        val (values, gates) = input.parse()

        val x = values.binaryValueOf("x")
        val y = values.binaryValueOf("y")
        val expectedZ = zExpectedF(x, y)

        val swaps = listOf<Pair<String, String>>(
            // iteratively manually determine swaps by inspection in graph
        )

        val newGates = swaps.fold(gates, {acc, entry -> acc.swap(entry.first to entry.second)})

        val result = newGates.runMachine(values).binaryValueOf("z")

        println("${x} <op> ${y} = ${result} ?= ${expectedZ}")
        println("\txor:          ${(result xor expectedZ).toString(2)}")
        println("\tfirst broken: ${(result xor expectedZ).toString(2).reversed().indexOfFirst { it == '1' }}")

        println("Writing dot file")
        File("part2.dot").writeText(newGates.dot())

        val gateByOutput = newGates.map { it.output to it }.toMap()

        newGates.map{it.output}.filter{it.startsWith("z")}.sorted().dropLast(1).map { o ->
            // find previous
            val visited = mutableListOf<String>()
            val toVisit = TreeSet<String>()
            toVisit.add(o)
            while (toVisit.isNotEmpty()) {
                val node = toVisit.removeFirst()
                visited.add(node)
                val gate = gateByOutput[node]
                if (gate !== null) {
                    toVisit.addAll(gate.inputs)
                }
            }
            // Each z## needs to have x + y ## .. 1 as ancestors
            val xs = visited.filter { it.startsWith("x") }.map{it.drop(1).toInt()}.toMutableList()
            val ys = visited.filter { it.startsWith("y") }.map{it.drop(1).toInt()}.toMutableList()
            val z = o.drop(1).toInt()
            val expectedZs = zExpectedInputsF(z)
            val missingXs = expectedZs.toMutableList()
            missingXs.removeAll(xs)
            val missingYs = expectedZs.toMutableList()
            missingYs.removeAll(ys)

            if (missingXs.isNotEmpty() || missingYs.isNotEmpty()) {
                println("${o}: ${missingXs} ${missingYs}")
            }
        }

        return swaps.flatMap{it.toList()}.sorted().joinToString(",")
    }

    fun part20(input: List<String>): String {
        val (values, gates) = input.parse()

        val x = values.binaryValueOf("x")
        val y = values.binaryValueOf("y")
        val expectedZ = x and y

        gates.allPairs().filter { (gate1, gate2 ) ->
            val newGates = mutableListOf<Gate>()
            newGates.addAll(gates)
            newGates[newGates.indexOf(gate1)] = gate1.copy(output = gate2.output)
            newGates[newGates.indexOf(gate2)] = gate2.copy(output = gate1.output)

            val output = newGates.runMachine(values)
            output.binaryValueOf("z") == expectedZ
        }.toList().p()

//        output.show().p()

        return ""
    }

    val testInput0 = readInput("${day}_test0")
    part1(testInput0).assertEqual(4L)

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(2024L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    timeAndPrint { part2(readInput("${day}_test2"), { a, b -> a and b}, {z -> listOf(z) }) }

    timeAndPrint { part2(input, { a, b -> a + b }, {z -> (0..z).toList()}) }
}
