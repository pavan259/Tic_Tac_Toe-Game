package com.pavan.tictactoe.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class NotificationData {
    @SerializedName("to")
    @Expose
    var to: String? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null
}