import kotlin.math.abs

fun main() {
    val day = "Day12"

    fun List<Long?>.productOfOrNull(): Long? {
        return this.reduce<Long?, Long?> { a, b ->
            if (a == null || b == null) null else a * b
        }
    }

    data class Problem(val bins: String, val pkgs: List<Int>) {
        fun unfold(n: Int): Problem {
            return Problem(Array(n, { this.bins }).joinToString("?"), Array(n, { this.pkgs }).flatMap { it })
        }
    }

    fun parseProblem(line: String): Problem {
        val (springfield, countS) = line.split(" ")
        val pkgs = countS.split(",").map { it -> it.toInt() }
        val bins = springfield.replace("[.]+".toRegex(), ".")
            .replace("^[.]".toRegex(), "")
            .replace("[.]$".toRegex(), "")
        return Problem(bins, pkgs)
    }

    fun part1(input: List<String>): Long {
        val solutions = mutableMapOf(
            Problem("", listOf()) to 0L,
//            Problem("????.#.#", listOf(4, 1, 1)) to 1L,
//            Problem("???.###", listOf(1, 1, 3)) to 1L,
//            Problem("??", listOf(1)) to 2L,
//            Problem("", listOf(1)) to 0L,
//            Problem("?###????????", listOf(3, 2, 1)) to 10L,
        )

        var currentIndent: String = ""
        fun indent(n: Int) {
            if (n < 0) {
                currentIndent = currentIndent.dropLast(abs(n))
            } else {
                currentIndent = currentIndent + String(CharArray(n, { ' ' }))
            }
        }

        fun pl(s: Any) {
            println(currentIndent + s)
        }


        fun setSolution(problem: Problem, solution: Long): Long {
            pl("${solution} <- ${problem} Solved")
            indent(-2)
            solutions.put(problem, solution)
            return solution
        }

        fun dropBins(problem: Problem) = problem.bins.dropWhile { it == '#' }.drop(1)

        fun countIt(line: String): List<Int> {
            val counts = line.split(".", "?").map { it.length }.filter { it > 0 }
//            counts.println()
            return counts;
        }

        fun generateSolution(bins: String, n: Int, places: Int): String {
            val chars = (0..places).map { if (n and (1 shl it) != 0) '#' else '.' }
//            println("Chars ${n} ${chars.joinToString("")} ${bins}")
            var i = 0
            return bins.map {
                when (it) {
                    '?' -> {
                        i += 1
                        chars[i - 1]
                    }

                    else -> it
                }
            }.joinToString("")
        }

        fun findSolutionCount2(problem: Problem, line: String? = null): Long {
            val places = problem.bins.count { it == '?' }

            var count = 0
            val max = (1 shl places) - 1

            return (0..max).count {
                countIt(generateSolution(problem.bins, it, places)) == problem.pkgs
            }.toLong()
        }

        fun findSolutionCount(problem: Problem, line: String? = null): Long {
            pl(problem)
            indent(2)
            if (problem.pkgs.isEmpty()) {
                pl("0 <- Base case")
                indent(-2)
                return 0
            }

            if (solutions.contains(problem)) {
                pl("${solutions.getValue(problem)} <- ${problem} Cached")
                indent(-2)
                return solutions.getValue(problem)
            }

            val binLengthNeeded = problem.pkgs.sum() + problem.pkgs.size - 1
            if (problem.bins.length < binLengthNeeded) {
                pl("Too short")
                return setSolution(problem, 0)
            }

            if (problem.pkgs.size == 1) {
                pl("Base case 1")

                // Take first pkg check if this matches
                val pkg = problem.pkgs.first()
                val matches = "^[#?]{${pkg}}[.?]?.*"
                val isMatch = if (problem.bins.matches(matches.toRegex())) 1 else 0
                val shiftCounts = findSolutionCount(Problem(dropBins(problem), problem.pkgs))
                val solution = shiftCounts + isMatch
                pl("${solution} = ${isMatch} . ${shiftCounts} ${matches} -> ${problem.bins}")
                return setSolution(problem, solution)
            }

            // Take first pkg check if this matches
            val pkg = problem.pkgs.first()
            val matches = "^[#?]{${pkg}}([^#].*|$)"
            val isMatch = if (problem.bins.matches(matches.toRegex())) 1 else 0
            pl("Recurse ${isMatch} ${matches}")
            // Recurse both sides

            val subCounts =
                if (isMatch == 1) {
                    val recurseLeftBins = problem.bins.drop(pkg + 1)
                    println("xxx ${problem.bins.splitAt(pkg + 1)} <- ${problem.bins} ${problem.pkgs[0]}")
                    findSolutionCount(Problem(recurseLeftBins, problem.pkgs.drop(1)))
                } else {
                    0
                }
            val shiftCounts = findSolutionCount(Problem(dropBins(problem), problem.pkgs))
            val solution = shiftCounts + subCounts
            pl("${solution} = ${isMatch} ${subCounts} ${shiftCounts} ${matches} -> ${problem.bins}")
            return setSolution(problem, solution)
        }

        return input.sumOf { line ->
            val problem = parseProblem(line)

            val count = findSolutionCount2(problem, line)
            if (count == 0L) {
                throw Exception("Cannot find solution to ${problem} ${line}")
            }
            println("--> ${count} ${line}")
            count
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf { line ->
            val problem = parseProblem(line).unfold(5)
            problem.println()
            -1L
        }
    }

    val testInput = readInput("${day}_test")
    part1(listOf("?###???????? 3,2,1")).assertEqual(10)
    part1(testInput).assertEqual(21L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(listOf(".??..??...?##. 1,1,3")).assertEqual(16384)
    part2(testInput).assertEqual(525152)
    timeAndPrint { part2(input) }
}
