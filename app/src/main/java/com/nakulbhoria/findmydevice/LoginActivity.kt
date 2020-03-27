package com.nakulbhoria.findmydevice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        signInAnonymously()
    }

    private fun signInAnonymously(){
        mAuth.signInAnonymously().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val user = mAuth.currentUser
                Toast.makeText(this,"Authentication Successful",Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this,"Authentication Failed",Toast.LENGTH_LONG).show()
            }
        }
    }



    fun login(view: View) {

        val phone = editText.text.toString()
        val userInfo = UserInfo(this)
        userInfo.savePhone(phone)
        val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
        val date = Date()
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("Users").child(editText.text.toString()).child("request").setValue(df.format(date).toString())
        finish()
    }
}
