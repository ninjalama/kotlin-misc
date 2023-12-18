package bowlingcalculator

import utils.ResourceUtils

fun List<Frame>.getNextRolls(nrOfRolls: Int): List<Roll> {
    return this.flatMap { it.rolls }.take(nrOfRolls)
}

fun List<Frame>.getNextRollsPinSum(nrOfRolls: Int): Int {
    return getNextRolls(nrOfRolls).sumBy { it.nrOfPins }
}

enum class FrameType {
    SPARE, STRIKE, OTHER
}

data class Roll(val nrOfPins: Int)

data class Frame(val frameNumber: Int, val rolls: List<Roll>) {
    val frameType: FrameType

    init {
        frameType = when {
            // If the first roll on its own is 10 - this Frame's FrameType is considered a STRIKE
            rolls.first().nrOfPins == 10 -> FrameType.STRIKE
            // If the first two rolls summed is 10 - this Frame's FrameTye is considered a SPARE 
            rolls.take(2).map { it.nrOfPins }.sum() == 10 -> FrameType.SPARE
            // Otherwise.. this fram's FrameType is someting else.. OTHER, for a lack of a better name
            else -> FrameType.OTHER
        }
    }

    fun sumOfPins() = rolls.sumBy { it.nrOfPins }
}

data class Game(val frames: List<Frame>) {
    fun getScore(): Int {
        return frames.mapIndexed { idx, frame ->
            when (frame.frameType) {
                FrameType.SPARE -> frame.sumOfPins() + frames.drop(idx + 1).getNextRollsPinSum(1)
                FrameType.STRIKE -> frame.sumOfPins() + frames.drop(idx + 1).getNextRollsPinSum(2)
                else -> frame.sumOfPins()
            }
        }.sum()
    }

    companion object {
        fun fromInputLine(line: String): Game {
            // Parse a line describing a game - where "X" = Strike, "/" = Spare, "-" indicated a miss
            // Sample a: X X X X X X X X X X X X
            // Sample b: 9- 9- 9- 9- 9- 9- 9- 9- 9- 9-
            // Samble c: 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/5

            // Since there are a maximum of 10 frames - but the samples include > 10 elements separated by " ", here's the parsing decision:
            // 1. Split the line into a maximum of 10 parts (last part potentially being e.g. " X X X")
            // 2. Remove whitespace (.. only useful for 10th frame). Tenth frame will as a result be represeted as e.g. "XX", "XXX", "5/5", "54", etc.
            val (framePart, gameScore) = line.split(":")
            val framesString = framePart.split(" ", limit = 10).map { it.replace(" ", "") }

            // TODO: This needs to be.. improved :-)
            val frames = framesString.mapIndexed { idx, frameStr ->
                val firstTry = when (frameStr[0]) {
                    // First try/throw is limited to strike, miss, or a number of knocked down pins
                    'X' -> 10
                    '-' -> 0
                    else -> frameStr[0].digitToInt()
                }
                val secondTry: Int? = when (frameStr.getOrNull(1)) {
                    // Second try/throw is spare, strike (if 10th frame), miss, or a number of knocked down pins
                    '/' -> 10 - firstTry
                    'X' -> 10
                    '-' -> 0
                    else -> frameStr.getOrNull(1)?.digitToInt()
                }
                val thirdTry: Int? = when (frameStr.getOrNull(2)) {
                    // Third try/throw is a strike (if 10th frame), miss, or a number of knocked down pins
                    'X' -> 10
                    '-' -> 0
                    else -> frameStr.getOrNull(2)?.digitToInt()
                }
                val frame = Frame(idx, listOf(firstTry, secondTry, thirdTry).filterNotNull().map { Roll(it) })
                frame
            }
            return Game(frames)
        }
    }
}

fun main(args: Array<String>) {
    val sampleInputs = ResourceUtils.getResourceAsText("/bowlingcalculator/sampleInput.txt").orEmpty()
    val lines = sampleInputs.split("\n")
    val games = lines.map {Game.fromInputLine(it) }

    games.forEachIndexed { idx, game ->
        println("Score for game #$idx: " + game.getScore())
    }
}