package com.pavan.tictactoe.models

/**
 * Class COMMENT
 *
 * @see "Online game Board data"
 */
class Game(
    var gameId: String? = "",
    var state: String? = "",
    var cell_1: Int? = 0,
    var cell_2: Int? = 0,
    var cell_3: Int? = 0,
    var cell_4: Int? = 0,
    var cell_5: Int? = 0,
    var cell_6: Int? = 0,
    var cell_7: Int? = 0,
    var cell_8: Int? = 0,
    var cell_9: Int? = 0
) {
    override fun toString(): String {
        return "Game(gameId=$gameId, state=$state,  cell_1=$cell_1, cell_2=$cell_2, cell_3=$cell_3, cell_4=$cell_4, cell_5=$cell_5, cell_6=$cell_6, cell_7=$cell_7, cell_8=$cell_8, cell_9=$cell_9)"
    }
}