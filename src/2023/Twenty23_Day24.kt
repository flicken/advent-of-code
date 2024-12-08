import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.MathContext

val mc = MathContext.DECIMAL64
val defaultMc = mc

fun gaussianElimination(
    count: Int,
    mc: MathContext = defaultMc,
    block: (Array<Array<BigDecimal>>, Array<BigDecimal>) -> Unit
): Array<BigDecimal> {
    val coefficients = Array(count) { Array(count) { BigDecimal(-1) } }
    val rhs = Array(count) { BigDecimal(-1) }

    block(coefficients, rhs)

    val varCount = coefficients.size
    for (i in 0 until varCount) {
        val pivot = coefficients[i][i]
        for (j in 0 until varCount) {
            coefficients[i][j] = coefficients[i][j].divide(pivot, mc)
        }
        rhs[i] = rhs[i].divide(pivot, mc)
        for (k in 0 until varCount) {
            if (k != i) {
                val factor = coefficients[k][i]
                for (j in 0 until varCount) {
                    coefficients[k][j] = coefficients[k][j] - factor * coefficients[i][j]
                }
                rhs[k] = rhs[k] - factor * rhs[i]
            }
        }
    }
    return rhs
}


fun main() {
    val day = "Day24"

    data class Location2D(val x: BigDecimal, val y: BigDecimal)
    data class Vector3D(val x: BigDecimal, val y: BigDecimal, val z: BigDecimal) {
        fun toLine() = "${x}, ${y}, ${z}"
        fun length() = (x * x + y * y + z * z).sqrt(mc)
        operator fun plus(o: Vector3D) = Vector3D(x + o.x, y + o.y, z + o.z)
        operator fun minus(o: Vector3D) = Vector3D(x - o.x, y - o.y, z - o.z)
        operator fun times(sc: BigDecimal) = Vector3D(sc * x, sc * y, sc * z)
    }

    data class Location3D(val x: BigDecimal, val y: BigDecimal, val z: BigDecimal) {
        fun toLine() = "${x}, ${y}, ${z}"

        fun toVector() = Vector3D(x, y, z)
        operator fun minus(o: Location3D) = Location3D(x - o.x, y - o.y, z - o.z)
    }

    fun Vector3D.toLocation() = Location3D(x, y, z)

    data class Hailstone(val p: Location3D, val v: Vector3D) {
        fun toLine() = "${p.toLine()} @ ${v.toLine()}"

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
        fun c() = m() * p.x - p.y

        private fun m() = v.y.divide(v.x, mc)
        operator fun minus(o: Hailstone) = Hailstone(p - o.p, v - o.v)
    }


    fun ClosedRange<BigDecimal>.narrowRange(
        a: Hailstone,
        b: Hailstone,
        block: (Location3D) -> BigDecimal
    ): ClosedRange<BigDecimal> {

        return listOfNotNull(
            start,
            block(a.p).takeIf { block(a.v.toLocation()) > ZERO },
            block(b.p).takeIf { block(b.v.toLocation()) > ZERO })
            .max()
            .rangeTo(
                listOfNotNull(
                    this.endInclusive,
                    block(a.p).takeIf { block(a.v.toLocation()) < ZERO },
                    block(b.p).takeIf { block(b.v.toLocation()) < ZERO },
                ).min()
            )
    }

    fun List<BigDecimal>.toLocation3D(): Location3D {
        if (size != 3) throw Exception("Must have 3 points")
        return Location3D(this[0], this[1], this[2])
    }

    fun String.toHailstone(): Hailstone {
        val (p, v) = split(" @ ").map { it.split(", ").map { it.trim().toBigDecimal() } }.map { it.toLocation3D() }
        return Hailstone(p, v.toVector())
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

    fun part2(input: List<String>, name: String): Long {
        val hailstones = input.map { it.toHailstone() }

        val roundingCx = MathContext.DECIMAL64
        val (x, y, vx, vy) = gaussianElimination(4) { coefficients, rhs ->
            for (i in 0..3) {
                val h1 = hailstones[i]
                val h2 = hailstones[i + 1]
                coefficients[i][0] = h2.v.y - h1.v.y
                coefficients[i][1] = h1.v.x - h2.v.x
                coefficients[i][2] = h1.p.y - h2.p.y
                coefficients[i][3] = h2.p.x - h1.p.x
                rhs[i] = -h1.p.x * h1.v.y + h1.p.y * h1.v.x + h2.p.x * h2.v.y - h2.p.y * h2.v.x
            }
        }.map { it.round(roundingCx) }

        val (z, vz) = gaussianElimination(2) { coefficients, rhs ->
            for (i in 0..1) {
                val h1 = hailstones[i]
                val h2 = hailstones[i + 1]
                coefficients[i][0] = h1.v.x - h2.v.x
                coefficients[i][1] = h2.p.x - h1.p.x
                rhs[i] =
                    -h1.p.x * h1.v.z + h1.p.z * h1.v.x + h2.p.x * h2.v.z - h2.p.z * h2.v.x - ((h2.v.z - h1.v.z) * x) - ((h1.p.z - h2.p.z) * vx)
            }
        }

        val rock = Hailstone(Location3D(x, y, z), Vector3D(vx, vy, vz))

        return (rock.p.x + rock.p.y + rock.p.z).toLong()
    }

    val testInput = readInput("${day}_test")
    part1(testInput, BigDecimal(7).rangeTo(BigDecimal(27))).assertEqual(2L)

    val input = readInput(day)

    timeAndPrint { part1(input, BigDecimal(200000000000000L).rangeTo(BigDecimal(400000000000000L))) }

    timeAndPrint { part2(testInput, "test") }//.assertEqual(47L)
    timeAndPrint { part2(input, "real") }
}


