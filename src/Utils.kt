import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T : Any, R> T.tap(tap: (T) -> R): T {
    tap(this)
    return this
}

fun <T : Any> T.p(): T {
    println(this)
    return this
}

var shouldDebug: Boolean = System.getProperty("debug").toBoolean()

fun debug(s: Any?) {
    if (shouldDebug) {
        println(s)
    }
}

fun Any?.db() = debug(this)

fun <T : Any> T.assertEqual(that: T): T {
    check(this == that) { "Got ${this}, but expected ${that}" }
    return this;
}

fun <T : Comparable<T>> T.assertLessThan(that: T): T {
    check(this < that) { "Got ${this}, but expected less than ${that}" }
    return this;
}

fun <T> List<T>.replaceAt(index: Int, block: (T) -> T): List<T> {
    return this.mapIndexed { row, line ->
        if (row == index) block(line) else line
    }
}

// inline fun <T> measureTimedValue(
//    block: () -> T
//): TimedValue<T>
fun <T> timeAndPrint(block: () -> T): T {
    val (value, timeTaken) = measureTimedValue(block);
    println("${value} in ${timeTaken}")
    return value;
}

fun List<String>.transpose(): List<String> {
    val a = Array(this[0].length) { CharArray(size) }
    indices.forEach { y -> (0..<this[0].length).forEach { x -> a[x][y] = this[y][x] } }
    return a.map { it.joinToString("") }
}

fun List<CharArray>.transposeCharArray(): List<CharArray> {
    val a = List(this[0].size) { CharArray(size) }
    indices.forEach { y -> (0..<this[0].size).forEach { x -> a[x][y] = this[y][x] } }
    return a
}

fun <T> List<T>.splitBy(block: (T) -> Boolean): List<List<T>> {
    val iter = this.iterator()
    return generateSequence {
        if (iter.hasNext()) {
            generateSequence { if (iter.hasNext()) iter.next().takeUnless(block) else null }.toList()
        } else {
            null
        }
    }.toList()
}

fun String.splitAt(n: Int): Pair<String, String> {
    assert(n >= 0, { "${n} must be a positive number" })
    return Pair(this.take(n), this.drop(n))
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
