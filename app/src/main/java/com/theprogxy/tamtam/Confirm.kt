package com.theprogxy.tamtam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Confirm : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        val hashId = intent.getStringExtra("hashId")
        val confirmText: TextView = findViewById(R.id.confirmText)
        val confirmationMessage = """
            Victim record successfully stored in the database
            Victim hashId: $hashId
            
            The TamTam Team.
        """.trimIndent()
        confirmText.text = confirmationMessage

        val nextButton: Button = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}