package com.example.mqtt_new_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi

class MyReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        if(intent.action.equals(Intent.ACTION_BOOT_COMPLETED)){
            val intent = Intent(context, MyService::class.java)
            intent.action = MyService.ACTION_CREATE
            //ontext.startForegroundActivity(i)
            //context.startForegroundService(intent);
            Toast.makeText(context, "Subscribed to" , Toast.LENGTH_SHORT).show()
        }
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        TODO("MyReceiver.onReceive() is not implemented")
    }
}