package com.example.campus_pointsis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Initialise firebase auth
        auth = Firebase.auth

        val LogIn_btn: TextView = findViewById(R.id.LogIn_btn)
        LogIn_btn.setOnClickListener {
            performLogin()
        }

        val forgotText: TextView = findViewById(R.id.fgtbtn)
        forgotText.setOnClickListener {
            val intent = Intent(this, LoginOTP::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        //getting user input
        val studentID: EditText = findViewById(R.id.editID)
        val password: EditText = findViewById(R.id.editPassword)

        //null checks
        if (studentID.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val studentIDInput = studentID.text.toString()
        val passwordInput = password.text.toString()

        auth.signInWithEmailAndPassword(studentIDInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, move to main activity
                    val user = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                        Toast.makeText(baseContext,"Successful!",Toast.LENGTH_SHORT)
                        .show()

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText( baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,)
                    .show()
            }
    }
}