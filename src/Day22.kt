fun main() {
    val day = "Day22"

    val prune = 16777216L

    fun nextRandom(seed: Long): Long {
        val step1 = ((seed * (64)) xor seed) % prune
        val step2 = ((step1 / (32)) xor step1) % prune
        return ((step2 * (2048)) xor step2) % prune
    }

    fun part1(input: List<String>): Long {
        return input.map { startingSeed ->
            (1..2000).fold(startingSeed.toLong(), { seed, _count ->
                nextRandom(seed)
            }).toLong()
        }.sum()
    }

    fun part2(input: List<String>): Long {
        val monkeysPrices = input.map{secretS ->
            var secret = secretS.toLong()
            (1..2000).fold(mutableListOf<Long>(), { acc, _count ->
                val next = nextRandom(secret)
                val nextPrice = (next % 10)
                acc += nextPrice

                secret = next
                acc
            })
        }

        val howMuch = mutableMapOf<List<Long>, List<Long>>()

        monkeysPrices.mapIndexed { i, monkeyPrices ->
            val monkeyMuch = mutableMapOf<List<Long>, Long>()
            monkeyPrices.windowed(5, 1).map{ seq ->
                seq.windowed(2, 1).map{it.last() - it.first()} to seq.last()
            }.filterNot{it.first.contains(0)}.forEach {
                if (!monkeyMuch.contains(it.first)) {
                    monkeyMuch[it.first] = it.second
                }
            }
            monkeyMuch
        }.forEach { monkeyMuch ->
            monkeyMuch.forEach{
                howMuch[it.key] = (howMuch[it.key]?: listOf()) + it.value
            }
        }

        return howMuch.maxBy { it.value.sum() }.p().value.sum()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(37327623L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(readInput("${day}_test2")).assertEqual(23L)
    timeAndPrint { part2(input) }
}
