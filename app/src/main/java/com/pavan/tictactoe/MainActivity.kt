package com.pavan.tictactoe

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.nandroidex.texticon.FontTextView
import com.pavan.tictactoe.models.Game
import com.pavan.tictactoe.models.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_loser.*
import kotlinx.android.synthetic.main.dialog_winner.*
import kotlinx.android.synthetic.main.dialog_winner.ibHome
import kotlinx.android.synthetic.main.dialog_winner.ibReplay
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private var user: User? = null
    private lateinit var user2: User
    private lateinit var game: Game

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    private var gameType = 0
    private var gameId: String = ""


    private var player1Moves= arrayListOf<Int>()
    private var player2Moves= arrayListOf<Int>()
    private var currentPlayer=1

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference
        databaseRef.keepSynced(true)

        gameType = intent.getIntExtra("gameType", 0)

        if (gameType == 3) {
            gameId = intent.getStringExtra("gameId")
            val splitGameId = gameId.split("_")
            var friendId = ""
            if (splitGameId[1] == auth.currentUser?.uid) {
                friendId = splitGameId[2]
                currentPlayer = 1
            } else if (splitGameId[2] == auth.currentUser?.uid) {
                friendId = splitGameId[1]
                currentPlayer = 2
                databaseRef.child("game")
                    .child(gameId)
                    .child("state")
                    .setValue("0")
            }
            setupFriend(friendId)
            getBoard()
        }

        setupViews()
    }

    /**
     * FUNCTION COMMENT
     *
     * @see "read game data"
     */
    private fun getBoard() {
        databaseRef.child("game")
            .orderByChild("gameId")
            .equalTo(gameId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (child in dataSnapshot.children) {
                        game = child.getValue(Game::class.java)!!
                        if (game.state == "0") {
                            databaseRef.child("game")
                                .child(gameId)
                                .setValue(
                                    Game(
                                        game.gameId,
                                        "1",

                                        game.cell_1,
                                        game.cell_2,
                                        game.cell_3,
                                        game.cell_4,
                                        game.cell_5,
                                        game.cell_6,
                                        game.cell_7,
                                        game.cell_8,
                                        game.cell_9
                                    )
                                )
                        }
                        updateBoard()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("TAG", databaseError.message)
                }
            })
    }

    /**
     * FUNCTION COMMENT
     *
     * @see "update board"
     */

    private fun updateBoard() {
        Log.d("TAG", game.toString())
        if (game.cell_1 == 0) {
            bu1.isEnabled = true
        } else {
            bu1.isEnabled = false
            if (game.cell_1 == 1) {
                bu1.text = getString(R.string.fa_circle)
            } else {
                bu1.text = getString(R.string.fa_times_solid)
            }
        }
        if (game.cell_2 == 0) {
            bu2.isEnabled = true
        } else {
            bu2.isEnabled = false
            if (game.cell_2 == 1) {
                bu2.text = getString(R.string.fa_circle)
            } else {
                bu2.text = getString(R.string.fa_times_solid)
            }
        }
        if (game.cell_3 == 0) {
            bu3.isEnabled = true
        } else {
            bu3.isEnabled = false
            if (game.cell_3 == 1) {
                bu3.text = getString(R.string.fa_circle)
            } else {
                bu3.text = getString(R.string.fa_times_solid)
            }
        }
        if (game.cell_4 == 0) {
            bu4.isEnabled = true
        } else {
            bu4.isEnabled = false
            if (game.cell_4 == 1) {
                bu4.text = getString(R.string.fa_circle)
            } else {
                bu4.text = getString(R.string.fa_times_solid)
            }
        }
        if (game.cell_5 == 0) {
            bu5.isEnabled = true
        } else {
            bu5.isEnabled = false
            if (game.cell_5 == 1) {
                bu5.text = getString(R.string.fa_circle)
            } else {
                bu5.text = getString(R.string.fa_times_solid)
            }
        }
        if (game.cell_6 == 0) {
            bu6.isEnabled = true
        } else {
            bu6.isEnabled = false
            if (game.cell_6 == 1) {
                bu6.text = getString(R.string.fa_circle)
            } else {
                bu6.text = getString(R.string.fa_times_solid)
            }
        }
        if (game.cell_7 == 0) {
            bu7.isEnabled = true
        } else {
            bu7.isEnabled = false
            if (game.cell_7 == 1) {
                bu7.text = getString(R.string.fa_circle)
            } else {
                bu7.text = getString(R.string.fa_times_solid)
            }
        }
        if (game.cell_8 == 0) {
            bu8.isEnabled = true
        } else {
            bu8.isEnabled = false
            if (game.cell_8 == 1) {
                bu8.text = getString(R.string.fa_circle)
            } else {
                bu8.text = getString(R.string.fa_times_solid)
            }
        }
        if (game.cell_9 == 0) {
            bu9.isEnabled = true
        } else {
            bu9.isEnabled = false
            if (game.cell_9 == 1) {
                bu9.text = getString(R.string.fa_circle)
            } else {
                bu9.text = getString(R.string.fa_times_solid)
            }
        }

        checkOnlineWinner()


    }

    /**
     * FUNCTION COMMENT
     *
     * @see "initialize views"
     */
    private fun setupViews() {
        if (gameType == 2) {
            tvName2.text = getString(R.string.player_2)
        }
        bu1.setOnClickListener(this)
        bu2.setOnClickListener(this)
        bu3.setOnClickListener(this)
        bu4.setOnClickListener(this)
        bu5.setOnClickListener(this)
        bu6.setOnClickListener(this)
        bu7.setOnClickListener(this)
        bu8.setOnClickListener(this)
        bu9.setOnClickListener(this)

        tvClose.setOnClickListener {
            onBackPressed()
        }

    }
    override fun onClick(v: View?) {

            val buSelected= v as FontTextView
            var cellId:Int
            cellId = when (buSelected.id) {
                R.id.bu1 -> 1
                R.id.bu2 -> 2
                R.id.bu3 -> 3
                R.id.bu4 -> 4
                R.id.bu5 -> 5
                R.id.bu6 -> 6
                R.id.bu7 -> 7
                R.id.bu8 -> 8
                R.id.bu9 -> 9
                else -> -1
            }
        if (gameType == 3) {
            if (game.state == currentPlayer.toString()) {
                if (currentPlayer == 1) {
                    buSelected.text = getString(R.string.fa_circle)
                }
                if (currentPlayer == 2) {
                    buSelected.text = getString(R.string.fa_times_solid)
                }
                databaseRef.child("game")
                    .child(gameId)
                    .child("cell_$cellId")
                    .setValue(currentPlayer)
                    .addOnSuccessListener {
                        val splitGameId = gameId.split("_")
                        if (splitGameId[1] == auth.currentUser?.uid) {
                            databaseRef.child("game")
                                .child(gameId)
                                .child("state")
                                .setValue("2")
                        }
                        if (splitGameId[2] == auth.currentUser?.uid) {
                            databaseRef.child("game")
                                .child(gameId)
                                .child("state")
                                .setValue("1")
                        }
                    }
            } else {
                Toast.makeText(this@MainActivity, "NOT YOUR TURN", Toast.LENGTH_LONG)
                    .show()
            }
            checkOnlineWinner()
        } else {
            setPlayerMove(cellId, buSelected)
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @see "check winner online"
     */

    private fun checkOnlineWinner() {
        val uId: String
        val splitGameId = gameId.split("_")
        if ((game.cell_1 == 1 && game.cell_2 == 1 && game.cell_3 == 1)
            || (game.cell_4 == 1 && game.cell_5 == 1 && game.cell_6 == 1)
            || (game.cell_7 == 1 && game.cell_8 == 1 && game.cell_9 == 1)
            || (game.cell_1 == 1 && game.cell_5 == 1 && game.cell_9 == 1)
            || (game.cell_3 == 1 && game.cell_5 == 1 && game.cell_7 == 1)
            || (game.cell_1 == 1 && game.cell_4 == 1 && game.cell_7 == 1)
            || (game.cell_2 == 1 && game.cell_5 == 1 && game.cell_8 == 1)
            || (game.cell_3 == 1 && game.cell_6 == 1 && game.cell_9 == 1)
        ) {
            uId = splitGameId[1]
            declareOnlineWinner(uId)
        } else if ((game.cell_1 == 2 && game.cell_2 == 2 && game.cell_3 == 2)
            || (game.cell_4 == 2 && game.cell_5 == 2 && game.cell_6 == 2)
            || (game.cell_7 == 2 && game.cell_8 == 2 && game.cell_9 == 2)
            || (game.cell_1 == 2 && game.cell_5 == 2 && game.cell_9 == 2)
            || (game.cell_3 == 2 && game.cell_5 == 2 && game.cell_7 == 2)
            || (game.cell_1 == 2 && game.cell_4 == 2 && game.cell_7 == 2)
            || (game.cell_2 == 2 && game.cell_5 == 2 && game.cell_8 == 2)
            || (game.cell_3 == 2 && game.cell_6 == 2 && game.cell_9 == 2)
        ) {
            uId = splitGameId[2]
            declareOnlineWinner(uId)
        } else if (game.cell_1 != 0 && game.cell_2 != 0 && game.cell_3 != 0
            && game.cell_4 != 0 && game.cell_5 != 0 && game.cell_6 != 0
            && game.cell_7 != 0 && game.cell_8 != 0 && game.cell_9 != 0
        ) {
            declareResult(1)
        }
    }


    /**
     * FUNCTION COMMENT
     *
     * @param uId: String
     * @see "fetch and declare winner"
     */
    private fun declareOnlineWinner(uId: String) {
        databaseRef.child("users")
            .orderByChild("uid")
            .equalTo(uId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (child in dataSnapshot.children) {
                        val usr = child.getValue(User::class.java)!!
                        declareWinner(usr)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TAG", databaseError.message)
                }
            })
    }


    /**
     * FUNCTION COMMENT
     *
     * @param cellId: Int, tvSelected: FontTextView
     * @see "set player move"
     */

    private fun setPlayerMove(cellId: Int, tvSelected: FontTextView) {
        tvSelected.isEnabled = false
        //
        if (currentPlayer == 1) {
            tvSelected.text = getString(R.string.fa_circle)
            player1Moves.add(cellId)
        } else if (currentPlayer == 2) {
            tvSelected.text = getString(R.string.fa_times_solid)
            player2Moves.add(cellId)
        }

        // if no winner, play random move else display winner
        if (checkWinner() == -1) {
            if (gameType == 0) {
                randomAiMove()
            } else {
                if (currentPlayer == 1) {
                    currentPlayer = 2
                } else if (currentPlayer == 2) {
                    currentPlayer = 1
                }
                checkResult()
            }
        } else {
            checkResult()
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @return winner: Int
     * @see "will check for winner after every move"
     */
    private fun checkWinner(): Int {
        var winner = -1

        if ((player1Moves.contains(1) && player1Moves.contains(2) && player1Moves.contains(3))
            || (player1Moves.contains(4) && player1Moves.contains(5) && player1Moves.contains(6))
            || (player1Moves.contains(7) && player1Moves.contains(8) && player1Moves.contains(9))
            || (player1Moves.contains(1) && player1Moves.contains(5) && player1Moves.contains(9))
            || (player1Moves.contains(3) && player1Moves.contains(5) && player1Moves.contains(7))
            || (player1Moves.contains(1) && player1Moves.contains(4) && player1Moves.contains(7))
            || (player1Moves.contains(2) && player1Moves.contains(5) && player1Moves.contains(8))
            || (player1Moves.contains(3) && player1Moves.contains(6) && player1Moves.contains(9))
        ) {
            winner = 0
        } else if ((player2Moves.contains(1) && player2Moves.contains(2) && player2Moves.contains(3))
            || (player2Moves.contains(4) && player2Moves.contains(5) && player2Moves.contains(6))
            || (player2Moves.contains(7) && player2Moves.contains(8) && player2Moves.contains(9))
            || (player2Moves.contains(1) && player2Moves.contains(5) && player2Moves.contains(9))
            || (player2Moves.contains(3) && player2Moves.contains(5) && player2Moves.contains(7))
            || (player2Moves.contains(1) && player2Moves.contains(4) && player2Moves.contains(7))
            || (player2Moves.contains(2) && player2Moves.contains(5) && player2Moves.contains(8))
            || (player2Moves.contains(3) && player2Moves.contains(6) && player2Moves.contains(9))
        ) {
            winner = 1
        }
        return winner
    }

    /**
     * FUNCTION COMMENT
     *
     * @see "get the random position"
     */
    private fun randomAiMove() {
        val availableCells = getAvailableCells()
        if (availableCells.isNotEmpty()) {
            val random = Random()
            val randomIndex = random.nextInt(availableCells.size)
            val randomCell = availableCells[randomIndex]
            player2Moves.add(randomCell)
            setAiMove(randomCell)
        } else {
            checkResult()
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @return availableCells
     * @see "get the available cells"
     */
    private fun getAvailableCells(): List<Int> {
        val availableCells = ArrayList<Int>()
        for (cellId in 1..9) {
            if (!(player1Moves.contains(cellId) || player2Moves.contains(cellId))) {
                availableCells.add(cellId)
            }
        }
        return availableCells
    }

    /**
     * FUNCTION COMMENT
     *
     * @param cellIndex: Int
     * @see "set computer move"
     */
    private fun setAiMove(cellIndex: Int) {
        val tvSelected: FontTextView? = when (cellIndex) {
            1 -> findViewById(R.id.bu1)
            2 -> findViewById(R.id.bu2)
            3 -> findViewById(R.id.bu3)
            4 -> findViewById(R.id.bu4)
            5 -> findViewById(R.id.bu5)
            6 -> findViewById(R.id.bu6)
            7 -> findViewById(R.id.bu7)
            8 -> findViewById(R.id.bu8)
            9 -> findViewById(R.id.bu9)
            else -> null
        }
        tvSelected!!.text = getString(R.string.fa_times_solid)
        tvSelected.isEnabled = false
        if (checkWinner() == -1) {
            return
        } else {
            checkResult()
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @see "check and display result"
     */
    private fun checkResult() {
        when {
            checkWinner() == 0 -> {

            if (user != null){
                declareWinner(user!!)
            }else{ declareWinner(User("", getString(R.string.player_1), "", ""))
            }
            }
            checkWinner() == 1 -> {
                if (gameType == 0) {
                    declareResult(0)
                } else {
                    declareWinner(User("", getString(R.string.player_2), "", ""))
                }
            }
            player1Moves.size + player2Moves.size >= 9 -> {
                declareResult(1)
            }
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @param i: Int
     * @see "show dialog to declare Winner"
     */

    private fun declareWinner(user: User) {
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_winner)
        dialog.window?.setBackgroundDrawableResource(R.color.bg_activity)
        Glide.with(this@MainActivity)
            .load(user.photoUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_avatar)
            .into(dialog.ivWinner)
        dialog.tvWinner.text = user.name
        dialog.ibHome.setOnClickListener {
            dialog.dismiss()
            onBackPressed()
        }
        if (gameType == 3) {
            dialog.ibReplay.visibility = View.GONE
        }
        dialog.ibReplay.setOnClickListener {
            dialog.dismiss()
            restart()
        }
        dialog.show()
    }


    /**
     * FUNCTION COMMENT
     *
     * @param i: Int
     * @see "show dialog to declare result"
     */
    private fun declareResult(i: Int) {
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_loser)
        dialog.window?.setBackgroundDrawableResource(R.color.bg_activity)
        if (i == 0) {
            dialog.tvResult.text = getString(R.string.you_lose)
        }
        if (i == 1) {
            dialog.tvResult.text = getString(R.string.tie)
        }
        dialog.ibHome.setOnClickListener {
            dialog.dismiss()
            onBackPressed()
        }
        if (gameType == 3) {
            dialog.ibReplay.visibility = View.GONE
        }
        dialog.ibReplay.setOnClickListener {
            dialog.dismiss()
            restart()
        }
        dialog.show()
    }

    /**
     * FUNCTION COMMENT
     *
     * @see "reset board"
     */
    private fun restart() {
        player1Moves.clear()
        player2Moves.clear()
        currentPlayer = 1
        for (i in 1..9) {
            val fields = findViewById<FontTextView>(
                resources.getIdentifier(
                    "bu$i", "id",
                    this.packageName
                )
            )
            fields.text = ""
            fields.isEnabled = true
        }
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
     * @see "set user info in ui"
     */
    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            user = User(
                firebaseUser.uid,
                firebaseUser.displayName,
                firebaseUser.email,
                firebaseUser.photoUrl.toString()
            )
            Glide.with(this@MainActivity)
                .load(user!!.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_avatar)
                .into(ivProfile1)
            /*var userName = "Player 1"
            if (user.name != null && user.name!!.isNotEmpty()) {
                userName = user.name!!
                if (user.name!!.contains(" ")) {
                    userName = user.name?.split(" ")?.get(0)!!
                }
            }*/
            tvName1.text = user!!.name
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * @param friendId : String
     * @see "fetch friend details and display user 2 info"
     */
    private fun setupFriend(friendId: String) {
        databaseRef.child("users")
            .orderByChild("uid")
            .equalTo(friendId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (child in dataSnapshot.children) {
                        user2 = child.getValue(User::class.java)!!
                        Glide.with(this@MainActivity)
                            .load(user2.photoUrl)
                            .centerCrop()
                            .placeholder(R.drawable.ic_avatar)
                            .into(ivProfile2)
                        tvName2.text = user2.name
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TAG", databaseError.message)
                }
            })
    }



}
