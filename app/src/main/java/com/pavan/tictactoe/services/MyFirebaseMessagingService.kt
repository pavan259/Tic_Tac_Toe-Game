package com.pavan.tictactoe.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pavan.tictactoe.MainActivity
import com.pavan.tictactoe.R


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationId = 1
    private val notificationChannelId = "fcm_default_channel"

    /**
     * FUNCTION COMMENT
     *
     * @param p0 : RemoteMessage
     * @see "extract data from received notification"
     */
    override fun onMessageReceived(p0: RemoteMessage) {
        val data = p0.data
        val title = data["title"]
        val message = data["message"]
        val gameId = data["gameId"]

        showNotification(title, message, gameId)
    }

    /**
     * FUNCTION COMMENT
     *
     * @param title : String
     * @param message : String
     * @param gameId : String
     * @see "show notification with parameters"
     */
    private fun showNotification(
        title: String?,
        message: String?,
        gameId: String?
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "Invite", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(this, notificationChannelId)
            .setVibrate(longArrayOf(0, 100, 100, 100, 100, 100))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(message)
            //notification will be automatically cleared after 60 seconds if not clicked
            .setTimeoutAfter(60000)
            .setContentIntent(
                //when clicked open board and pass gameId
                PendingIntent.getActivity(
                    this@MyFirebaseMessagingService, 0,
                    Intent(
                        this@MyFirebaseMessagingService,
                        MainActivity::class.java
                    ).putExtra("gameId", gameId).putExtra("gameType", 3),
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            ).setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}