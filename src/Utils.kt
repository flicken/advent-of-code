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

// inline fun <T> measureTimedValue(
//    block: () -> T
//): TimedValue<T>
fun <T> timeAndPrint(block: () -> T): T {
    val (value, timeTaken) = measureTimedValue(block);
    println("${value} in ${timeTaken}")
    return value;
}