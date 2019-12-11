package com.pavan.tictactoe.services.retrofit

import com.pavan.tictactoe.models.NotificationData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * FUNCTION COMMENT
 *
 * @see "Will allow the application to send notification"
 * "Add Authorization key from the Firebase Console >> App Setting >> Cloud messaging"
 */
interface APIService {
    @Headers("Content-type: application/json", "Authorization:key=AIzaSyCtxpLMXHuCg25fVBOuBu4iv7DfFXn3xIc")/*TODO : Replace KEY from Console*/
    @POST("send")
        fun sendNotification(@Body notificationData: NotificationData): Call<JSONObject>
}