package com.example.mqtt_new_test

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import info.mqtt.android.service.MqttAndroidClient
import info.mqtt.android.service.QoS
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import org.eclipse.paho.client.mqttv3.*

class MainActivity: FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/battery"
    private lateinit var mqttAndroidClient: MqttAndroidClient
    //tcp://broker.hivemq.com:1883
    private val serverUri:String = "tcp://mqtt.monitoring6.com:1883"
    private val clientId:String = "시인클라이언트"
    private val subscriptionTopic:String = "happy"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call, result ->
            if (call.method == "brokerConnect") {
                brokerConnect()
            }
            else if (call.method == "flutterPublishMessage") {
                publishMessage()
            }
            else if (call.method == "flutterSubscribeMessage") {
                subscribeToTopic()
            }
            else if (call.method == "startService") {
                serviceStart()
            }
            else {
                result.notImplemented()
            }

            //flutterSubscribeMessage
            // This method is invoked on the main thread.
            // TODO
        }
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

    private fun publishMessage() {
        val message = MqttMessage()
        message.payload = publishMessage.toByteArray()
        if (mqttAndroidClient.isConnected) {
            mqttAndroidClient.publish(publishTopic, message)
            //addToHistory("Message Published >$publishMessage<")
            Toast.makeText(applicationContext, "Message Published" + message, Toast.LENGTH_SHORT).show()
            if (!mqttAndroidClient.isConnected) {
                //addToHistory(mqttAndroidClient.bufferedMessageCount.toString() + " messages in buffer.")
                Toast.makeText(applicationContext, mqttAndroidClient.bufferedMessageCount.toString() + " messages in buffer.", Toast.LENGTH_SHORT).show()
            }
        } else {
            //Snackbar.make(findViewById(android.R.id.content), "Not connected", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
            Toast.makeText(applicationContext, "Published Not connected", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun serviceStart() {
        val intent = Intent(this, MyService::class.java)
        intent.action = MyService.ACTION_CREATE
        //startService(intent)
        startForegroundService(intent)
    }

    companion object {
        private const val serverUri = "tcp://mqtt.monitoring6.com:1883"
        private const val subscriptionTopic = "exampleAndroidTopic"
        private const val publishTopic = "all"
        private const val publishMessage = "시인 플러터 테스트입니다"
        private var clientId = "BasicSample"
    }
}
