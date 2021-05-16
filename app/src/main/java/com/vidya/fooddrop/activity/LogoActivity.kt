package com.vidya.fooddrop.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.vidya.fooddrop.R

class LogoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)
        this.supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this@LogoActivity, LogInActivity::class.java)
            startActivity(intent)
        }, 2000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
