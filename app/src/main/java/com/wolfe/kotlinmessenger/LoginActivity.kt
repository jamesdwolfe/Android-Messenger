package com.wolfe.kotlinmessenger

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

private val TAG = LoginActivity::class.qualifiedName

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        submitLogin.setOnClickListener {
            Log.d(TAG, "Username is: ${usernameLogin.text.toString()}")
            Log.d(TAG, "Password is: ${passwordLogin.text.toString()}")
        }

        registerLogin.setOnClickListener {
            Log.d(TAG, "Launch register activity")
//            val intent = Intent(this, RegisterActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivity(intent)
            finish()
        }
    }

}