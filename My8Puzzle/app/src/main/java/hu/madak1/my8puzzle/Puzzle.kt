package hu.madak1.my8puzzle

enum class Move {
    UP, LEFT, RIGHT, DOWN
}

class Puzzle(private var board: Array<IntArray>) {

    private val boardSizeN = this.board.size
    private val blankTarget = (1 .. this.boardSizeN*this.boardSizeN).random()
    private var stepNum = 0

    fun getBlank(): Int = this.blankTarget
    fun getStepNum(): Int = this.stepNum

    fun findBlank(): Pair<Int, Int> {
        for (row in 0 until this.boardSizeN) {
            for (col in 0 until this.boardSizeN) {
                if (this.board[row][col] == this.blankTarget) {
                    return Pair(row, col)
                }
            }
        }
        throw IllegalArgumentException("The board doesn't have a blank tile")
    }

    fun updateBoard(nBoard: Array<IntArray>): Boolean {
        if (this.board.contentEquals(nBoard)) return false
        this.board = nBoard
        this.stepNum++
        return this.isBoardInGoalState()
    }

    private fun isBoardInGoalState(): Boolean {
        for (i in 0 until this.boardSizeN) {
            for (j in 0 until this.boardSizeN) {
                if (i*this.boardSizeN+j != this.board[i][j]-1) return false
            }
        }
        return true
    }

    fun isSolvable(): Boolean {
        var sumInversion = 0
        val tmpList: ArrayList<Int> = arrayListOf()
        for (row in 0 until this.boardSizeN) {
            for (col in 0 until this.boardSizeN) {
                val x = this.board[row][col]
                if (x != blankTarget) {
                    for (y in tmpList) if (y > x) sumInversion+=1
                    tmpList.add(x)
                }
            }
        }
        return sumInversion%2==0 && !isBoardInGoalState()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (row in this.board) {
            builder.append(row.joinToString(" "))
            builder.append('\n')
        }
        return builder.toString()
    }
}