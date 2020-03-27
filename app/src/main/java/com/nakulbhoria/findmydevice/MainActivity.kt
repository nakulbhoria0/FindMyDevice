package com.nakulbhoria.findmydevice

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contact_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private var list = ArrayList<User>()

    var adapter: MainAdapter? = null
    var myNumber: String? = null
    val db = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userInfo = UserInfo(this)
        myNumber = UserInfo.formatNumber(userInfo.getPhone())


        if(!MyService.isActivityRunning){

            checkLocationPermission()
        }



        adapter = MainAdapter(this, list)
        listView.adapter = adapter



        listView.setOnItemClickListener { parent, view, position, id ->
            val userData = list[position]

            val mobile = userData.phoneNumber

            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("phone", mobile)
            startActivity(intent)
        }



    }

    fun getUserLocation() {


        if(!MyService.isActivityRunning) {
            val intent = Intent(baseContext, MyService::class.java)
            startService(intent)
        }

    }

    fun addData() {

        list.clear()


        db!!.child("Users").child(myNumber!!).child("Receivers")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.value != null) {
                        val td = dataSnapshot.value as HashMap<String, Any>

                        for (key in td.keys) {
                            val value = td[key].toString()
                            list.add(User(value, key))
                            adapter!!.notifyDataSetChanged()
                        }


                    }
                }

            })
    }

    val LOCATION = 124
    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION)
                return
            }
        }
        getUserLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onResume() {
        addData()
        if(!MyService.isActivityRunning){

            checkLocationPermission()
        }
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.add -> {
                val intent = Intent(this, AddContacts::class.java)
                startActivity(intent)
            }
            R.id.help -> {
                //TODO()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

class MainAdapter: BaseAdapter {
    private var list: ArrayList<User>
    private var context: Context? = null

    constructor(context: Context, list:ArrayList<User>){
        this.context = context
        this.list = list
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentUser = list[position]
        val myView = LayoutInflater.from(context).inflate(R.layout.contact_item,null)


        myView.tvName.text = currentUser.name
        myView.tvNumber.text = currentUser.phoneNumber

        return myView
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}
