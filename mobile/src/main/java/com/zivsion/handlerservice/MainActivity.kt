package com.zivsion.handlerservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.zivsion.sharedlibrary.Constants.Companion.ACTIVITY_POST
import com.zivsion.sharedlibrary.Constants.Companion.COUNTDOWN_TIME
import com.zivsion.sharedlibrary.Constants.Companion.LOCK
import com.zivsion.sharedlibrary.Constants.Companion.RECEIVED_MESSAGE_KEY
import com.zivsion.sharedlibrary.Constants.Companion.START

class MainActivity : AppCompatActivity() {
    private lateinit var startServiceMaterialButton: MaterialButton
    private lateinit var receiver: BroadcastReceiver
    private lateinit var watchStateTextView: TextView
    private lateinit var serviceStateTextView: TextView
    var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        startService()
        initializeReceiver()
        startCountDown()
        startServiceStateListener()

        startLockActionInService()
    }

    private fun startLockActionInService() {
        val intent = Intent(this, HandlerService::class.java)
        intent.action = LOCK
        startService(intent)
    }

    private fun startServiceStateListener() {
        Thread {
            while (true) {
                val serviceState = if (HandlerService.isRunning) "running" else "not Running"


                runOnUiThread {
                    serviceStateTextView.text = "Service is " + serviceState
                }
                Thread.sleep(1000)
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()

        val filter = IntentFilter(ACTIVITY_POST)
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

    private fun initViews() {
        startServiceMaterialButton = findViewById(R.id.startButton)
        startServiceMaterialButton.setOnClickListener {
            startService()
        }

        watchStateTextView = findViewById(R.id.watchState)
        serviceStateTextView = findViewById(R.id.serviceStateTextView)
    }

    private fun startService() {
        if (HandlerService.isRunning) {
            Toast.makeText(this, "Service is already running", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, HandlerService::class.java)
        intent.action = START
        startForegroundService(intent)

        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show()
    }

    private fun initializeReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val message = intent?.getStringExtra(RECEIVED_MESSAGE_KEY)

                setWatchState(connected = true)

                startCountDown()
            }
        }
    }

    private fun startCountDown(milliseconds: Long = COUNTDOWN_TIME) {
        countdownTimer?.cancel()

        countdownTimer = object : CountDownTimer(milliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                setWatchState(connected = false)
            }
        }

        countdownTimer?.start()
    }

    fun setWatchState(connected: Boolean) {
        if (connected) {
            watchStateTextView.text = "Connected"
            watchStateTextView.setTextColor(resources.getColor(R.color.green, null))
        } else {
            watchStateTextView.text = "Not Connected"
            watchStateTextView.setTextColor(resources.getColor(R.color.red, null))
        }
    }
}