package com.theprogxy.tamtam

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.Gravity.CENTER_VERTICAL
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.concurrent.TimeUnit

const val MY_PERMISSIONS_REQUEST_MULTIPLE = 4

class RegisterActivity : Activity() {
    private lateinit var idEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!this.checkPermission()) {
            val text : String = """
                Permission needed to get current location
            """.trimIndent()
            this.createCenteredText(text)
        }
        else {
            this.userPreferences = UserPreferences()
            val victimId: String? = this.userPreferences.getValue("victimId", this)
            if (victimId != null) this.startTracking(victimId)
            else this.showIdEdit()
        }
    }

    private fun checkPermission() : Boolean {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        val permissionStatus = permissions.map {
            ContextCompat.checkSelfPermission(this, it)
        }

        if (!permissionStatus.all { it == PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_MULTIPLE)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_MULTIPLE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                    finish()
                    return
                } else {
                    print("Permission not granted")
                    return
                }
            }
        }
    }

    private fun showIdEdit() {
        setContentView(R.layout.activity_register)

        idEditText = findViewById(R.id.idEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val victimCreatedId = idEditText.text.toString()

            if (victimCreatedId != "") {
                val id: String = hashCode().toString()
                if (!this.userPreferences.setValue("victimId", id, this)) {
                    Toast.makeText(this, "Failed registering the victim's id, please retry", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                this.registerVictim(id, victimCreatedId)

                val intent = Intent(this, Confirm::class.java)
                intent.putExtra("hashId", id)
                startActivity(intent)
                finish()
            } else Toast.makeText(this, "Empty victim id", Toast.LENGTH_SHORT).show()

            idEditText.text.clear()
        }

        return
    }

    private fun registerVictim(id: String, victimId: String) {
        val database = Firebase.database.reference
        database.child(id).child("id").setValue(victimId)
        database.child(id).child("isTracked").setValue(false)
        val time = Calendar.getInstance().time.time
        database.child(id).child("lastUpdate").setValue(time)
        database.child(id).child("latitude").setValue(Math.random() * 100)
        database.child(id).child("longitude").setValue(Math.random() * 100)
        return
    }

    private fun startTracking(victimId: String) {
        val data = Data.Builder().putString("victimId", victimId).build()
        // Note that it will be reset to 15 Minutes as it's the minimum repeat interval (hardcoded system value for battery saving)
        val workRequest = PeriodicWorkRequestBuilder<TrackerWorker>(1, TimeUnit.MINUTES).setInputData(data).build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
        val text = """
            Me... Nothing, just hanging around...
        """.trimIndent()
        this.createCenteredText(text)
    }

    private fun createCenteredText(text: String) {
        val linearLayout = LinearLayout(this).apply {
            orientation = VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = CENTER_VERTICAL
        }

        val textView = TextView(this).apply {
            textSize = 18f
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            layoutParams = LinearLayout.LayoutParams(
                1000,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
            gravity = CENTER_HORIZONTAL
        }

        textView.text = text

        linearLayout.addView(textView)

        setContentView(linearLayout)
    }

}
