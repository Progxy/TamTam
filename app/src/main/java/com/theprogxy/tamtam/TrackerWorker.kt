package com.theprogxy.tamtam

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TrackerWorker(private val context : Context, private val params : WorkerParameters): Worker(context, params) {

    override fun doWork() : Result {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context)

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Result.failure()
        }

        val latch = CountDownLatch(60)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    processLocation(location)
                    latch.countDown()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())


        latch.await(14, TimeUnit.MINUTES)

        fusedLocationClient.removeLocationUpdates(locationCallback)

        return Result.success()
    }

    private fun processLocation(location: Location) {
        val database = Firebase.database.reference
        val victimId = this.params.inputData.getString("victimId").toString()
        database.child(victimId).child("lastUpdate").setValue(location.time)
        database.child(victimId).child("latitude").setValue(location.latitude)
        database.child(victimId).child("longitude").setValue(location.longitude)
    }
}