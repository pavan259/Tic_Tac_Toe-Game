package com.pavan.tictactoe.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Class COMMENT
 *
 * @see "Notification Data"
 */

class Data {
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("gameId")
    @Expose
    var gameId: String? = null
}