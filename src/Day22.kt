fun main() {
    val day = "Day22"

    fun <T, I> Iterable<T>.chunked(chunkIndicator: (T) -> I): Iterable<List<T>> {
        val underlyingSequence = this
        return sequence {
            val buffer = mutableListOf<T>()
            var lastPredicate: I? = null

            for (current in underlyingSequence) {
                val curPredicate = chunkIndicator(current)
                if (lastPredicate != curPredicate && buffer.isNotEmpty()) {
                    yield(buffer.toList())
                    buffer.clear()
                }
                buffer.add(current)
                lastPredicate = curPredicate
            }
            if (buffer.isNotEmpty()) {
                yield(buffer)
            }
        }.asIterable()
    }

    data class Location3D(val x: Int, val y: Int, val z: Int) {
        override fun toString(): String = "$x,$y,$z"
    }

    data class Block(val id: Int, val a: Location3D, val b: Location3D) {
        fun locations3D(): List<Location3D> = xRange().flatMap { x ->
            yRange().flatMap { y ->
                zRange().map { z -> Location3D(x, y, z) }
            }
        }

        private fun rangeOf(a: Int, b: Int): IntRange = if (a < b) a..b else b..a

        fun xRange() = rangeOf(a.x, b.x)
        fun yRange() = rangeOf(a.y, b.y)
        fun zRange() = rangeOf(a.z, b.z)

        fun atZ(z: Int) = this.copy(a = a.copy(z = z), b = b.copy(z = b.z - (a.z - z)))
    }

    fun String.toBlock(i: Int): Block {
        return split("~").map {
            it.split(",").let {
                it.map { it.toInt() }.let { (x, y, z) -> Location3D(x, y, z) }
            }
        }.let { (a, b) -> Block(i, a, b) }
    }

    operator fun <T> Array<Array<T>>.get(it: Location): T = this[it.row][it.col]
    operator fun <T> Array<Array<T>>.set(it: Location, v: T) {
        this[it.row][it.col] = v
    }

    fun allFallDown(blocks: List<Block>): MutableMap<Int, Set<Int>> {
        val fallen = mutableMapOf<Location3D, Int>()
        val blockRestsOn = mutableMapOf<Int, Set<Int>>()

        blocks.forEach { block ->
            var z = block.zRange().first
            while (z > 1 && block.atZ(z - 1).locations3D().all { !fallen.contains(it) }) {
                z -= 1
            }

            val fallenBlock = block.atZ(z)

            blockRestsOn[fallenBlock.id] = fallenBlock.atZ(z - 1).locations3D().mapNotNull { fallen[it] }.toSet()
            fallen.putAll(fallenBlock.locations3D().associateWith { fallenBlock.id })
        }
        return blockRestsOn
    }

    fun countFallen(blockRestsOn: MutableMap<Int, Set<Int>>, disintegratingBlock: Int): Int {
        val supportedBy = blockRestsOn.filter { it.value.isNotEmpty() }.toMutableMap()
        val gone = mutableSetOf<Int>()

        var going = setOf(disintegratingBlock)
        while (going.isNotEmpty()) {
            supportedBy -= going
            gone += going

            going = supportedBy.filter { (_, supports) -> supports.all { gone.contains(it) } }.keys
        }

        return gone.count() - 1
    }

    fun part1(input: List<String>): Long {
        val blocks = input.mapIndexed { i, line -> line.toBlock(i) }.sortedBy { it.a.z }
        val blockRestsOn = allFallDown(blocks)

        return blocks.indices.count { countFallen(blockRestsOn, it) == 0 }.toLong()
    }

    fun part2(input: List<String>): Long {
        val blocks = input.mapIndexed { i, line -> line.toBlock(i) }.sortedBy { it.a.z }
        val blockRestsOn = allFallDown(blocks)

        return blocks.indices.map { countFallen(blockRestsOn, it) }.sum().toLong()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(5L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(7L)
    timeAndPrint { part2(input) }
}



