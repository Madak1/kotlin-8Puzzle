package hu.madak1.my8puzzle

import org.junit.Test
import org.junit.Assert.*
import java.lang.reflect.Field
import java.lang.reflect.Method

class PuzzleUnitTest {

    @Test
    fun `Step counter increase after update`() {
        // When start the step num is one
        val puzzle = Puzzle(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 6, 5), intArrayOf(7, 8, 9)))
        assertEquals(0, puzzle.getStepNum())
        // After a step the step num increase
        puzzle.updateBoard(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 5, 6), intArrayOf(7, 8, 9)))
        assertEquals(1, puzzle.getStepNum())
    }

    @Test
    fun `Can find the position of the blank tile`() {
        val puzzle = Puzzle(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 5, 6), intArrayOf(7, 8, 9)))
        // Set the blank target to test
        val privateField: Field = Puzzle::class.java.getDeclaredField("blankTarget")
        privateField.isAccessible = true
        privateField.set(puzzle, 5)
        // Get blank pos
        val blank = puzzle.findBlank()
        assertEquals(1, blank.first)
        assertEquals(1, blank.second)
    }

    @Test
    fun `Update board when make valid move`() {
        val b = arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 8, 6), intArrayOf(7, 5, 9))
        val puzzle = Puzzle(b)
        assertEquals(0, puzzle.getStepNum())
        // No update
        puzzle.updateBoard(b)
        assertEquals(0, puzzle.getStepNum())
        // Valid move
        puzzle.updateBoard(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 6, 8), intArrayOf(7, 5, 9)))
        assertEquals(1, puzzle.getStepNum())
    }

    @Test
    fun `Check board is in a goal state`() {
        // Access private method
        val privateMethod: Method = Puzzle::class.java.getDeclaredMethod("isBoardInGoalState")
        privateMethod.isAccessible = true
        // When no
        val puzzle1 = Puzzle(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 6, 5), intArrayOf(7, 8, 9)))
        assertFalse(privateMethod.invoke(puzzle1) as Boolean)
        // When yes
        val puzzle2 = Puzzle(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 5, 6), intArrayOf(7, 8, 9)))
        assertTrue(privateMethod.invoke(puzzle2) as Boolean)
    }

    @Test
    fun `Check the game is solvable`() {
        // Set the blank target to test
        val privateField: Field = Puzzle::class.java.getDeclaredField("blankTarget")
        privateField.isAccessible = true
        val solvablePuzzle = Puzzle(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 6, 5), intArrayOf(7, 8, 9)))
        privateField.set(solvablePuzzle, 9)
        assertEquals(false, solvablePuzzle.isSolvable())
        val unsolvablePuzzle = Puzzle(arrayOf(intArrayOf(1, 2, 3), intArrayOf(6, 4, 5), intArrayOf(7, 8, 9)))
        privateField.set(unsolvablePuzzle, 9)
        assertEquals(true, unsolvablePuzzle.isSolvable())
        val solvedPuzzle = Puzzle(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 5, 6), intArrayOf(7, 8, 9)))
        assertEquals(false, solvedPuzzle.isSolvable())
    }

    @Test
    fun `To string test`() {
        val puzzle = Puzzle(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 5, 6), intArrayOf(7, 8, 9)))
        val expectedString = "1 2 3\n4 5 6\n7 8 9\n"
        assertEquals(expectedString, puzzle.toString())
    }

}