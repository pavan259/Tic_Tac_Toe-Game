package com.pavan.tictactoe

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nandroidex.texticon.FontTextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_loser.*
import kotlinx.android.synthetic.main.dialog_winner.*
import kotlinx.android.synthetic.main.dialog_winner.ibHome
import kotlinx.android.synthetic.main.dialog_winner.ibReplay
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var gameType = 0

    private var player1Moves= arrayListOf<Int>()
    private var player2Moves= arrayListOf<Int>()

    private var currentPlayer=1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        gameType = intent.getIntExtra("gameType", 0)
        auth = FirebaseAuth.getInstance()

        setupViews()
    }



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
            setPlayerMove(cellId, buSelected)
        }

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

    private fun getAvailableCells(): List<Int> {
        val availableCells = ArrayList<Int>()
        for (cellId in 1..9) {
            if (!(player1Moves.contains(cellId) || player2Moves.contains(cellId))) {
                availableCells.add(cellId)
            }
        }
        return availableCells
    }

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

    private fun checkResult() {
        when {
            checkWinner() == 0 -> {


                    declareWinner(User("", getString(R.string.player_1), "", ""))

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
        dialog.ibReplay.setOnClickListener {
            dialog.dismiss()
            restart()
        }
        dialog.show()
    }


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
        dialog.ibReplay.setOnClickListener {
            dialog.dismiss()
            restart()
        }
        dialog.show()
    }
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

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            user = User(
                firebaseUser.uid,
                firebaseUser.displayName,
                firebaseUser.email,
                firebaseUser.photoUrl.toString()
            )
            Glide.with(this@MainActivity)
                .load(user.photoUrl)
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
            tvName1.text = user.name
        }
    }



}
