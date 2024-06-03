package com.theprogxy.tamtam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ConfirmEmail : Activity() {
    private lateinit var confirmText: TextView
    private lateinit var nextButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_email)

        val email = intent.getStringExtra("email")
        confirmText = findViewById(R.id.confirmText)
        val confirmationMessage = """
            Account Created Successfully!
            
            A confirmation link has been sent to $email. Please check your email and click on the link to confirm your account.

            If you do not see the email in your inbox, please check your spam or junk folder.
            
            Thank you for registering with us!
            
            The TamTam team.
        """.trimIndent()
        confirmText.text = confirmationMessage

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}