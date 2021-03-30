package com.wolfe.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

private val TAG = RegisterActivity::class.qualifiedName

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        submitRegister.setOnClickListener {
            val username = usernameRegister.text.toString()
            val email = emailRegister.text.toString()
            val password = passwordRegister.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                    "Registration failed: Please enter an email and password",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this,
                        "Registration success",
                        Toast.LENGTH_LONG).show()
                    Log.d(TAG,"Registration success: UID=${it.result!!.user.uid}")
                }
                .addOnFailureListener {
                    Toast.makeText(this,
                        "Registration failed: ${it.message}",
                        Toast.LENGTH_LONG).show()
                    Log.d(TAG,"Registration failed: ${it.message}")
                }
        }

        loginRegister.setOnClickListener {
            Log.d(TAG, "Launch login activity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}