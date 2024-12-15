import Direction.*

fun main() {
    val day = "Day15"

    fun List<List<Char>>.show(): String = this.map{it.joinToString("")}.joinToString("\n")
    operator fun List<List<Char>>.get(location: Location): Char = this[location.row][location.col]

    operator fun MutableList<MutableList<Char>>.set(location: Location, c: Char): Char {
        val previous = this[location.row][location.col]
        this[location.row][location.col] = c
        return previous
    }


    fun List<List<Char>>.canMove(location: Location, direction: Direction): Boolean {
        val next = location.go(direction)
        when(this[next]) {
            '#' -> return false
            '.' -> return true
            'O' -> return this.canMove(next, direction)
            else -> throw IllegalStateException("Cannot move ${direction} from ${location} unknown char ${this[next]}")
        }
    }

    fun MutableList<MutableList<Char>>.push(location: Location, direction: Direction, c: Char): Location {
        val nextChar = this.set(location, c)
        val next = location.go(direction)
        return when(nextChar) {
            '#' -> throw IllegalStateException("Cannot push wall ${location} ${direction} -> ${next}")
            '.' -> next
            else -> {
                this.push(next, direction, nextChar)
            }
        }
    }

    val charToDirection = mapOf('^' to Up, '>' to Right, 'v' to Down, '<' to Left)

    fun List<List<Char>>.score(): Long {
        var score = 0L
        this.forEachIndexed { row, line ->
            line.forEachIndexed { col, c ->
                if (c == 'O') {
                    score += (row * 100) + col
                }
                if (c == '[') {
                    // TODO No adjustment needed if on left side
                    score += (row * 100) + col
                }
            }
        }
        return score
    }

    fun part1(input: List<String>): Long {
        val (warehouseS, moves) = input.splitBy{it.isEmpty()}

        val warehouse = warehouseS.map { it.toCharArray().toMutableList() }.toMutableList()

        var location = Location(0, 0)
        warehouse.forEachIndexed { row, line ->
            line.forEachIndexed { col, c ->
                if (c == '@') {
                    location = Location(row, col)
                }
            }
        }

        moves.forEach { line ->
            line.forEach { moveC ->
                val move = charToDirection[moveC] ?: throw IllegalStateException("Cannot find direction for move ${moveC}")
                if (warehouse.canMove(location, move)) {
                    warehouse.push(location, move, '.')
                    location = location.go(move)
                }
            }
        }

        return warehouse.score()
    }

    val doubleDirections = listOf(Up, Down)
    fun List<List<Char>>.canMove2(location: Location, direction: Direction, skipDouble: Boolean = false): Boolean {
        val next = location.go(direction)
        when(this[next]) {
            '#' -> return false
            '.' -> return true
            '[' -> return this.canMove2(next, direction) && (skipDouble || if (doubleDirections.contains(direction)) this.canMove2(location.go(Right), direction, true) else true)
            ']' -> return this.canMove2(next, direction)  && (skipDouble || if (doubleDirections.contains(direction)) this.canMove2(location.go(Left), direction, true) else true)
            else -> throw IllegalStateException("Cannot move ${direction} from ${location} unknown char ${this[next]}")
        }
    }

    fun MutableList<MutableList<Char>>.swap(a: Location, b: Location) {
        this[a] = this.set(b, this[a])
    }

    fun MutableList<MutableList<Char>>.push2(locations: Collection<Location>, direction: Direction): Boolean {
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
            '@' -> listOf(it)
            '#' -> throw IllegalStateException("Did not expect # at ${it}")
            else -> throw IllegalStateException("Did not expect ${this[it]} at ${it}")
        } }.toSet()

        if (!push2(addBoxes, direction)) {
            return false
        }

        locations.forEach { a ->
            swap(a, a.go(direction))
        }

        return true
    }


    fun part2(input: List<String>): Long {
        val (warehouseS, moves) = input.splitBy{it.isEmpty()}

        val warehouse = warehouseS.map { it.toCharArray().flatMap{ when(it) {
            '#' -> "##".toList()
            'O' -> "[]".toList()
            '.' -> "..".toList()
            '@' -> "@.".toList()
            else -> throw IllegalStateException("Unknown character ${it}")
        } }.toMutableList() }.toMutableList()

        var location = Location(0, 0)
        warehouse.forEachIndexed { row, line ->
            line.forEachIndexed { col, c ->
                if (c == '@') {
                    location = Location(row, col)
                }
            }
        }

        moves.forEach { line ->
            line.forEach { moveC ->
                val move = charToDirection[moveC] ?: throw IllegalStateException("Cannot find direction for move ${moveC}")
                if (warehouse.push2(listOf(location), move)) {
                    location = location.go(move)
                }
            }
        }

        return warehouse.score()
    }

    val test0Input = readInput("${day}_test0")
    part1(test0Input).assertEqual(2028L)

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(10092L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    val scoreTestingWarehouseS = readInput("${day}_testscore")
    scoreTestingWarehouseS.map{it.toCharArray().toList()}.score().assertEqual(9021L)

    part2(readInput("${day}_test2")).assertEqual(618)

    part2(testInput).assertEqual(9021L)
    timeAndPrint { part2(input) }.assertLessThan(1481774L)
}
