package com.pavan.tictactoe

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home_screen_guest.*

class HomeScreenActivityGuest : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen_guest)
        twoplyguestbtn.setOnClickListener(this)
        compguestbtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.twoplyguestbtn -> openBoard(2)
            R.id.compguestbtn -> openBoard(0)

        }
    }
    private fun openBoard(i: Int) {
        val intent = Intent(this@HomeScreenActivityGuest, MainActivity::class.java)
        intent.putExtra("gameType", i)
        startActivity(intent)
    }
}

