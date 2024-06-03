package com.theprogxy.tamtam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : Activity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountBt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateCredentials(username, password)) {
                // Perform login action (e.g., API call or navigate to another activity)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                // Launch the MapActivity
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
            }

            usernameEditText.text.clear()
            passwordEditText.text.clear()
        }

        createAccountBt = findViewById(R.id.createAccountButton)

        createAccountBt.setOnClickListener {
            val intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateCredentials(username: String, password: String): Boolean {
        return username == "a" && password == "b"
    }
}
