package com.nakulbhoria.findmydevice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class UserInfo {
    var context:Context?=null
    var pref:SharedPreferences?=null
    constructor(context: Context){
        this.context = context
        pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
    }

    fun savePhone(phone:String){
        val editor = pref!!.edit()
        editor.putString("phone", phone)
        editor.apply()

    }

    fun getPhone():String {

        val phone = pref!!.getString("phone","empty")
        if(phone.equals("empty")){
            val intent = Intent(context,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(intent)
        }
        return phone!!

    }

    fun saveContactInfo(){
        var str =""
        for((key,value) in myTrackers) {

            if(str.isEmpty()){
                str = "$key%$value%"
            }else{
                str += "$key%$value%"
            }
        }

        val editor = pref!!.edit()
        editor.putString("myTrackers", str)
        editor.apply()
    }
    fun loadContactInfo(){
        myTrackers.clear()
        val trackersList = pref!!.getString("myTrackers","")
        if(trackersList!!.isNotEmpty()){
            val userInfo = trackersList.split("%").toTypedArray()

            var i =0
            while(i+1<userInfo.size){
                myTrackers.put(userInfo[i],userInfo[i+1])
                i+=2
            }
        }
    }

    companion object{
        val myTrackers:MutableMap<String,String> = HashMap()

        fun formatNumber(st:String):String
        {
            var stringst = st.replace("\\s".toRegex(),"")
            return if(stringst.contains("+91")){
                stringst
            }else{
                "+91$stringst"
            }
        }
    }

}