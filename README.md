# Tic_Tac_Toe-Game using Kotlin

Tic-Tac-Toe is one of the most popular paper-and-pencil games played by two players. Each player selects Xs or Os and mark one of the spaces provided in 3x3 grid. Whoever creates a diagonal, vertical or horizontal lines of same mark wins the game.
This application will provide user to play this game in three different modes:
* Single Player V/s Android
* Two Player (same Device)
* Online Two Player

## Getting Started

Open android studio and goto "File"
* Select New >> Import from version control
* Choose git / github (git option recommanded )
![AndroidStudio](https://user-images.githubusercontent.com/44563119/70840407-a6b0cc00-1dd7-11ea-9140-afbb83be6994.png)
* Paste url to repository i.e https://github.com/pavan259/Tic_Tac_Toe-Game.git
* Click on clone.




### Prerequisites

This app is using the Firebase Services. So, if you want to view the real-time data and perform analytics you need to follow these steps:

#### For Firebase Connection 
* Goto [ https://console.firebase.google.com/ ] and register your app package and follow the steps to make your app firebase ready.
* After registering your app you will get "google-services.json" file. Paste that file in your projects root directory.



#### For Firebase Cloud Messaging Service
* Open Firebase Console and open your project.
* Goto prject setting click beside "Project Overview" to see this setting.
* Click on Cloud Messaging tab from the project settings.
* Copy the "Legacy Server Key" and paste that key in following file in the project [ Tic_Tac_Toe-Game\app\src\main\java\com\pavan\tictactoe\services\retrofit\APIService.kt ] in authorization key part.

```
interface APIService {
    @Headers("Content-type: application/json", "Your Legacy Server Key")/*TODO : Replace KEY from Console*/
    @POST("send")
        fun sendNotification(@Body notificationData: NotificationData): Call<JSONObject>
}
```

### Installing

* Install this app as a normal android application on your emulator or device.
* Add the email addresses of your friends with whom you would like to send the game request.
* Goto to "Contacts" application in phone and add email address of your friend.
 
**NOTE** : For sending game request to your friend, you must add his email-id that he used for logging in the game, and same stands for them if they want to send you game requests. 


## Using the application

### Logging In
* User just need to follow steps as shown in images below:
![Logging in](https://user-images.githubusercontent.com/44563119/70841074-e8904100-1ddc-11ea-816e-23eccc9a5dfb.png)

### Playing Online Game
* Player 1 will click on the "Online" button to start the online game and follow the steps shown in images below:
![Sending Request](https://user-images.githubusercontent.com/44563119/70841281-d2838000-1dde-11ea-9d14-d794b845949a.png)

* Player 2 to whom request was send will follow the following steps shown in images below:
![Player2Receive](https://user-images.githubusercontent.com/44563119/70843289-70824500-1df5-11ea-99ed-6c15cbee4fb0.png)


### Winner Screen
* When a player wins a dialog box will pop-up showing the winners name and profile picture as shown in figure.
![PlayerWin](https://user-images.githubusercontent.com/44563119/70843325-edadba00-1df5-11ea-9067-cfce668fc688.png)


## Built With

* [Firebase Real-time Database](https://firebase.google.com/products/realtime-database) - Used for storing players moves
* [Firebase CLoud Messaging](https://firebase.google.com/docs/cloud-messaging) - Used for game request management
* [TextIcon](https://github.com/NAndroidEx/TextIcon) - Used to generate icons.


## Author

* **PAVAN PATEL** 

Please feel free to add features in this android game application.
-Thank You


