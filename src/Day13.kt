import Direction.*
import java.math.BigInteger


fun main() {
    val day = "Day13"

    data class LocationL(val row: Long, val col: Long) {
        fun go(direction: Direction) = when (direction) {
            Up -> this.copy(row = this.row - 1)
            Down -> this.copy(row = this.row + 1)
            Left -> this.copy(col = this.col - 1)
            Right -> this.copy(col = this.col + 1)
        }
    }

    operator fun LocationL.plus(b: LocationL) = LocationL(this.row + b.row, this.col + b.col)
    operator fun LocationL.times(m: Long): LocationL = LocationL(this.row * m, this.col * m)

    data class Machine(val a: LocationL, val b: LocationL, val prize: LocationL)

    fun List<String>.parse() = this.windowed(3, 4, true).map {
        it.map { it.split(Regex("[:,] ")).drop(1).map { it.substring(2).toLong() } }.map { LocationL(it[0], it[1]) }
    }.map { Machine(it[0], it[1], it[2]) }


    data class SearchState(val location: LocationL, val aCount: Long = 0, val bCount: Long = 0) {
        fun a(m: Machine, steps: Long = 1): SearchState =
            copy(location = location + (m.a * steps), aCount = aCount + steps)

        fun b(m: Machine, steps: Long = 1): SearchState =
            copy(location = location + (m.b * steps), bCount = bCount + steps)

        fun isValid(m: Machine): Boolean = location.col <= m.prize.col &&
                location.row <= m.prize.row

        fun atPrize(m: Machine): Boolean = m.prize == location
    }

    fun toBigInt(a1: LocationL) = BigInteger.valueOf(a1.row) to BigInteger.valueOf(a1.col)

    fun Machine.solve(): Long {
        val (ya, xa) = toBigInt(this.a)
        val (yb, xb) = toBigInt(this.b)
        val (y, x) = toBigInt(this.prize)
        val top = xb * y - x * yb
        val bottom = xb * ya - xa * yb

        if (top.signum() == bottom.signum() && (top % bottom) == BigInteger.valueOf(0)) {
            val a = (top / bottom)
            val legitB = (y - a * ya) % yb == BigInteger.ZERO
            val b = (y - a * ya) / yb
            val result = (a * BigInteger.valueOf(3) + b)

            if (legitB && a >= BigInteger.ZERO && b >= BigInteger.ZERO && result >= BigInteger.ZERO) {
                return result.toLong()
            }
        }

        return 0L
    }


    fun part1(input: List<String>): Long {
        return input.parse().map { m ->
            val graph = object : Graph<SearchState> {
                override fun neighborsOf(node: SearchState): Iterable<Cost<SearchState>> =
                    listOf(Cost(node.a(m), 3), Cost(node.b(m), 1)).filter { it.first.isValid(m) }
            }

            val result = graph.search(SearchState(LocationL(0, 0)), 400, goalFunction = { it.atPrize(m) })

            if (result.destination != null) result.destination.aCount * 3L + result.destination.bCount else 0L
        }.sum()
    }

    fun part2(input: List<String>): Long {
        return input.parse()
            .map { it.copy(prize = it.prize + LocationL(10000000000000L, 10000000000000L)) }
            .sumOf { it.solve()}
    }

    val testInput = readInput("${day}_test")
    val input = readInput(day)

    part1(testInput).assertEqual(480L)

    timeAndPrint { part1(input) }

    val part2TestResult = timeAndPrint { part2(testInput) }
    if (part2TestResult < 400L) {
        throw IllegalStateException("Must be bigger than part 1")
    }

    timeAndPrint { part2(input) }.assertEqual(75200131617108L)
}
