import bowlingcalculator.Game
import bowlingcalculator.Frame
import bowlingcalculator.Roll

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class BowlingCalculatorTest {
    private val sampleLine = "5/ 4- 3/ 2/ 1/ 9/ X -4 5/ 5/8"
    private val sampleGamesWithScore = listOf(
        "X X X X X X X X X X X X" to 300,
        "5/ 4- 3/ 2/ 1/ 9/ X -4 5/ 5/8" to 131,
        "9- 9- 9- 9- 9- 9- 9- 9- 9- 9-" to 90,
        "5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/5" to 150
    )
    
    @Test
    fun testFromInputLine() {
        val expectedGame = Game(
            listOf(
                Frame(1, listOf(Roll(5), Roll(5))),
                Frame(2, listOf(Roll(4),Roll(0))),
                Frame(3, listOf(Roll(3), Roll(7))),
                Frame(4, listOf(Roll(2), Roll(8))),
                Frame(5, listOf(Roll(1), Roll(9))),
                Frame(6, listOf(Roll(9), Roll(1))),
                Frame(7, listOf(Roll(10))),
                Frame(8, listOf(Roll(0), Roll(4))),
                Frame(9, listOf(Roll(5), Roll(5))),
                Frame(10, listOf(Roll(5), Roll(5), Roll(8)))
            )
        )

        assertEquals(Game.fromInputLine(sampleLine), expectedGame) 
    }
    
    @Test
    fun testGetScore() {
        assertTrue(sampleGamesWithScore.all {
            Game.fromInputLine(it.first).getScore() == it.second
        })
    }
    
}