package com.theprogxy.tamtam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CreateAccount : Activity() {
    private lateinit var createUsername: EditText
    private lateinit var createPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var createAccountButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_account)

        createUsername = findViewById(R.id.createUsername)
        createPassword = findViewById(R.id.createPassword)
        confirmPassword = findViewById(R.id.confirmPassword)
        createAccountButton = findViewById(R.id.createAccountButton)

        createAccountButton.setOnClickListener {
            if (isValidData(createUsername.text.toString(), createPassword.text.toString(), confirmPassword.text.toString())) {
                Toast.makeText(this, "Account successfully created", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show()

            }

            createUsername.text.clear()
            createPassword.text.clear()
            confirmPassword.text.clear()
        }

    }

    private fun isValidData(username: String, password: String, confirm: String) : Boolean {
        return !(username == "" || password == "" || confirm == "") && (password == confirm)
    }

}