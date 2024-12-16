import Direction.*

fun main() {
    val day = "Day15"

    fun Iterable<Iterable<Char>>.findLocationOrThrow(char: Char): Location {
    this.forEachIndexed { row, line ->
        line.forEachIndexed { col, c ->
            if (c == char) {
                return Location(row, col)
            }
        }
    }
    throw IllegalStateException("Cannot find ${char}")
}
    fun List<List<Char>>.show(): String = this.map{it.joinToString("")}.joinToString("\n")

    operator fun List<List<Char>>.get(location: Location): Char = this[location.row][location.col]

    operator fun MutableList<MutableList<Char>>.set(location: Location, c: Char): Char {
        val previous = this[location.row][location.col]
        this[location.row][location.col] = c
        return previous
    }

    val charToDirection = mapOf('^' to Up, '>' to Right, 'v' to Down, '<' to Left)

    fun List<List<Char>>.score(): Long {
        return this.mapIndexed { row, line ->
            line.mapIndexed{col, c -> if (c == 'O' || c == '[') {(row * 100L) + col} else 0L}.sum()
        }.sum()
    }

    val doubleDirections = listOf(Up, Down)

    fun MutableList<MutableList<Char>>.swap(a: Location, b: Location) {
        this[a] = this.set(b, this[a])
    }

    fun MutableList<MutableList<Char>>.push(locations: Collection<Location>, direction: Direction): Boolean {
        if (locations.isEmpty()) {
            return true
        }

        val nextLocations = locations.map{it.go(direction)}

        if (nextLocations.any { this[it] == '#' }) {
            return false
        }

        val addBoxes = nextLocations.flatMap{ when(this[it]) {
            '[' -> if(doubleDirections.contains(direction)) listOf(it, it.go(Right)) else listOf(it)
            ']' -> if(doubleDirections.contains(direction)) listOf(it, it.go(Left)) else listOf(it)
            '.' -> listOf()
            'O' -> listOf(it)
            '@' -> listOf(it)
            '#' -> throw IllegalStateException("Did not expect # at ${it}")
            else -> throw IllegalStateException("Did not expect ${this[it]} at ${it}")
        } }.toSet()

        val canPush = push(addBoxes, direction)
        if (canPush) {
            locations.forEach { swap(it, it.go(direction)) }
        }

        return canPush
    }

    fun List<List<Char>>.findRobot(): Location = this.findLocationOrThrow('@')

    fun List<String>.moveAndScore(moves: List<String>): Long {
        val warehouse = this.map { it.toCharArray().toMutableList() }.toMutableList()

        var location = warehouse.findRobot()

        moves.forEach { line ->
            line.forEach { moveC ->
                val move =
                    charToDirection[moveC] ?: throw IllegalStateException("Cannot find direction for move ${moveC}")
                if (warehouse.push(listOf(location), move)) {
                    location = location.go(move)
                }
            }
        }

        return warehouse.score()
    }

    fun part1(input: List<String>): Long {
        val (warehouse, moves) = input.splitBy{it.isEmpty()}

        return warehouse.moveAndScore(moves)
    }

    fun part2(input: List<String>): Long {
        val (warehouseS, moves) = input.splitBy{it.isEmpty()}

        val warehouse = warehouseS.map { it.flatMap{ when(it) {
            '#' -> "##".toList()
            'O' -> "[]".toList()
            '.' -> "..".toList()
            '@' -> "@.".toList()
            else -> throw IllegalStateException("Unknown character ${it}")
        } }.joinToString("") }

        return warehouse.moveAndScore(moves)
    }

    val test0Input = readInput("${day}_test0")
    part1(test0Input).assertEqual(2028L)

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(10092L)

    val input = readInput(day)
    timeAndPrint { part1(input) }.assertEqual(1485257L)

    val scoreTestingWarehouseS = readInput("${day}_testscore")
    scoreTestingWarehouseS.map{it.toCharArray().toList()}.score().assertEqual(9021L)

    part2(readInput("${day}_test2")).assertEqual(618)

    part2(testInput).assertEqual(9021L)
    timeAndPrint { part2(input) }.assertLessThan(1481774L).assertEqual(1475512L)
}
