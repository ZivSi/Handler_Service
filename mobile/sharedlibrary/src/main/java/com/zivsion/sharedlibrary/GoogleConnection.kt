package com.zivsion.sharedlibrary

import android.content.Context
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Wearable
import com.zivsion.sharedlibrary.Constants.Companion.PACKAGE_NAME


const val ALIVE_MESSAGE_DELAY: Long = 1000

class GoogleConnection(private val context: Context) {
    private lateinit var googleApiClient: GoogleApiClient

    private val aliveMessage = MessageObject(PACKAGE_NAME, PACKAGE_NAME, "Alive", "Alive")

    init {
        initializeGoogleApiClient()
        connect()
        sendAlive()
    }

    private fun initializeGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(context).addApi(Wearable.API).build()
    }

    private fun connect() {
        googleApiClient.connect()
    }

    fun disconnect() {
        googleApiClient.disconnect()
    }

    fun isConnected(): Boolean {
        return googleApiClient.isConnected
    }

    fun getGoogleApiClient(): GoogleApiClient {
        return googleApiClient
    }

    fun send(messageObject: MessageObject) {
        messageObject.sentTime = System.currentTimeMillis()

        val data = messageObject.toString().toByteArray()
        Wearable.MessageApi.sendMessage(googleApiClient, "*", Constants.PATH, data)
    }

    fun sendAlive() {
        Thread {
            while (true) {
                if (!googleApiClient.isConnected) {
                    googleApiClient.connect()
                    continue
                }

                Thread.sleep(ALIVE_MESSAGE_DELAY)
                send(aliveMessage)
            }
        }.start()
    }
}

class Options : Api.ApiOptions {

}