package com.pavan.tictactoe

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.ContactsContract
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pavan.tictactoe.models.Data
import com.pavan.tictactoe.models.Game
import com.pavan.tictactoe.models.NotificationData
import com.pavan.tictactoe.models.User
import com.pavan.tictactoe.services.retrofit.APIService
import com.pavan.tictactoe.services.retrofit.ApiUtils
import kotlinx.android.synthetic.main.activity_friends_list.*
import kotlinx.android.synthetic.main.activity_main.tvClose
import kotlinx.android.synthetic.main.dialog_waiting.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsListActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    private var tempUsers: ArrayList<User> = ArrayList()
    private var users: ArrayList<User> = ArrayList()

    private var mAPIService: APIService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        activity = this@FriendsListActivity

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference
        databaseRef.keepSynced(true)

        mAPIService = ApiUtils.apiService

        tvClose.setOnClickListener {
            onBackPressed()
        }

        rvFriends.layoutManager = LinearLayoutManager(this)
        rvFriends.adapter = FriendsAdapter(users, this)

        checkPermissions()
    }

    /**
     * FUNCTION COMMENT
     *
     * @see "check if contacts permission is allowed"
     */
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this@FriendsListActivity,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            //if not allowed, ask for permissions
            ActivityCompat.requestPermissions(
                this@FriendsListActivity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        } else {
            //if allowed, fetch contacts
            GetEmails().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }


    /**
     * FUNCTION COMMENT
     *
     * @see "AsyncTask to fetch all contacts with Email without blocking UI"
     */
    class GetEmails :
        AsyncTask<Void, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            //clear lists and show loading before fetching
            activity.tempUsers.clear()
            activity.users.clear()
            activity.showProgressDialog()
        }

        @SuppressLint("Recycle")
        override fun doInBackground(vararg p0: Void?): String? {
            val cur: Cursor? =
                activity.contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
            if (cur!!.count > 0) {
                while (cur.moveToNext()) {
                    val id: String =
                        cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                    val cur1: Cursor? = activity.contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (cur1!!.moveToNext()) { //to get the contacts
                        val name: String =
                            cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        val email: String =
                            cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                        //add contacts to temp list
                        activity.tempUsers.add(User("", name, email, ""))
                    }
                    cur1.close()
                }
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            activity.checkUsers()
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @see "check if users with email are registered or not"
     */
    private fun checkUsers() {
        val rootRef = databaseRef.child("users")
        for (user in tempUsers) {
            rootRef.orderByChild("email").equalTo(user.email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in snapshot.children) {
                            //get all registered users
                            val usr: User = child.getValue(User::class.java)!!
                            //if user is not same as logged in user
                            //add user to users list
                            if (!usr.uid.equals(auth.currentUser?.uid)) {
                                Log.e("user", "" + usr.email)
                                users.add(usr)
                            }
                        }
                        //add users to RecyclerView and hide loading
                        rvFriends.adapter = FriendsAdapter(activity.users, activity)
                        hideProgressDialog()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @see "Fetch the "Emails" from the contact list"
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //if user allow permission, fetch contacts
                    GetEmails().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                } else {
                    //if deny, show toast and close screen
                    Toast.makeText(
                        this@FriendsListActivity,
                        "You need to allow Contacts Permission.",
                        Toast.LENGTH_LONG
                    ).show()
                    onBackPressed()
                }
                return
            }
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @param friend : User
     * @see "send invitation to friend"
     */
    fun inviteFriend(friend: User) {
        val notificationData = NotificationData()
        notificationData.to = friend.token
        val data = Data()
        data.title = getString(R.string.app_name)
        data.message = auth.currentUser?.displayName + " invited you for game."
        val gameId =
            System.currentTimeMillis().toString() + "_" + auth.currentUser?.uid + "_" + friend.uid
        data.gameId = gameId
        notificationData.data = data

        mAPIService!!.sendNotification(notificationData).enqueue(object : Callback<JSONObject> {
            override fun onResponse(call: Call<JSONObject>, response: Response<JSONObject>) {
                if (response.isSuccessful) {
                    Log.i("TAG", "Notification Sent" + response.body()!!.toString())
                    val dialog = Dialog(this@FriendsListActivity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.dialog_waiting)
                    dialog.window?.setBackgroundDrawableResource(R.color.bg_activity)
                    dialog.setCancelable(false)
                    val timer = object : CountDownTimer(60000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            dialog.tvCountDown.text =
                                "00:" + String.format("%02d", millisUntilFinished / 1000)
                        }

                        override fun onFinish() {
                            dialog.dismiss()
                        }
                    }
                    timer.start()
                    dialog.show()

                    databaseRef.child("game")
                        .orderByChild("gameId")
                        .equalTo(gameId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    val game = Game(gameId, "-1")
                                    databaseRef.child("game")
                                        .child(gameId)
                                        .setValue(game)
                                }
                                for (child in dataSnapshot.children) {
                                    val game: Game = child.getValue(Game::class.java)!!
                                    val state = game.state
                                    if (state == "0") {
                                        dialog.dismiss()
                                        val intent =
                                            Intent(
                                                this@FriendsListActivity,
                                                MainActivity::class.java
                                            )
                                        intent.putExtra("gameId", gameId)
                                        intent.putExtra("gameType", 3)
                                        startActivity(intent)
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d("TAG", databaseError.message)
                            }
                        })
                }
            }

            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                Log.e("TAG", "Failed to send notification")
            }
        })
    }

    companion object {
        const val REQUEST_READ_CONTACTS = 100
        lateinit var activity: FriendsListActivity
    }
}
