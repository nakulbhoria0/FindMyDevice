package com.nakulbhoria.findmydevice

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.contact_item.view.*

class AddContacts : AppCompatActivity(){
    
    private var list = ArrayList<User>()
    var adapter:MyAdapter ?= null
    var ui:UserInfo?=null

    val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        ui = UserInfo(applicationContext)

        adapter = MyAdapter(this,list)
        contactsList.adapter = adapter

        loadContact()

        contactsList.setOnItemClickListener { parent, view, position, id ->
            val data = list[position]

            db.child("Users").child(ui!!.getPhone()).child("Receivers").child(data.phoneNumber!!).removeValue()
            UserInfo.myTrackers.remove(data.phoneNumber!!)
            loadContact()
        }
        
        
    }

    private fun loadContact() {

        ui!!.loadContactInfo()
        refreshList()
    }

    fun refreshList(){

        list.clear()
        for((key,value)in UserInfo.myTrackers){
            list.add(User(value,key))
            adapter!!.notifyDataSetChanged()
        }

    }

    private val PICK_CODE = 234
    fun pickContacts(){

        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

        startActivityForResult(intent,PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode){
            PICK_CODE ->{
                if(resultCode== Activity.RESULT_OK){
                    val contactData = data!!.data
                    val cr = contentResolver.query(contactData!!,null,null,null,null)

                    if(cr!!.moveToFirst()){
                        val id = cr.getString(cr.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val hasPhone = cr.getString(cr.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        if(hasPhone == "1"){
                            val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id,null,null)

                            phones!!.moveToFirst()
                            var phoneNumber = phones.getString(phones.getColumnIndexOrThrow("data1"))

                            val name = cr.getString(cr.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                            phoneNumber = UserInfo.formatNumber(phoneNumber)
                            UserInfo.myTrackers.put(phoneNumber.toString(),name.toString())

                            db.child("Users").child(phoneNumber).child("Finders").child(ui!!.getPhone()).setValue("true")
                            db.child("Users").child(ui!!.getPhone()).child("Receivers").child(phoneNumber).setValue(name.toString())
                            db.child("Users").child(phoneNumber).child("Name").setValue(name.toString())

                            ui!!.saveContactInfo()
                            refreshList()

                        }
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private val READ_CONTACTS = 123

    private fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)
                !=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),READ_CONTACTS)
                return
            }
        }
        pickContacts()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            READ_CONTACTS -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickContacts()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.contacts_menu,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.finish ->{
                finish()
            }
            R.id.addMore ->{
                checkPermission()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

class MyAdapter:BaseAdapter {
    private var list: ArrayList<User>
    private var context: Context? = null

    constructor(context:Context, list:ArrayList<User>){
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
