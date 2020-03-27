package com.nakulbhoria.findmydevice


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val database = FirebaseDatabase.getInstance().reference
    var lastOnline = ""

    var currentLocation = LatLng(0.0, 0.0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val bundle = intent.extras
        val phone = bundle!!.getString("phone")
        database.child("Users").child(phone!!).child("Location").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                try{
                    val td = p0.value as HashMap<String, Any>
                    val lat1 = td["lat"].toString()
                    val lon1 = td["long"].toString()
                    currentLocation = LatLng(lat1.toDouble(), lon1.toDouble())
                    lastOnline = td["lastOnline"].toString()
                    setMap()
                }catch(ex:Exception){}
            }

        })

    }
    fun setMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera


        mMap.addMarker(MarkerOptions().position(currentLocation).title(lastOnline))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,12f))
    }
}
