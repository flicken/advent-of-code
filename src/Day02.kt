fun main() {
    val day = "Day02"

    fun part1(input: List<String>): Long {
        val ranges = input[0].split(",").map{ range -> range.split("-").map{n -> n.toLong()}}.map{ (start, end) ->
            (start..end)
        }

        val maxNumber = ranges.map{it.last }.max()
        val maxDigits = maxNumber.toString().length / 2

        val illegalNumbers = (1.."1${"0".repeat(maxDigits)}".toInt()).map{ n ->
            "${n}${n}".toLong()
        }.filter{ possiblyIllegal -> ranges.any { r -> r.contains(possiblyIllegal) } }

        return illegalNumbers.sum()
    }

    fun repeatedStrings(x: String, maxLength: Int): List<String> =
        generateSequence(x.repeat(2)) { it + x }
            .takeWhile { it.length <= maxLength }
            .toList()

    fun part2(input: List<String>): Long {
        val ranges = input[0].split(",").map{ range -> range.split("-").map{n -> n.toLong()}}.map{ (start, end) ->
            (start..end)
        }

        val maxNumber = ranges.map{it.last }.max()
        val maxDigits = maxNumber.toString().length

        val possiblyIllegalNumbers = HashSet((1.."1${"0".repeat(maxDigits/2)}".toLong()).flatMap { n ->
            repeatedStrings(n.toString(), maxDigits).map { it.toLong() }
        }).filter{it <= maxNumber}

        val illegalNumbers = possiblyIllegalNumbers.filter{ possiblyIllegal -> ranges.any { r -> r.contains(possiblyIllegal) } }

        return illegalNumbers.sum()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(1227775554L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(4174379265L)
    timeAndPrint { part2(input) }
}
