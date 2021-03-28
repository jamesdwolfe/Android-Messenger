package com.wolfe.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        submitRegister.setOnClickListener {
            Log.d("RegisterActivity", "Username is: ${usernameRegister.text.toString()}")
            Log.d("RegisterActivity", "Email is: ${emailRegister.text.toString()}")
            Log.d("RegisterActivity", "Password is: ${passwordRegister.text.toString()}")
        }

        loginRegister.setOnClickListener {
            Log.d("RegisterActivity", "Launch log activity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}