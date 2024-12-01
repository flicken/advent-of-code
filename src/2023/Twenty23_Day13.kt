fun main() {
    val day = "Day13"

    fun scoreAllPuzzle(puzzle: List<String>, multiplier: Long = 1): List<Long> {
        val scores = mutableListOf<Long>()
        var searchFrom = (puzzle.size + 1) / 2

        while (searchFrom < puzzle.size) {
            val right = puzzle.subList(searchFrom, puzzle.size)
            val left = puzzle.subList(searchFrom - right.size, searchFrom)
            if (right.isNotEmpty() && right.reversed() == left) {
                scores.add(multiplier * searchFrom)
            }
            searchFrom += 1
        }

        searchFrom = (puzzle.size / 2)
        while (searchFrom > 0) {
            val left = puzzle.subList(0, searchFrom)
            val right = puzzle.subList(searchFrom, searchFrom + left.size)
            if (right.isNotEmpty() && right.reversed() == left) {
                scores.add(multiplier * searchFrom)
            }
            searchFrom -= 1
        }

        return scores;
    }

    fun scoreAllAndTransposed(smudged: List<String>) =
        scoreAllPuzzle(smudged, 100) + scoreAllPuzzle(smudged.transpose())

    fun part1(input: List<String>): Long {
        return input.splitBy { line -> line.isBlank() }.sumOf {
            scoreAllAndTransposed(it).firstOrNull()
                ?: throw Exception("Cannot find solution to ${it} or ${it.transpose()}")
        }
    }

    fun smudge(list: List<String>, count: Int): List<String> {
        return list.replaceAt(count % list.size) { line ->
            val colToChange = count / list.size
            val replacement = when (line[colToChange]) {
                '.' -> "#"
                '#' -> "."
                else -> throw Exception("Unexpected char ${line[colToChange]}")
            }
            line.replaceRange(colToChange, colToChange + 1, replacement)
        }
    }

    fun part2(input: List<String>): Long {
        return input.splitBy { line -> line.isBlank() }.sumOf {
            val maxCount = it[0].length * it.size
            val originalScore = scoreAllAndTransposed(it).first()
            var score = originalScore
            var count = 0
            while (true) {
                if (count >= maxCount) {
                    break
                }
                val smudged = smudge(it, count)
                val scores = scoreAllAndTransposed(smudged)
                val differentScore = scores.filter { it != originalScore }
                if (differentScore.isNotEmpty()) {
                    score = differentScore.first()
                    break;
                }
                count += 1
            }
            score
        }
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(405)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(400)
    part2(readInput("${day}_test2")).assertEqual(10)
    timeAndPrint { part2(input) }
}
