package com.pavan.tictactoe

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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home_screen.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeScreenActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var profileEmail:TextView? = null
    private var profileImage: ImageView? = null
    private var profileName: TextView? = null
    private var signOut: Button? = null

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

    private fun setupUI() {
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
        const val TAG = "HomeActivity"
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
            R.id.onlinebtn -> openBoard(0)
            R.id.twoplybtn -> openBoard(2)
            R.id.compbtn -> openBoard(0)
            R.id.sign_out-> signOut()
        }
    }



    private fun openBoard(i: Int) {
        val intent = Intent(this@HomeScreenActivity, MainActivity::class.java)
        intent.putExtra("gameType", i)
        startActivity(intent)
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            val user = User(
                firebaseUser.uid,
                firebaseUser.displayName,
                firebaseUser.email,
                firebaseUser.photoUrl.toString()
            )
            addUser(user)
            logUser(user)
        }
    }


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


    private fun logUser(user: User) {
      //  Crashlytics.setUserIdentifier(user.uid)
        //Crashlytics.setUserEmail(user.email)
        //Crashlytics.setUserName(user.name)

        firebaseAnalytics.setUserId(user.email)
        firebaseAnalytics.setUserProperty("uid", user.uid)
        firebaseAnalytics.setUserProperty("email", user.email)
        firebaseAnalytics.setUserProperty("name", user.name)
    }



}
