package com.nakulbhoria.findmydevice

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MyService: Service() {

    var database:DatabaseReference?=null
    var myNumber:String?=null
    var locationManager:LocationManager?=null

    override fun onBind(intent: Intent?): IBinder? {
        return null!!
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val myLocation = MyLocationListener(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try{
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3, 3f, myLocation)
        }catch (ex:SecurityException){}



        val userInfo = UserInfo(applicationContext)
        myNumber = UserInfo.formatNumber(userInfo.getPhone())

        database!!.child("users").child(myNumber!!).addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                if(location == null) return
                val dateFormat = SimpleDateFormat("yyyy/MMM/dd hh:mm:ss")
                val date = Date()
                database!!.child("Users").child(myNumber!!).child("Location").child("lat")
                    .setValue(location.latitude)
                database!!.child("Users").child(myNumber!!).child("Location").child("long")
                    .setValue(location.longitude)
                database!!.child("Users").child(myNumber!!).child("Location").child("lastOnline")
                    .setValue(dateFormat.format(date))
            }

        })

        return START_NOT_STICKY
    }
    companion object{
        var location:Location = Location("Start")
        var isActivityRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance().reference
        isActivityRunning = true
    }
    class MyLocationListener : LocationListener {
        var context:Context?=null
        constructor(context: Context):super() {
            this.context = context
            location = Location("Start")
        }

        override fun onLocationChanged(location1: Location?) {
            location = location1!!
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }


    }
}