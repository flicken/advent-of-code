import java.util.*

fun main() {
    val day = "Day25"

    fun cut(machine: MutableMap<String, MutableList<String>>, pair: Pair<String, String>) {
        val (a, b) = pair
        machine.getValue(a).remove(b)
        machine.getValue(b).remove(a)
    }

    fun part1(input: List<String>, cuts: List<Pair<String, String>>): Long {
        val machine = mutableMapOf<String, MutableList<String>>()
        input.forEach { line ->
            line.split(": ")
                .let { (a, connectionsS) ->
                    connectionsS.split(" ").forEach { b ->
                        machine.getOrPut(a) { mutableListOf() }.add(b)
                        machine.getOrPut(b) { mutableListOf() }.add(a)
//                        println("$a -- $b") // Graphviz dot format output
                    }
                }
        }

        // Determined via graphviz
        cuts.forEach { cut(machine, it) }

        val queue: Queue<String> = LinkedList()
        val visited = mutableSetOf<String>()

        queue.add(cuts[0].first)

        while (queue.isNotEmpty()) {
            val node = queue.remove()

            machine.getValue(node).forEach {
                if (!visited.contains(it)) {
                    queue.add(it)
                }
            }

            visited.add(node)
        }

        return ((machine.size - visited.size) * visited.size).toLong()
    }

    fun part2(input: List<String>): Long {
        return -1
    }

    val testInput = readInput("${day}_test")
    part1(testInput, listOf("hfx" to "pzl", "bvb" to "cmg", "nvd" to "jqt")).assertEqual(54L)

    val input = readInput(day)
    // Cuts determined via graphviz layout algorithm sfdp
    timeAndPrint { part1(input, listOf("qnd" to "mbk", "rrl" to "pcs", "ddl" to "lcm")) }

    part2(testInput).assertEqual(-43L)
    timeAndPrint { part2(input) }
}
