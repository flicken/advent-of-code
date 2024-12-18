fun main() {
    val day = "Day18"

    fun IntRange.show(corrupted: Collection<Location>, path: Collection<Location> = setOf()): String =
        this.map { row ->
            this.map { col ->
                val l = Location(row, col)
                if (corrupted.contains(l)) '#' else if (path.contains(l)) "O" else "."
            }.joinToString("")
        }.joinToString("\n")


    fun IntRange.findPath(corrupted: Collection<Location>): SearchPath<Location>? {
        val mazeRange = this
        val endLocation = Location(mazeRange.last, mazeRange.last)

        val graph = object : Graph<Location> {
            override fun neighborsOf(node: Location): Iterable<Cost<Location>> =
                Direction.entries.map { node.go(it) }
                    .filter { !corrupted.contains(it) && it.row in mazeRange && it.col in mazeRange }
                    .map { Cost(it, 1) }
        }

        return graph.search(Location(0, 0), goalFunction = { it == endLocation }).path()
    }

    fun List<String>.parse() =
        this.map { it.split(",") }.map { Location(it[1].toInt(), it[0].toInt()) }

    fun part1(input: List<String>, mazeRange: IntRange, bytesFallen: Int): Long {
        val path = mazeRange.findPath(input.parse().take(bytesFallen).toSet())

        return path!!.size.toLong() - 1
    }

    fun part2(input: List<String>, mazeRange: IntRange): String {
        val locations = input.parse();
        val corrupted = mutableSetOf<Location>()

        locations.forEach { l ->
            corrupted.add(l)
            mazeRange.findPath(corrupted) ?: return "${l.col},${l.row}"
        }

        return ""
    }

    val testInput = readInput("${day}_test")
    part1(testInput, 0..6, 12).assertEqual(22L)

    val input = readInput(day)
    timeAndPrint { part1(input, 0..70, 1024) }

    part2(testInput, 0..6).assertEqual("6,1")
    timeAndPrint { part2(input, 0..70) }
}
