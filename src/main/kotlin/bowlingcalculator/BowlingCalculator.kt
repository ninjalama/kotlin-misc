package bowlingcalculator

import utils.ResourceUtils

fun List<Frame>.getNextRolls(nrOfRolls: Int): List<Roll> {
    return this.flatMap { it.rolls }.take(nrOfRolls)
}

fun List<Frame>.getNextRollsPinSum(nrOfRolls: Int): Int {
    return getNextRolls(nrOfRolls).sumOf { it.nrOfPins }
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
            // Parse a line describing a game - where "X" = Strike, "/" = Spare, "-" indicated a miss
            // Sample a: X X X X X X X X X X X X:300
            // Sample b: 9- 9- 9- 9- 9- 9- 9- 9- 9- 9-:
            // Samble c: 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/5

            // Since there are a maximum of 10 frames - but the samples include > 10 elements separated by " ", here's the parsing decision:
            // 1. Split the line into a maximum of 10 parts (last part potentially being e.g. " X X X")
            // 2. Remove whitespace (.. only useful for 10th frame). Tenth frame will as a result be represeted as e.g. "XX", "XXX", "5/5", "54", etc.
            val (framePart, gameScore) = line.split(":")
            val framesString = framePart.split(" ", limit = 10).map { it.replace(" ", "") }

            // Even though the possible representationsfor roll 1, roll 2, and the bonus roll 3 (tenth frame), are:
            // Roll 1: "X", "-", [1,9]
            // Roll 2: "/", "-", [1, 9]
            // Roll 3: "X", "-", [1,9]
            // .. we can simplify, because we're not supposed to validate rolls, frames, or anything.
            // The input-line is to be viewed as a valid line describing the game - which simplifies things:
            val frames = framesString.mapIndexed { idx, frameStr ->
                val rolls = frameStr.mapIndexed { idx, char ->
                    val previousTurn = frameStr.getOrNull(idx - 1)
                    when (char) {
                        'X' -> 10
                        '-' -> 0
                        '/' -> 10 - (previousTurn?.digitToInt() ?: 0)
                        else -> char.digitToInt()
                    }
                }.map(::Roll)

                Frame(idx, rolls)
            }
            return Game(frames)
        }
    }
}

fun main(args: Array<String>) {
    val sampleInputs = ResourceUtils.getResourceAsText("/bowlingcalculator/sampleInput.txt").orEmpty()
    val lines = sampleInputs.split("\n")
    val games = lines.map { Game.fromInputLine(it) }

    games.forEachIndexed { idx, game ->
        println("Score for game #$idx: " + game.getScore())
    }
}