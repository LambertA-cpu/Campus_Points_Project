package com.example.campus_pointsis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import javax.annotation.Nonnull

class LoginOTP : AppCompatActivity() {

    // get reference of the firebase auth
    private lateinit var auth: FirebaseAuth
    private lateinit var otp: EditText
    private lateinit var phone: EditText
    private lateinit var generateBtn: Button
    private lateinit var verifyBtn: Button
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_otp)

        auth = FirebaseAuth.getInstance()

        // get storedVerificationId from the intent
        val storedVerificationId = intent.getStringExtra("storedVerificationId")

        otp = findViewById(R.id.otp)
        verifyBtn = findViewById(R.id.verifyBtn)
        phone = findViewById(R.id.phone)
        generateBtn = findViewById(R.id.generateBtn)

        generateBtn.setOnClickListener {
            val number: String = phone.text.toString() // Declare the variable here

            if (number.isEmpty()) {
                Toast.makeText(this, "Please enter a valid phone number", LENGTH_SHORT)
                    .show()
            } else {
                sendVerificationCode(number)
            }
        }

        verifyBtn.setOnClickListener {
            if (TextUtils.isEmpty(this.otp.text.toString())) {
                Toast.makeText(this, "invalid OTP entered", LENGTH_SHORT).show()
            } else {
                verifyCode(this.otp.text.toString())
            }
        }

        verifyBtn.setOnClickListener {
            val otp = this.otp.text.trim().toString()
            if (otp.isNotEmpty()) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Enter OTP", LENGTH_SHORT).show()
            }
        }
    }


    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+254$phoneNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(@Nonnull credential: PhoneAuthCredential) {

            val code = credential.smsCode
            if (code!=null){
                verifyCode(code)
            }

        }

        override fun onVerificationFailed(@Nonnull e: FirebaseException) {
            // Show a message and update the UI
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(applicationContext, "Verification failed", LENGTH_SHORT).show()
            }
        }

        // Move the onCodeSent function here, at the same level as onVerificationFailed
        override fun onCodeSent(@Nonnull s: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, token)
            verificationId = s
        }

    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signinbyCredentials (credential)

    }

    private fun signinbyCredentials(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult> ->
                // Your code for onComplete goes here
                if (task.isSuccessful){
                    Toast.makeText(this,"Login Successfull", LENGTH_SHORT).show()
                }
            }

    }

    // start the new activity if the code matches sent by firebase
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, "Invalid OTP", LENGTH_SHORT).show()
                    }
                }
            }
    }
}
