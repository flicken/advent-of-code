import SwitchType.*

enum class SwitchType {
    Broadcaster,
    FlipFlop,
    Conjunction,
}


fun main() {
    val day = "Day20"
    val High = true
    val Low = false

    data class Ping(val from: String, val to: String, val pulse: Boolean)

    data class Module(val type: SwitchType, val outputs: List<String>)

    fun buildMachine(input: List<String>): Pair<Map<String, Module>, MutableMap<String, Any>> {
        val modules = input.map {
            it.split(" -> ").let { (name, output) ->
                when (name[0]) {
                    '%' -> name.drop(1) to Module(FlipFlop, output.split(", "))
                    '&' -> name.drop(1) to Module(Conjunction, output.split(", "))
                    else -> name to Module(Broadcaster, output.split(", "))
                }
            }
        }.toMap()

        val inputs = mutableMapOf<String, MutableList<String>>()
        modules.forEach { (name, module) ->
            module.outputs.forEach { output ->
                inputs.getOrPut(output, { mutableListOf() }).add(name)
            }
        }

        val memory = mutableMapOf<String, Any>()
        modules.forEach { (name, module) ->
            when (module.type) {
                Broadcaster -> {}

                FlipFlop -> {
                    memory[name] = false
                }

                Conjunction -> {
                    memory[name] = inputs.getValue(name).associateWith { false }
                }
            }
        }
        return Pair(modules, memory)
    }

    val pushButton = Ping("button", "broadcaster", Low)

    fun runMachine(
        modules: Map<String, Module>,
        memory: MutableMap<String, Any>,
        ping: Ping,
        debug: ((Ping, Int) -> Unit),
    ): List<Ping> {
        val pulses = mutableListOf(ping)
        var count = 0
        while (count < pulses.size) {
            val next = pulses[count]
            val (from, to, pulse) = next
            debug(next, count)
            count += 1

            val module = modules[to]

            if (module == null) {
//                println("$from sent $pulse to $to")
                continue
            }

            when (module.type) {
                Broadcaster -> {
                    module.outputs.forEach { pulses.add(Ping(to, it, pulse)) }
                }

                FlipFlop -> {
                    if (pulse == Low) {
                        val newState = !(memory.getValue(to) as Boolean)
                        memory[to] = newState
                        module.outputs.forEach { pulses.add(Ping(to, it, newState)) }
                    }
                }

                Conjunction -> {
                    val state = memory.getValue(to) as MutableMap<String, Boolean>
                    state[from] = pulse
                    val newPulse = !state.values.all { it }
                    module.outputs.forEach { pulses.add(Ping(to, it, newPulse)) }
                }
            }
        }
        return pulses
    }

    fun part1(input: List<String>): Long {
        val (modules, memory) = buildMachine(input)

        val pulseCount = mutableMapOf(false to 0L, true to 0L)
        (1..1000).forEach {
            runMachine(modules, memory, pushButton) { a, b -> }.forEach {
                pulseCount[it.pulse] = pulseCount.getValue(it.pulse) + 1
            }
        }

        return pulseCount.getValue(true) * pulseCount.getValue(false)
    }

    fun part2(input: List<String>): Long {
        val (modules, memory) = buildMachine(input)

        var count = 0L
        return count
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(32000000L)

    part1(readInput("${day}_test2")).assertEqual(11687500L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

//    part2(testInput).assertEqual(-43L)
    timeAndPrint { part2(input) }
}
