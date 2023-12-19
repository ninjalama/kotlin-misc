package bowlingcalculator

import utils.ResourceUtils


fun List<Frame>.getNextRollsPinSum(nrOfRolls: Int): Int {
    return this.flatMap { it.rolls }.take(nrOfRolls).sumOf { it.nrOfPins}
}

enum class FrameType {
    SPARE, STRIKE, OTHER
}

data class Roll(val nrOfPins: Int)
data class Frame(val frameNumber: Int, val rolls: List<Roll>) {
    val frameType: FrameType

    init {
        frameType = when {
            rolls.first().nrOfPins == 10 -> FrameType.STRIKE
            rolls.take(2).sumOf { it.nrOfPins } == 10 -> FrameType.SPARE
            else -> FrameType.OTHER
        }
    }

    fun sumOfPins() = rolls.sumOf { it.nrOfPins }
}

data class Game(val frames: List<Frame>) {
    fun getScore(): Int =
        frames.mapIndexed { idx, frame ->
            when (frame.frameType) {
                FrameType.SPARE -> frame.sumOfPins() + frames.drop(idx + 1).getNextRollsPinSum(1)
                FrameType.STRIKE -> frame.sumOfPins() + frames.drop(idx + 1).getNextRollsPinSum(2)
                else -> frame.sumOfPins()
            }
        }.sum()

    companion object {
        fun fromInputLine(line: String): Game {
            // Parses a line describing a game - where "X" = Strike, "/" = Spare, "-" indicated a miss, a number representing knocked down pins otherwise
            // Sample a: X X X X X X X X X X X X
            // Sample b: 9- 9- 9- 9- 9- 9- 9- 9- 9- 9-
            // Samble c: 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/5

            // Since there are a maximum of 10 frames - but the samples include > 10 elements separated by " ", here's the parsing decision:
            // 1. Split the line into a maximum of 10 parts (last part potentially being e.g. " X X X")
            // 2. Remove whitespace (.. only useful for 10th frame). Tenth frame will as a result be represeted as e.g. "XX", "XXX", "5/5", "54", etc.

            // Even though the possible representations for roll 1, roll 2, and the bonus roll 3 (tenth frame), are:
            // Roll 1: "X", "-", [1,9]
            // Roll 2: "/", "-", [1, 9]
            // Roll 3: "X", "-", [1,9]
            // .. we can simplify, because we're not supposed to validate rolls, frames, or anything.
            val framesString = line.split(" ", limit = 10).map { it.replace(" ", "") }
            val frames = framesString.mapIndexed { idx, frameStr ->
                val rolls = frameStr.mapIndexed { idy, char ->
                    val previousTurn = frameStr.getOrNull(idy - 1)
                    when (char) {
                        'X' -> 10
                        '-' -> 0
                        '/' -> 10 - (previousTurn?.digitToInt() ?: 0)
                        else -> char.digitToInt()
                    }
                }.map(::Roll)

                Frame(idx + 1, rolls)
            }
            return Game(frames)
        }
    }
}

fun main(args: Array<String>) {
    val sampleInputs = ResourceUtils.getResourceAsText("/bowlingcalculator/sampleInput.txt").orEmpty()
    val lines = sampleInputs.split("\n")
    val games = lines.map { Game.fromInputLine(it) }

    val gameLinesToGame = lines.zip(games)

    println("Sample games - and score:")
    gameLinesToGame.forEach { gameLineToGame ->
        println("\nGame input: " + gameLineToGame.first)
        println("Game score: " + gameLineToGame.second.getScore())
    }

    fun readGameInput() {
        println("Type in a game to get score:\n")
        println()
        val input = readLine()
        if (input != null) {
            println("Game score: " + Game.fromInputLine(input).getScore())
        }
    }

    while(true) readGameInput()
}