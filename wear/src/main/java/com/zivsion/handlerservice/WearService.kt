package com.zivsion.handlerservice

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.zivsion.sharedlibrary.Constants
import com.zivsion.sharedlibrary.Constants.Companion.ACTIVITY_POST
import com.zivsion.sharedlibrary.Constants.Companion.ALIVE
import com.zivsion.sharedlibrary.Constants.Companion.MESSAGE_PATH
import com.zivsion.sharedlibrary.Constants.Companion.RECEIVED_MESSAGE_KEY
import com.zivsion.sharedlibrary.Constants.Companion.SERVICE_BROADCAST_FILTER
import com.zivsion.sharedlibrary.Constants.Companion.START
import com.zivsion.sharedlibrary.Constants.Companion.STOP
import com.zivsion.sharedlibrary.GoogleConnection
import com.zivsion.sharedlibrary.MessageObject
import com.zivsion.sharedlibrary.Utils
import com.zivsion.sharedlibrary.Utils.Companion.relevantMessage

class WearService : WearableListenerService() {
    private lateinit var googleConnection: GoogleConnection
    private lateinit var broadcastReceiver: BroadcastReceiver

    companion object {

        const val NOTIFICATION_ID = 1

        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        println("WearService onCreate")

        googleConnection = GoogleConnection(this)
        initializeBroadcastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        println("WearService onDestroy")

        isRunning = false

        googleConnection.disconnect()

        unregisterReceiver(broadcastReceiver)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("WearService onStartCommand")

        val notification = Utils.createNotification(this)
        startForeground(NOTIFICATION_ID, notification)

        when (intent?.action) {

            START -> {
                googleConnection.sendAlive()

                isRunning = true
            }

            STOP -> {
                stopSelf()
                isRunning = false
            }

            Constants.FORWARD_TO_OTHER_SIDE -> {
                val intentContent =
                    intent.getStringExtra(RECEIVED_MESSAGE_KEY) ?: return START_STICKY

                val message = MessageObject.fromString(intentContent)
                message.print()

                googleConnection.send(message)
            }


            else -> {
                Log.e("Unknown Action", "WearService Unknown action: ${intent?.action}")
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

    private fun process(message: MessageObject) {
        if (!serviceIsDestination(message)) {
            broadcastMessage(message)

            return
        }

        when (message.action) {
            ALIVE -> {
                Log.e("ALIVE", "Phone is connected")
            }

            else -> {
                Log.e("Unknown Action", "WearService Unknown action: ${message.action}")
            }
        }
    }

    private fun serviceIsDestination(messageEvent: MessageObject): Boolean {
        // If service is the destination, message.action won't be "No Action"
        return messageEvent.to == packageName
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        super.onDataChanged(p0)

        println("WearService onDataChanged")

        postToActivity("WearService onDataChanged")
    }


    private fun initializeBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                println("WearService broadcastReceiver onReceive")

                val message = intent?.getStringExtra(MESSAGE_PATH)
                println("WearService broadcastReceiver message: $message")

                postToActivity(message)
            }
        }

        val intentFilter = IntentFilter(SERVICE_BROADCAST_FILTER)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun postToActivity(message: String?) {
        val intent = Intent(ACTIVITY_POST)
        intent.putExtra(RECEIVED_MESSAGE_KEY, message)
        sendBroadcast(intent)
    }

    private fun broadcastMessage(message: MessageObject) {
        val intent = Intent(SERVICE_BROADCAST_FILTER)
        intent.component =
            ComponentName(message.to, message.receiverPackageName)
        intent.putExtra(MESSAGE_PATH, message.toString())
        sendBroadcast(intent)
    }
}