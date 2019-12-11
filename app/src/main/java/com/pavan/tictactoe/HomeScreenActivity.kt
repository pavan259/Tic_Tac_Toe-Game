package com.pavan.tictactoe

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.pavan.tictactoe.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home_screen.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeScreenActivity : AppCompatActivity(), View.OnClickListener {

    private var profileEmail:TextView? = null
    private var profileImage: ImageView? = null
    private var profileName: TextView? = null
    private var signOut: Button? = null

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference
        databaseRef.keepSynced(true)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        onlinebtn.setOnClickListener(this)
        twoplybtn.setOnClickListener(this)
        compbtn.setOnClickListener(this)

        profileEmail = findViewById(R.id.profile_email)
        profileImage = findViewById(R.id.profile_image)
        profileName = findViewById(R.id.profile_text)
        signOut=findViewById(R.id.sign_out)

        setupUI()
    }

    fun setupUI() {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (acct != null) {
            Picasso.get().load(acct.photoUrl).centerInside().fit()
                .into(profileImage)
            profileName!!.text = acct.displayName
            profileEmail!!.text = acct.email

        }
        sign_out.setOnClickListener {
            signOut()
        }
    }
    companion object {
        const val TAG = "HomeScreenActivity"
        fun getLaunchIntent(from: Context) = Intent(from,HomeScreenActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }


    private fun signOut() {
        startActivity(LoginActivity.getLaunchIntent(this))
        FirebaseAuth.getInstance().signOut()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.onlinebtn -> openFriends()
            R.id.twoplybtn -> openBoard(2)
            R.id.compbtn -> openBoard(0)
            R.id.sign_out-> signOut()
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @see "open friends list"
     */
    private fun openFriends() {
        val intent = Intent(this@HomeScreenActivity, FriendsListActivity::class.java)
        startActivity(intent)
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    /**
     * FUNCTION COMMENT
     *
     * @param firebaseUser: FirebaseUser
     * @see "if firebaseUser logged in, set firebaseUser info in ui"
     */
    @SuppressLint("SetTextI18n")
    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            retrieveToken(firebaseUser)
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @param firebaseUser : FirebaseUser
     * @see "retrieve firebase messaging token for receiving notification"
     */
    private fun retrieveToken(firebaseUser: FirebaseUser) {
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result?.token.toString()
                Log.e(TAG, "" + token)
                val user = User(
                    firebaseUser.uid,
                    firebaseUser.displayName,
                    firebaseUser.email,
                    firebaseUser.photoUrl.toString(),
                    token
                )
                databaseRef.child("users")
                    .child(user.uid.toString())
                    .child("token")
                    .setValue(token)
                    .addOnSuccessListener {
                        Log.e(TAG, "token added")
                    }.addOnFailureListener {
                        Log.e(TAG, "token not added")
                    }
                addUser(user)
                logUser(user)
            })
    }
    private fun logUser(user: User) {
        firebaseAnalytics.setUserId(user.email)
        firebaseAnalytics.setUserProperty("uid", user.uid)
        firebaseAnalytics.setUserProperty("email", user.email)
        firebaseAnalytics.setUserProperty("name", user.name)
    }


    /**
     * FUNCTION COMMENT
     *
     * @param user: User
     * @see "store user details in db"
     */
    private fun addUser(user: User) {
        databaseRef.child("users")
            .orderByChild("uid")
            .equalTo(user.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        databaseRef.child("users")
                            .child(user.uid.toString())
                            .setValue(user)
                            .addOnSuccessListener {
                                Log.e(TAG, "user added")
                            }.addOnFailureListener {
                                Log.e(TAG, "user not added")
                            }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message) //Don't ignore errors!
                }
            })
    }

    /**
     * FUNCTION COMMENT
     *
     * @param i: Int
     * @see "handle offline game selection and open board"
     */
    private fun openBoard(i: Int) {
        val intent = Intent(this@HomeScreenActivity, MainActivity::class.java)
        intent.putExtra("gameType", i) //0=random, 1=trained AI, 2=double player, 3=online
        startActivity(intent)
    }


}
