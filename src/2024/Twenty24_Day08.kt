typealias Roof = Map<Char, List<Location>>;

fun main() {
    val day = "Day08"

    fun Location.inBounds(input: List<String>): Boolean =
        row >= 0 && col >= 0 && row < input.size && col < input[0].length

    fun List<String>.parse(): Roof {
        val roof = mutableMapOf<Char, List<Location>>()
        this.forEachIndexed{rowNum, row ->
            row.forEachIndexed{colNum, c ->
                if (c != '.') {
                    val prev = roof[c] ?: listOf()
                    roof[c] = prev + Location(rowNum, colNum)
                }
            }
        }
        return roof
    }

    fun findAntinodes(locations: List<Location>): List<Location> =
        locations.allPairs().flatMap { (a, b) ->
           listOf(Location(a.row + a.row - b.row, a.col + a.col - b.col),
               Location(b.row + b.row - a.row, b.col + b.col - a.col),
               )
        }.toList()

    fun resonantAntinodes(a: Location, b: Location, size: Int): List<Location> {
        val rowDelta = a.row - b.row
        val colDelta = a.col - b.col

        return (1..size).map  {
            Location(a.row + (it * rowDelta), a.col + (it * colDelta))
        }
    }

    fun findResonateAntinodes(locations: List<Location>, input: List<String>): List<Location> =
        locations.allPairs().flatMap { (a, b) ->
            (locations + resonantAntinodes(a, b, input.size) + resonantAntinodes(b, a, input.size))
                .filter{it.inBounds(input)}
        }.toList()

    fun List<String>.toOutput(antinodeLocations: Set<Location>) = this.mapIndexed { rowNum, row ->
        row.mapIndexed { colNum, c ->
            if (c == '.' && antinodeLocations.contains(Location(rowNum, colNum))) {
                '#'
            } else {
                c
            }
        }.joinToString("")
    }.joinToString("\n")


    fun part1(input: List<String>): Long {
        return input.parse().flatMap{(c, locations) ->
            findAntinodes(locations)
        }.filter{it.inBounds(input)}.toSet().count().toLong()
    }

    fun part2(input: List<String>): Long {
        return input.parse().flatMap{(c, locations) ->
            findResonateAntinodes(locations, input)
        }.filter{it.inBounds(input)}.toSet().tap{ antinodeLocations ->
            input.toOutput(antinodeLocations).p()
        }.count().toLong()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(14L)

    val input = readInput(day)
    val p1Result = timeAndPrint { part1(input) }
    if (p1Result >= 335) {
        println("Too high")
    }

    part2(testInput).assertEqual(34L)
    timeAndPrint { part2(input) }
}
