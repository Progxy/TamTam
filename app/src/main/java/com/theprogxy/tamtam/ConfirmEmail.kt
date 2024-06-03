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
        val confirmationText = "Confirm the account by visiting the link sent to $email"
        confirmText.text = confirmationText

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}