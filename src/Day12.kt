import java.io.File
import kotlin.math.abs

fun main() {
    val day = "Day12"
    val pw = File("out.txt").printWriter()

    fun List<Long?>.productOfOrNull(): Long? {
        return this.reduce<Long?, Long?> { a, b ->
            if (a == null || b == null) null else a * b
        }
    }

    data class Problem(val bins: String, val pkgs: List<Int>) {
        fun unfold(n: Int): Problem {
            return Problem(Array(n, { this.bins }).joinToString("?"), Array(n, { this.pkgs }).flatMap { it })
        }

        fun line(): String = "${this.bins} ${this.pkgs.joinToString(",")}"
    }

    fun parseProblem(line: String): Problem {
        val (springfield, countS) = line.split(" ")
        val pkgs = countS.split(",").map { it -> it.toInt() }
        val bins = springfield.replace("[.]+".toRegex(), ".")
            .replace("^[.]".toRegex(), "")

        return Problem(if (bins.last() == '.') bins else bins + '.', pkgs)
    }

    fun findSolutions(problems: List<Problem>, tryBoth: Boolean = false): List<Long> {
        println("Trying $tryBoth} ${problems.size} ${problems[0]}")
        val solutions = mutableMapOf(
            Problem("", listOf()) to 0L,
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
//            println(currentIndent + s)
        }


        fun setSolution(problem: Problem, solution: Long): Long {
            indent(-2)
            pl("${solution} <- ${problem} Solved")
            solutions.put(problem, solution)
            return solution
        }

        fun dropBins(problem: Problem) = problem.bins.drop(1).dropWhile { it == '.' }

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

        fun Problem.generateAllSolutions(): List<String> {
            val places = this.bins.count { it == '?' }

            var count = 0
            val max = (1 shl places) - 1

            return (0..max).map { it ->
                generateSolution(this.bins, it, places)
            }.filter { countIt(it) == this.pkgs }
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
                indent(-2)
                pl("0 <- Base case")
                return 0
            }

            if (solutions.contains(problem)) {
                indent(-2)
                pl("${solutions.getValue(problem)} <- ${problem} Cached")
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
                val matches = "^[#?]{${pkg}}[.?]+$"
                val isMatch =
                    if (problem.bins.matches(matches.toRegex())) 1 else 0
                val shiftCounts =
                    if (!problem.bins.startsWith("#")) findSolutionCount(
                        Problem(
                            dropBins(problem),
                            problem.pkgs
                        )
                    ) else 0
                val solution = shiftCounts + isMatch
                pl("${solution} = ${isMatch} . ${shiftCounts} ${matches} -> ${problem.line()}")
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
//                    println("xxx ${problem.bins.splitAt(pkg + 1)} <- ${problem.bins} ${problem.pkgs[0]}")
                    findSolutionCount(Problem(recurseLeftBins, problem.pkgs.drop(1)))
                } else {
                    0
                }
            val shiftCounts =
                if (!problem.bins.startsWith("#")) findSolutionCount(Problem(dropBins(problem), problem.pkgs)) else 0
            val solution = shiftCounts + subCounts
            pl("${solution} = ${isMatch} ${subCounts} ${shiftCounts} ${matches} -> ${problem.line()}")
            return setSolution(problem, solution)
        }

        return problems.map { problem ->
            if (tryBoth) {
                val count = findSolutionCount2(problem, problem.bins)
                val count1 = findSolutionCount(problem, problem.bins)
                if (count != count1) {
                    println("Disagreement ${count} != ${count1} on ${problem} ${problem.line()}")
                    pw.println(problem.line())
                    println(problem.generateAllSolutions().joinToString("\n"))
                    println(solutions.entries.joinToString("\n"))
                }
                if (count == 0L) {
                    throw Exception(
                        "Cannot find solution to ${problem} ${problem.line()}\n${
                            problem.generateAllSolutions().joinToString("\n")
                        }"
                    )
                }
//            println("--> ${count} ${line}")
                count1
            } else {
                findSolutionCount(problem, problem.bins)
            }
        }
    }

    fun part1(input: List<String>, tryBoth: Boolean = true): Long {
        return findSolutions(input.map(::parseProblem), tryBoth).sum()
    }

    fun part2(input: List<String>, tryBoth: Boolean = false): Long {
        return findSolutions(input.map { line ->
            val (chars, nums) = line.split(" ")
            Array(5, { chars }).joinToString("?") + " " + Array(5, { nums }).joinToString(",")
        }.map(::parseProblem), tryBoth).sum()
    }

//    part1(listOf("?# 1")).assertEqual(1)
//    part1(listOf("????#??.?# 1,3,1")).assertEqual(6)
//    part1(listOf("#??##???. 6")).assertEqual(1)
    // ????#??.?# 1,3,1
    // #.###....#
    // #..###...#
    // #...###..#
    // .#.###...#
    // .#..###..#

//    part1(listOf("??.?#??##???. 1,6")).assertEqual(5)
    part1(listOf("?###??????? 3,2,1")).assertEqual(6)
    part1(listOf("??#????#?. 4,1")).assertEqual(3)
    part1(readInput("Day12_problems")).assertEqual(83)

    part1(listOf("?#??##??? 6")).assertEqual(2)
    part1(listOf(".??.?#??##???. 1,6")).assertEqual(5)

    val testInput = readInput("${day}_test")
    part1(listOf("?###???????? 3,2,1")).assertEqual(10)
    part1(testInput).assertEqual(21L)

    val input = readInput(day)
    timeAndPrint { part1(input, false) }

    listOf(
        part2(listOf(".??..??...?##. 1,1,3")),
        part2(listOf("????.#...#... 4,1,1")),
        part2(listOf("?###???????? 3,2,1")),
    ).assertEqual(listOf(16384L, 16L, 506250L))

    part2(testInput, false).assertEqual(525152)
    timeAndPrint { part2(input) }
}
