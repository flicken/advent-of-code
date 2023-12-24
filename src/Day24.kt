import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.MathContext
import java.math.RoundingMode

fun main() {
    val day = "Day24"

    var mc = MathContext(10, RoundingMode.FLOOR)

    data class Location2D(val x: BigDecimal, val y: BigDecimal)
    data class Location3D(val x: BigDecimal, val y: BigDecimal, val z: BigDecimal) {
        fun toLine() = "${x}, ${y}, ${z}"
    }

    data class Hailstone(val p: Location3D, val v: Location3D) {
        fun toLine() = "${p.toLine()} @ ${v.toLine()}"
        fun xAt(y: BigDecimal): BigDecimal = divM() * (y - p.y) + p.x
        fun yAt(x: BigDecimal): BigDecimal {
            return m() * (x - p.x) + p.y
        }

        fun intersection(
            o: Hailstone
        ): Location2D {
            val divisor = a() - o.a()
            val x = (o.c() - c()).divide(divisor, mc)
            val y = ((c() * o.a()) - (o.c() * a())).divide(divisor, mc)
            return Location2D(x, y)
        }

        fun parallel(o: Hailstone): Boolean = o.m().equals(m())

        fun a() = -(v.y.divide(v.x, mc))
        fun b() = BigDecimal(1.0)
        fun c() = m() * p.x - p.y

        private fun m() = v.y.divide(v.x, mc)
        private fun divM() = v.x.divide(v.y, mc)
    }

    fun ClosedRange<BigDecimal>.narrowRange(
        a: Hailstone,
        b: Hailstone,
        block: (Location3D) -> BigDecimal
    ): ClosedRange<BigDecimal> {

        return listOfNotNull(
            this.start,
            block(a.p).takeIf { block(a.v) > ZERO },
            block(b.p).takeIf { block(b.v) > ZERO })
            .max()
            .rangeTo(
                listOfNotNull(
                    this.endInclusive,
                    block(a.p).takeIf { block(a.v) < ZERO },
                    block(b.p).takeIf { block(b.v) < ZERO },
                ).min()
            )
    }

    fun List<BigDecimal>.toLocation3D(): Location3D {
        if (size != 3) throw Exception("Must have 3 points")
        return Location3D(this[0], this[1], this[2])
    }

    fun String.toHailstone(): Hailstone {
        val (p, v) = split(" @ ").map { it.split(", ").map { it.trim().toBigDecimal() } }.map { it.toLocation3D() }
        return Hailstone(p, v)
    }

    fun <T> List<T>.allPairs(): Sequence<Pair<T, T>> {
        val list = this
        return sequence {
            list.indices.forEach { i ->
                for (j in i + 1 until list.size)
                    yield(list[i] to list[j])
            }
        }
    }


    fun part1(input: List<String>, testArea: ClosedRange<BigDecimal>): Long {
        val hailstones = input.map { it.toHailstone() }
        return hailstones.allPairs().mapIndexed { i, (a, b) ->
            if (a.parallel(b)) {
                false
            } else {
                val intersection = a.intersection(b)

                val xRange = testArea.narrowRange(a, b) { it.x }
                val yRange = testArea.narrowRange(a, b) { it.y }

                intersection.x in xRange && intersection.y in yRange
            }
        }.count { it }.toLong()
    }

    fun part2(input: List<String>): Long {
        return -1
    }

    val testInput = readInput("${day}_test")
    part1(testInput, BigDecimal(7).rangeTo(BigDecimal(27))).assertEqual(2L)

    val input = readInput(day)

    timeAndPrint { part1(input, BigDecimal(200000000000000L).rangeTo(BigDecimal(400000000000000L))) }

    part2(testInput).assertEqual(-43L)
    timeAndPrint { part2(input) }
}


