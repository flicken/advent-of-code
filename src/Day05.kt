fun toRange(src: Long, len: Long) = LongRange(src, src + len - 1)


/*
    a....b
       c....d
    a..c, c..b, b..d

    a...b
           c...d
    a...b, empty, c...d

       a...b
     c.......d

 */

data class RangeOverlap(val overlap: LongRange?, val remaining: List<LongRange>)

fun LongRange.overlap(that: LongRange): LongRange {
    return LongRange(this.first.coerceAtLeast(that.first), this.last.coerceAtMost(that.last));
}

fun LongRange.rangeOverlap(that: LongRange): RangeOverlap {
    val overlap = this.overlap(that);

    if (overlap.isEmpty()) {
        return RangeOverlap(null, listOf(that));
    } else {
        val remaining = mutableListOf<LongRange>();
        if (that.first < this.first) {
            remaining.add(LongRange(that.first, this.first - 1))
        }
        if (that.last > this.last) {
            remaining.add(LongRange(this.last + 1, that.last))
        }
        return RangeOverlap(overlap, remaining)
    }
}

fun testRangeOverlap() {
    LongRange(-100, -1).rangeOverlap(LongRange(0, 10)).assertEqual(RangeOverlap(null, listOf(LongRange(0, 10))))
    LongRange(-1, 5).rangeOverlap(LongRange(0, 10)).assertEqual(RangeOverlap(LongRange(0, 5), listOf(LongRange(6, 10))))
    LongRange(-1, 100).rangeOverlap(LongRange(0, 10)).assertEqual(RangeOverlap(LongRange(0, 10), listOf()))
    LongRange(-1, 9).rangeOverlap(LongRange(0, 10))
        .assertEqual(RangeOverlap(LongRange(0, 9), listOf(LongRange(10, 10))))
    LongRange(4, 6).rangeOverlap(LongRange(0, 10))
        .assertEqual(RangeOverlap(LongRange(4, 6), listOf(LongRange(0, 3), LongRange(7, 10))))
    LongRange(5, 100).rangeOverlap(LongRange(0, 10))
        .assertEqual(RangeOverlap(LongRange(5, 10), listOf(LongRange(0, 4))))
    LongRange(11, 100).rangeOverlap(LongRange(0, 10))
        .assertEqual(RangeOverlap(null, listOf(LongRange(0, 10))))
}

fun Long.toDest(src: Long, dst: Long) = this + (dst - src)

fun LongRange.toDest(src: LongRange, dst: LongRange): LongRange {
    return LongRange(
        this.start.toDest(src.start, dst.start),
        this.endInclusive.toDest(src.endInclusive, dst.endInclusive)
    )
}


fun main() {
    testRangeOverlap()
    val day = "Day05"


    fun part1(input: List<String>): Long {
        val values = input[0].substringAfter(": ").split(" ").map { it.toLong() }
        val inputIt = input.drop(1).listIterator();

        values.println()

        var valuesMap = values.associateWith { it }.toMutableMap()

        inputIt.forEachRemaining {
            if (it.endsWith("map:")) {
                it.println()
                valuesMap.println()

                val values = valuesMap.values.toTypedArray();
                valuesMap.clear();
                values.forEach { v -> valuesMap.put(v, v) }
            } else if (it.trim().isEmpty()) {
                // do nothing
            } else {
                val (dst, src, len) = it.split(" ").map { it.toLong() };
                val srcRange = toRange(src, len)

                valuesMap.forEach { (valueSrc) ->
                    if (srcRange.contains(valueSrc)) {
                        val valueDest = valueSrc.toDest(src, dst)
                        println("\t${valueSrc} -> ${valueDest}")
                        valuesMap[valueSrc] = valueDest;
                    }
                }
            }
        }

        return valuesMap.values.min();
    }

    fun part2(input: List<String>): Long {
        val values = input[0].substringAfter(": ").split(" ").map { it.toLong() }.chunked(2).map { (src, len) ->
            toRange(src, len)
        }
        val inputIt = input.drop(1).listIterator();

        values.println()

        var valuesMap = values.associateWith { it }.toMutableMap()

        inputIt.forEachRemaining {
            if (it.endsWith("map:")) {
                it.println()
                valuesMap.println()

                val values = valuesMap.values.toTypedArray();
                valuesMap.clear();
                values.forEach { v -> valuesMap.put(v, v) }
            } else if (it.trim().isEmpty()) {
                // do nothing
            } else {
                val (dst, src, len) = it.split(" ").map { it.toLong() };
                val srcRange = toRange(src, len)
                val dstRange = toRange(dst, len);

                valuesMap.keys.toTypedArray().forEach { valueSrc ->
                    val rangeOverlap = srcRange.rangeOverlap(valueSrc)
                    if (rangeOverlap.overlap != null) {
                        println("\t${valueSrc} -> ${rangeOverlap}")
                        valuesMap.remove(valueSrc)
                        valuesMap[rangeOverlap.overlap] = rangeOverlap.overlap.toDest(srcRange, dstRange)
                        rangeOverlap.remaining.forEach {
                            valuesMap[it] = it
                        }
                    }
                }
            }
        }

        return valuesMap.values.minOf { range -> range.start };
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(35L)

    val input = readInput("${day}")
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(46L)
    timeAndPrint { part2(input) }
}
