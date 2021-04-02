package com.wolfe.kotlinmessenger

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

private val TAG = LoginActivity::class.qualifiedName

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        submitLogin.setOnClickListener {
            loginUser()
        }

        registerLogin.setOnClickListener {
            moveToRegister()
        }
    }

    private fun moveToRegister() {
        Log.d(TAG, "Launch register activity")
        finish()
    }

    private fun loginUser() {
        val email = emailLogin.text.toString()
        val password = passwordLogin.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Login failed: Please enter an email and password",
                    Toast.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this,
                            "Login success",
                            Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Login success: UID=${it.result?.user?.uid}")
                }
                .addOnFailureListener {
                    Toast.makeText(this,
                            "Login failed: ${it.message}",
                            Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Login failed: ${it.message}")
                }
    }

}