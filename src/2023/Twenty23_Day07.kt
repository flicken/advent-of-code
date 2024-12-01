enum class Type {
    HighCard,
    OnePair,
    TwoPair,
    ThreeOfKind,
    FullHouse,
    FourOfKind,
    FiveOfKind,
}

val cardsRank = "23456789TJQKA".toCharArray()
val cardsRankWithJoker = "J23456789TQKA".toCharArray()

data class Hand(val type: Type, val cards: List<Char>, val cardsAfterJoker: List<Char>, val bid: Int)

fun main() {
    val day = "Day07"

    fun Char.rank() = cardsRank.indexOf(this)
    fun Char.rankWithJoker() = cardsRankWithJoker.indexOf(this)
    fun bestOrder(cards: List<Char>) =
        cards.sortedByDescending { c -> cards.count { it == c } * 100 + c.rank() }

    fun typeFor(cards: List<Char>): Type {
        val counts = bestOrder(cards).map { c -> cards.count { c == it } }
        val type = when (counts) {
            listOf(5, 5, 5, 5, 5) -> Type.FiveOfKind
            listOf(4, 4, 4, 4, 1) -> Type.FourOfKind
            listOf(3, 3, 3, 2, 2) -> Type.FullHouse
            listOf(3, 3, 3, 1, 1) -> Type.ThreeOfKind
            listOf(2, 2, 2, 2, 1) -> Type.TwoPair
            listOf(2, 2, 1, 1, 1) -> Type.OnePair
            listOf(1, 1, 1, 1, 1) -> Type.HighCard
            else -> throw Exception("Cannot find hand for ${cards} ${counts}")
        }
        return type
    }

    fun String.toHand(): Hand {
        val (cardS, bidS) = this.split(" ");
        val cards = cardS.toCharArray().toList()

        return Hand(typeFor(cards), cards, cards, bidS.toInt())
    }

    fun String.toHandWithJokers(): Hand {
        val (cardS, bidS) = this.split(" ");
        val cards = cardS.toCharArray().toList()

        val cardsWithoutJokers = bestOrder(cards.filter { it != 'J' })
        val bestNonJack = cardsWithoutJokers.getOrNull(0) ?: 'J';
        val cardsReplacingJokers = cards.map { if (it == 'J') bestNonJack else it }

        return Hand(typeFor(cardsReplacingJokers), cards, cardsReplacingJokers, bidS.toInt())
    }

    fun part1(input: List<String>): Int {
        val hands =
            input.map { it.toHand() }
                .sortedWith { a, b ->
                    compareValuesBy(
                        a,
                        b,
                        { h -> h.type },
                        { h ->
                            h.cards.map { it.rank() }.reduce { acc, rank -> rank + (acc shl 4) }
                        },
                    )
                }

        return hands
            .mapIndexed { i, h -> (i + 1) * h.bid }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val hands =
            input.map { it.toHandWithJokers() }
                .sortedWith { a, b ->
                    compareValuesBy(
                        a,
                        b,
                        { h -> h.type },
                        { h ->
                            h.cards.map { it.rankWithJoker() }.reduce { acc, rank -> rank + (acc shl 4) }
                        },
                    )
                }

        return hands
            .mapIndexed { i, h -> (i + 1) * h.bid }
            .sum()
    }

    val testInput = readInput("${day}_test")
    part1(testInput).assertEqual(6440)

    val input = readInput(day)
    timeAndPrint { part1(input) }

    part2(testInput).assertEqual(5905)
    timeAndPrint { part2(input) }
}
