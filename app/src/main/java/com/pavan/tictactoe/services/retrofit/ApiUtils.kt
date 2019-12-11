package com.pavan.tictactoe.services.retrofit

object ApiUtils {
    private const val BASE_URL = "https://fcm.googleapis.com/fcm/"

    val apiService: APIService
        get() = RetrofitClient.getClient(BASE_URL)!!.create(APIService::class.java)
}