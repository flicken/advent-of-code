fun main() {
    val day = "Day15"

    fun String.hashAlgorithm(): Int {
        var hash = 0
        this.forEach {
            hash += it.code
            hash *= 17
            hash %= 256
        }
        return hash
    }

    fun part1(input: List<String>): Long = input.sumOf { it.hashAlgorithm() }.toLong()

    data class Lens(val label: String, val focalLength: Int)

    fun part2(input: List<String>): Long {
        val boxes = MutableList(256) { mutableListOf<Lens>() }
        input.forEach { instruction ->
            val (label, maybeFocalLength) = instruction.split("-", "=")
            val operation = instruction[label.length]
            val hash = label.hashAlgorithm()
            when (operation) {
                '-' -> {
                    boxes[hash] = boxes[hash].filter { l -> l.label != label }.toMutableList()
                }

                '=' -> {
                    val lens = Lens(label, maybeFocalLength.toInt())
                    val box = boxes[hash]
                    when (val index = box.indexOfFirst { l -> l.label == lens.label }) {
                        -1 -> box.add(lens)
                        else -> box[index] = lens
                    }
                }

                else -> throw Exception("Unhandled operation ${operation}")
            }
        }

        return boxes.mapIndexed { boxIndex, box ->
            box.mapIndexed { lensIndex, lens ->
                (boxIndex + 1) * (lensIndex + 1) * lens.focalLength
            }.sum().toLong()
        }.sum()
    }

    fun List<String>.toInstructions() = joinToString("").split(",")

    val testInput = readInput("${day}_test").toInstructions()
    part1(testInput).assertEqual(1320L)

    val input = readInput(day).toInstructions()
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(145)
    timeAndPrint { part2(input) }
}
