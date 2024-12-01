import SwitchType.*

enum class SwitchType {
    Broadcaster,
    FlipFlop,
    Conjunction,
}


typealias Memory = MutableMap<String, MutableMap<String, Boolean>>

fun main() {
    val day = "Day20"
    val High = true
    val Low = false

    data class Ping(val from: String, val to: String, val pulse: Boolean)

    data class Module(val type: SwitchType, val outputs: List<String>)

    fun buildMachine(input: List<String>): Pair<Map<String, Module>, Memory> {
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

        val memory: Memory = mutableMapOf()
        modules.forEach { (name, module) ->
            when (module.type) {
                Broadcaster -> {}

                FlipFlop -> {
                    memory[name] = mutableMapOf(name to false)
                }

                Conjunction -> {
                    memory[name] = inputs.getValue(name).associateWith { false }.toMutableMap()
                }
            }
        }
        return Pair(modules, memory)
    }

    val pushButton = Ping("button", "broadcaster", Low)

    fun runMachine(
        modules: Map<String, Module>,
        memory: Memory,
        ping: Ping,
        debug: ((Ping, Int) -> Unit) = { _, _ -> },
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
                continue
            }

            when (module.type) {
                Broadcaster -> {
                    module.outputs.forEach { pulses.add(Ping(to, it, pulse)) }
                }

                FlipFlop -> {
                    if (pulse == Low) {
                        val newState = !(memory.getValue(to).getValue(to))
                        memory.getValue(to)[to] = newState
                        module.outputs.forEach { pulses.add(Ping(to, it, newState)) }
                    }
                }

                Conjunction -> {
                    val state = memory.getValue(to)
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

        val lookAt = "cn"
        val inputs = memory.getValue(lookAt).keys

        var cycleCount = 0L
        val firstHighPulse = mutableMapOf<String, Long>()
        while (firstHighPulse.size < inputs.size) {
            cycleCount += 1
            runMachine(modules, memory, pushButton)
                .filter { p -> p.to == lookAt && p.pulse == High }
                .forEach { p ->
                    firstHighPulse.putIfAbsent(p.from, cycleCount)
                }
        }

        return firstHighPulse.values.lcm()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(32000000L)

    part1(readInput("${day}_test2")).assertEqual(11687500L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    timeAndPrint { part2(input) }
}
