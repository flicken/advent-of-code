import java.util.*

typealias PartToCheck = Map<Char, Long>;

fun main() {
    val day = "Day19"

    data class Condition(val field: Char, val operator: Char, val value: Long) {
        fun applies(part: PartToCheck) = when (operator) {
            '<' -> part.getValue(field) < value
            '>' -> part.getValue(field) > value
            else -> throw Exception("Unknown operator ${operator}")
        }
    }

    data class Rule(val condition: Condition?, val result: String) {
        fun applies(part: PartToCheck): String? {
            if (condition == null || condition.applies(part)) {
                return result
            }

            return null;
        }
    }

    // {x=787,m=2655,a=1222,s=2876}

    fun accept(part: PartToCheck, workflows: Map<String, List<Rule>>): Boolean {
        tailrec fun iter(workflowName: String): Boolean {
            return when (workflowName) {
                "A" -> true
                "R" -> false
                else -> {
                    val workflow = workflows.getValue(workflowName)
                    iter(workflow.firstNotNullOf {
                        it.applies(part)
                    })
                }
            }
        }

        return iter("in")
    }

    fun toWorkflowMap(workflowsA: List<String>): Map<String, List<Rule>> {
        val workflows = workflowsA.map { workflowS ->
            val (name, rulesS) = workflowS.split("{")
            val rules = rulesS.replace("}", "").split(",").map {
                if (it.contains(":")) {
                    val (conditionS, result) = it.split(":")
                    Rule(Condition(conditionS[0], conditionS[1], conditionS.drop(2).toLong()), result)
                } else {
                    Rule(null, it)
                }
            }
            name to rules
        }.toMap()
        return workflows
    }

    fun part1(input: List<String>): Long {
        val (workflowsA, partsA) = input.splitBy { it.isBlank() }
        val workflows = toWorkflowMap(workflowsA)

        return partsA.map {
            it.replace("[}{]".toRegex(), "").split(",")
                .map { it.split("=").let { (a, b) -> a[0] to b.toLong() } }
                .toMap()
        }.filter {
            accept(it, workflows)
        }.sumOf { it.values.sum() }

    }


    fun splitParts(
        condition: Condition,
        partsLeft: Map<Char, List<Long>>
    ): Pair<Map<Char, List<Long>>, Map<Char, List<Long>>> {
        val values = partsLeft.getValue(condition.field)
        val meetMap = partsLeft.toMutableMap()
        val restMap = partsLeft.toMutableMap()

        val partioner = when (condition.operator) {
            '<' -> { x: Long -> x < condition.value }
            '>' -> { x: Long -> x > condition.value }
            else -> throw Exception("Invalid operator '${condition.operator}'")
        }
        val (match, rest) = values.partition(partioner)

        meetMap[condition.field] = match
        restMap[condition.field] = rest

        return Pair(meetMap, restMap)
    }

    fun part2(input: List<String>): Long {
        val (workflowsA) = input.splitBy { it.isBlank() }
        val workflows = toWorkflowMap(workflowsA)

        var queue = LinkedList(
            mutableListOf(
                Pair(
                    "in", mapOf(
                        'x' to (1L..4000L).toList(),
                        'm' to (1L..4000L).toList(),
                        'a' to (1L..4000L).toList(),
                        's' to (1L..4000L).toList()
                    )
                )
            )
        )

        val accepted = mutableListOf<Map<Char, List<Long>>>()
        val rejected = mutableListOf<Map<Char, List<Long>>>()

        while (queue.isNotEmpty()) {
            val (workflowName, parts) = queue.remove()

            when (workflowName) {
                "A" -> accepted.add(parts)
                "R" -> rejected.add(parts)
                else -> {
                    val workflow = workflows.getValue(workflowName)
                    var partsLeft = parts
                    workflow.forEach { rule ->
                        if (rule.condition == null) {
                            queue.add(rule.result to partsLeft)
                        } else {
                            val (partsToContinue, nextPartsLeft) = splitParts(rule.condition, partsLeft)
                            partsLeft = nextPartsLeft
                            queue.add(rule.result to partsToContinue)
                        }
                    }
                }
            }
        }

        return accepted.sumOf { a ->
            a.values.map { it.size.toLong() }.reduce { a, b -> a * b }
        }
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(19114L)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(167409079868000)
    timeAndPrint { part2(input) }
}