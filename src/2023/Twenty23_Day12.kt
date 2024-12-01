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
        val solutions = mutableMapOf(
            Problem("", listOf()) to 0L,
        )

        fun setSolution(problem: Problem, solution: Long): Long {
            solutions.put(problem, solution)
            return solution
        }

        fun dropBins(problem: Problem) = problem.bins.drop(1).dropWhile { it == '.' }

        fun countIt(line: String): List<Int> {
            return line.split(".", "?").map { it.length }.filter { it > 0 };
        }

        fun generateSolution(bins: String, n: Int, places: Int): String {
            val chars = (0..places).map { if (n and (1 shl it) != 0) '#' else '.' }
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

            val max = (1 shl places) - 1

            return (0..max).map { it ->
                generateSolution(this.bins, it, places)
            }.filter { countIt(it) == this.pkgs }
        }

        fun findSolutionCountSlow(problem: Problem, line: String? = null): Long {
            return problem.generateAllSolutions().size.toLong()
        }

        fun compareSlowSolution(problem: Problem, count: Long) {
            val countSlow = findSolutionCountSlow(problem, problem.bins)
            if (countSlow != count) {
                println("Disagreement ${countSlow} != ${count} on ${problem} ${problem.line()}")
                println(problem.generateAllSolutions().joinToString("\n"))
                println(solutions.entries.joinToString("\n"))
            }
        }

        fun findSolutionCount(problem: Problem, line: String? = null): Long {
            if (problem.pkgs.isEmpty()) {
                return 0
            }

            if (solutions.contains(problem)) {
                return solutions.getValue(problem)
            }

            val binLengthNeeded = problem.pkgs.sum() + problem.pkgs.size - 1
            if (problem.bins.length < binLengthNeeded) {
                return setSolution(problem, 0)
            }

            if (problem.pkgs.size == 1) {
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
                return setSolution(problem, shiftCounts + isMatch)
            }

            // More than one to fit in
            val pkg = problem.pkgs.first()
            val matches = "^[#?]{${pkg}}[^#].*"
            val isMatch = if (problem.bins.matches(matches.toRegex())) 1 else 0

            // Recurse in both directions (drop a package, drop start of bins)
            val subCounts =
                if (isMatch == 1) {
                    val recurseLeftBins = problem.bins.drop(pkg + 1)
                    findSolutionCount(Problem(recurseLeftBins, problem.pkgs.drop(1)))
                } else {
                    0
                }
            val shiftCounts =
                if (!problem.bins.startsWith("#")) findSolutionCount(Problem(dropBins(problem), problem.pkgs)) else 0
            return setSolution(problem, shiftCounts + subCounts)
        }

        return problems.map { problem ->
            val count = findSolutionCount(problem, problem.bins)
            if (tryBoth) {
                compareSlowSolution(problem, count)
            }
            if (count == 0L) {
                throw Exception(
                    "Cannot find solution to ${problem} ${problem.line()}\n${
                        problem.generateAllSolutions().joinToString("\n")
                    }"
                )
            }

            count
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

    part1(listOf("?###??????? 3,2,1")).assertEqual(6)
    part1(listOf("??#????#?. 4,1")).assertEqual(3)
    part1(readInput("Day12_problems")).assertEqual(83)

    part1(listOf("?#??##??? 6")).assertEqual(2)
    part1(listOf(".??.?#??##???. 1,6")).assertEqual(5)

    val testInput = readInput("${day}_test")
    part1(listOf("?###???????? 3,2,1")).assertEqual(10)
    part1(testInput).assertEqual(21L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(525152)
    timeAndPrint { part2(input) }
}
