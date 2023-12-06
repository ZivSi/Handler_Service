package com.zivsion.handlerservice

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.zivsion.sharedlibrary.Constants
import com.zivsion.sharedlibrary.Constants.Companion.ACTIVITY_POST
import com.zivsion.sharedlibrary.Constants.Companion.ALIVE
import com.zivsion.sharedlibrary.Constants.Companion.FORWARD_TO_OTHER_SIDE
import com.zivsion.sharedlibrary.Constants.Companion.LOCK
import com.zivsion.sharedlibrary.Constants.Companion.RECEIVED_MESSAGE_KEY
import com.zivsion.sharedlibrary.Constants.Companion.SALARY
import com.zivsion.sharedlibrary.Constants.Companion.START
import com.zivsion.sharedlibrary.Constants.Companion.STOP
import com.zivsion.sharedlibrary.GoogleConnection
import com.zivsion.sharedlibrary.MessageObject
import com.zivsion.sharedlibrary.Utils
import com.zivsion.sharedlibrary.Utils.Companion.relevantMessage


const val FILENAME_EXTRA_KEY = "filename"
const val SAVED_TO_EXTRA_KEY = "saved_to"

class HandlerService : WearableListenerService() {
    private lateinit var googleConnection: GoogleConnection
    private lateinit var broadcastReceiver: BroadcastReceiver

    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val NOTIFICATION_ID = 1
        var isRunning = false

        var salary = -1
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("HandlerService", "onCreate")

        googleConnection = GoogleConnection(this)
        initializeBroadcastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HandlerService", "onDestroy")

        isRunning = false

        googleConnection.disconnect()

        unregisterReceiver(broadcastReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = Utils.createNotification(this)
        startForeground(NOTIFICATION_ID, notification)

        val message = intent?.getStringExtra(Constants.MESSAGE_PATH)
        val messageObject = MessageObject.fromString(message ?: return START_STICKY)

        if(serviceIsDestination(messageObject)) {
            process(messageObject)
        }

        when (val action = intent?.action) {
            START -> {
                googleConnection.sendAlive()

                isRunning = true
            }

            STOP -> {
                stopSelf()
                isRunning = false
            }

            FORWARD_TO_OTHER_SIDE -> {
                val intentContent =
                    intent.getStringExtra(RECEIVED_MESSAGE_KEY) ?: return START_STICKY

                val message = MessageObject.fromString(intentContent)
                message.print()

                googleConnection.send(message)
            }

            LOCK -> {
                val devicePolicyManager =
                    getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                val componentName = ComponentName(this, DeviceAdminSampleReceiver::class.java)

                lock(devicePolicyManager, componentName)
            }

            else -> {
                Log.e("Unknown Action", "HandlerService Unknown action: $action")
            }
        }

        return START_STICKY
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        val message = MessageObject.fromString(String(messageEvent.data))
        message.print()

        if (relevantMessage(messageEvent)) {
            postToActivity(message.toString())

            process(message)
        }
    }

    private fun serviceIsDestination(messageEvent: MessageObject): Boolean {
        Log.d("HandlerService", "serviceIsDestination: ${messageEvent.to == packageName}")

        return messageEvent.to == packageName
    }

    private fun process(message: MessageObject) {
        if (!serviceIsDestination(message)) {
            broadcastMessage(message)

            return
        }

        when (message.action) {
            ALIVE -> {
                Log.e("ALIVE", "Watch is connected")
            }

            SALARY -> {
                Log.e("SALARY", "Salary is received: " + salary)
                salary = message.content.toInt()
            }

            else -> {
                Log.e("Unknown Action", "HandlerService Unknown action: ${message.action}")
            }
        }
    }

    private fun lock(devicePolicyManager: DevicePolicyManager, componentName: ComponentName) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.lockNow()
        }
    }

    private fun initializeBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("HandlerService", "broadcastReceiver onReceive")

                val message = intent?.getStringExtra(Constants.MESSAGE_PATH)
                Log.d("HandlerService", "broadcastReceiver message: $message")

                postToActivity(message)
            }
        }

        val intentFilter = IntentFilter(Constants.SERVICE_BROADCAST_FILTER)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun postToActivity(message: String?) {
        val intent = Intent(ACTIVITY_POST)
        intent.putExtra(RECEIVED_MESSAGE_KEY, message)
        sendBroadcast(intent)
    }

    private fun broadcastMessage(message: MessageObject) {
        val intent = Intent(message.to)
        intent.component = ComponentName(message.to, message.receiverPackageName)
        intent.putExtra(Constants.MESSAGE_PATH, message.toString())
        sendBroadcast(intent)
    }
}

