import java.util.*

fun main() {
    val day = "Day09"


    fun List<Int?>.show(): String = this.map{ if (it is Int){ it.toString()}else {"." } }.joinToString ("")
    fun List<Int?>.checksum(): Long = this.mapIndexed { index, fileId ->
        index.toLong() * (fileId ?: 0).toLong()
    }.sum()


    fun MutableList<Int?>.defrag() {
        var left = 0
        var right = this.size - 1
        while (left < right) {
            if (this[left] == null && this[right] != null) {
                Collections.swap(this, left, right)
            }
            if (this[left] != null) {
                left += 1
            }
            if (this[right] == null) {
                right -= 1
            }
        }
    }


    fun List<String>.parseDisk(): MutableList<Int?> {
        val disk = mutableListOf<Int?>()
        this[0].map { it.toString().toInt() }.windowed(size = 2, step = 2, partialWindows = true)
            .mapIndexed { id, sizes ->
                disk.addAll(List(sizes[0]) { id })
                if (sizes.size == 2) {
                    disk.addAll(List(sizes[1]) { null })
                }
            }
        return disk
    }

    fun part1(input: List<String>): Long {
        val disk = input.parseDisk()

        disk.defrag()

        return disk.checksum()
    }

    data class Space(val id: Int?, val size: Int) {
        fun isFree(): Boolean = id == null
        fun isFile(): Boolean = !isFree()
    }


    fun MutableList<Space>.toReal() = this.flatMap{ space -> List(space.size){space.id} }

    fun MutableList<Space>.moveFile(fileIndex: Int, freeIndex: Int) {
        val file = this[fileIndex]
        this[fileIndex] = file.copy(id = null)
        val freeSpace = this[freeIndex]
        this[freeIndex] = file
        this.add(freeIndex + 1, freeSpace.copy(size = freeSpace.size - file.size))
    }

    fun MutableList<Space>.defrag() {
        var right = this.size - 1
        while (right > 0) {
            val space = this[right]
            if (space.isFile()) {
                val freeIndex = this.subList(0, right).indexOfFirst { it.isFree() && it.size >= space.size }
                if (freeIndex > 0) {
                    moveFile(right, freeIndex)
                } else {
                    right -= 1
                }
            } else {
                right -= 1
            }
        }
    }

    fun part2(input: List<String>): Long {
        val virtualDisk = mutableListOf<Space>()
        input[0].map { it.toString().toInt() }.windowed(size = 2, step = 2, partialWindows = true)
            .mapIndexed { id, sizes ->
                virtualDisk.add(Space(id, sizes[0]))
                if (sizes.size == 2) {
                    virtualDisk.add(Space(null, sizes[1]))
                }
            }

        virtualDisk.defrag()

        return virtualDisk.toReal().checksum()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(1928L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(2858L)
    timeAndPrint { part2(input) }
}
