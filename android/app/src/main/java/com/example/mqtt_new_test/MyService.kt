package com.example.mqtt_new_test

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import info.mqtt.android.service.MqttAndroidClient
import info.mqtt.android.service.QoS
import org.eclipse.paho.client.mqttv3.*


class MyService : Service() {
    private lateinit var mqttAndroidClient: MqttAndroidClient
    private val serverUri:String = "tcp://mqtt.monitoring6.com:1883"
    private val clientId:String = "시인클라이언트"
    private val subscriptionTopic:String = "happy"
    companion object{
        val ACTION_CREATE = "create"
        val ACTION_DELETE = "delete"
        const val NOTIFICATION_ID = 10
        const val CHANNEL_ID = "primary_notification_channel"
    }


    override fun onCreate() {
        super.onCreate()
        Toast.makeText(applicationContext, "onCreate 서비스", Toast.LENGTH_SHORT).show()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MyService is running")
                .setContentText("MyService is running")
                .build()
            Toast.makeText(applicationContext, "start foreground", Toast.LENGTH_SHORT).show()
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "MyApp notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "AppApp Tests"

        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            notificationChannel)
    }

    override fun onBind(intent: Intent): IBinder {
        Toast.makeText(applicationContext, "야호오오", Toast.LENGTH_SHORT).show()
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when(action){
            ACTION_CREATE -> create()
            ACTION_DELETE -> delete()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun create() {
        Toast.makeText(applicationContext, "서비스 생성!", Toast.LENGTH_SHORT).show()
        brokerConnect()
        //subscribeToTopic()
    }
    fun delete() {
        Toast.makeText(applicationContext, "서비스 삭제!", Toast.LENGTH_SHORT).show()
    }
    fun subscribeToTopic() {
        mqttAndroidClient.subscribe(subscriptionTopic, QoS.AtMostOnce.value, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                //addToHistory("Subscribed! $subscriptionTopic")
                Toast.makeText(applicationContext, "Subscribed to" + subscriptionTopic, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                //addToHistory("Failed to subscribe $exception")
                Toast.makeText(applicationContext, "Failed to subscribe" + exception, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun brokerConnect() {
        mqttAndroidClient = MqttAndroidClient(applicationContext, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                if (reconnect) {
                    Toast.makeText(applicationContext, "Reconnected", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()
                }
            }

            override fun connectionLost(cause: Throwable?) {
                //addToHistory("The Connection was lost.")
                Toast.makeText(applicationContext, "The Connection was lost.", Toast.LENGTH_SHORT).show()
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                //addToHistory("Incoming message: " + String(message.payload))

                Toast.makeText(applicationContext, "Incoming message: " + String(message.payload), Toast.LENGTH_SHORT).show()
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {}
        })
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        //addToHistory("Connecting: $serverUri")
        Toast.makeText(applicationContext, "Connecting", Toast.LENGTH_SHORT).show()

        mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                val disconnectedBufferOptions = DisconnectedBufferOptions()
                disconnectedBufferOptions.isBufferEnabled = true
                disconnectedBufferOptions.bufferSize = 100
                disconnectedBufferOptions.isPersistBuffer = false
                disconnectedBufferOptions.isDeleteOldestMessages = false
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                //subscribeToTopic()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                //addToHistory("Failed to connect: $serverUri")
                Toast.makeText(applicationContext, "Failed to connect" + exception, Toast.LENGTH_SHORT).show()
            }
        })
    }
}