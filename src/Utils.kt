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

fun <T : Any> T.assertEqual(that: T): T {
    check(this == that) { "Got ${this}, but expected ${that}" }
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


// From https://stackoverflow.com/a/76533918
fun <T> List<List<T>>.transpose(): List<List<T>> {
    return (this[0].indices).map { i -> (this.indices).map { j -> this[j][i] } }
}

inline fun <reified T> Array<Array<T>>.transpose(): Array<Array<T>> {
    return Array(this[0].size) { i -> Array(this.size) { j -> this[j][i] } }
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