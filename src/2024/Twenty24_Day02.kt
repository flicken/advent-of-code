fun main() {
    val day = "Day02"

    fun Iterable<Int>.isSafe(): Boolean {
        val diffs = this.windowed(2, 1).map { (a, b) -> a - b }
        return (diffs.all { it == 1 || it == 2 || it == 3 }) || diffs.all { it == -1 || it == -2 || it == -3 }
    }

    fun <T> Iterable<T>.removeIt(i: Int) = this.toMutableList().tap { it.removeAt(i) }
    fun <T> Iterable<T>.anyIndex(f: (Int) -> Boolean)= this.withIndex().any { f(it.index) }
    fun Iterable<Int>.isSafeDroppingOne()=  this.isSafe() || this.anyIndex{this.removeIt(it).isSafe()}
    fun String.toReport() = this.split(" ").map{it.toInt()}

    fun part1(input: List<String>): Long {
        return input.map{it.toReport()}.count{it.isSafe()}.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.map{it.toReport()}.count{it.isSafeDroppingOne()}.toLong()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(2L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(4L)
    timeAndPrint { part2(input) }
}

