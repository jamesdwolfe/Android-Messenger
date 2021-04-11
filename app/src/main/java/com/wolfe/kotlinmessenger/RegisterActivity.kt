package com.wolfe.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.wolfe.kotlinmessenger.objects.User
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

private val TAG = RegisterActivity::class.qualifiedName
private var imageUri: Uri? = null

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        submitRegister.setOnClickListener {
            registerUser()
        }

        loginRegister.setOnClickListener {
            moveToLogin()
        }

        registerImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data!=null){
            Log.d(TAG,"image selected")
            imageUri = data.data

//            val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
//            val drawable = Drawable.createFromStream(inputStream, imageUri.toString())
//            registerImageButton.background = drawable

            val source = ImageDecoder.createSource(contentResolver, imageUri!!)
            val drawable = ImageDecoder.decodeBitmap(source)
            registerImageButton.alpha = 0.0f
            registerImageView.setImageBitmap(drawable)
        }
    }

    private fun moveToLogin() {
        Log.d(TAG, "Launch login activity")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun registerUser() {
        //val username = usernameRegister.text.toString()
        val email = emailRegister.text.toString()
        val password = passwordRegister.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Registration failed: Please enter an email and password",
                    Toast.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d(TAG, "Registration success: UID=${it.result?.user?.uid}")
                    uploadImageToFirebase()
                }
                .addOnFailureListener {
                    Toast.makeText(this,
                            "Registration failed: ${it.message}",
                            Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Registration failed: ${it.message}")
                }
    }

    private fun uploadImageToFirebase(){
        if(imageUri==null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(imageUri!!)
                .addOnSuccessListener {
                    Log.d(TAG,"Uploaded Image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener { it ->
                        saveUserToFirebaseDatabase(it.toString())
                    }

                }
                .addOnFailureListener {
                    Log.d(TAG,"Upload Image Failure: ${it.message}")
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, usernameRegister.text.toString(), profileImageUrl)

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG,"User added to Firebase Database")

                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to add user to Firebase Database")
                }
    }
}

