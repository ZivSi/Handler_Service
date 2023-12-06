package com.zivsion.handlerservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.zivsion.sharedlibrary.Constants
import com.zivsion.sharedlibrary.Constants.Companion.ACTIVITY_POST
import com.zivsion.sharedlibrary.Constants.Companion.RECEIVED_MESSAGE_KEY
import com.zivsion.sharedlibrary.Constants.Companion.START
import com.zivsion.sharedlibrary.Constants.Companion.TURN_OFF


class MainActivity : ComponentActivity() {
    private lateinit var receiver: BroadcastReceiver
    private lateinit var mainScrollView: ScrollView
    private lateinit var serviceStateTextView: TextView
    private lateinit var mobileStateTextView: TextView
    private lateinit var startServiceButton: Button
    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initViews()
        startService()
        initializeReceiver()
        startCountDown()
        startServiceStateListener()
    }

    private fun startServiceStateListener() {
        Thread {
            while (true) {
                val serviceState = if (WearService.isRunning) "running" else "not Running"


                runOnUiThread {
                    serviceStateTextView.text = "Service is " + serviceState
                }
                Thread.sleep(1000)
            }
        }.start()
    }


    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter(ACTIVITY_POST)
        registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

    private fun initViews() {
        serviceStateTextView = findViewById(R.id.serviceStatusTextView)
        mobileStateTextView = findViewById(R.id.mobileStateTextView)
        startServiceButton = findViewById(R.id.startServiceButton)
        startServiceButton.setOnClickListener {
            startService()
        }
        mainScrollView = findViewById(R.id.mainScrollView)
        mainScrollView.requestFocus()
    }

    private fun startService() {
        println("MainActivity startService")

        if (WearService.isRunning) {
            Toast.makeText(this, "Service is already running", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, WearService::class.java)
        intent.action = START
        startForegroundService(intent)

        Toast.makeText(this, "Started Service", Toast.LENGTH_SHORT).show()
    }


    private fun initializeReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val message = intent?.getStringExtra(RECEIVED_MESSAGE_KEY)

                setMobileState(connected = true)

                startCountDown()
            }
        }
    }

    private fun startCountDown(milliseconds: Long = Constants.COUNTDOWN_TIME) {
        countdownTimer?.cancel()

        countdownTimer = object : CountDownTimer(milliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                setMobileState(connected = false)
            }
        }

        countdownTimer?.start()
    }

    private fun setMobileState(connected: Boolean) {
        if (connected) {
            mobileStateTextView.text = "Connected"
            mobileStateTextView.setTextColor(resources.getColor(R.color.green, null))
        } else {
            mobileStateTextView.text = "Not Connected"
            mobileStateTextView.setTextColor(resources.getColor(R.color.red, null))
        }
    }

    fun lockPhone(view: View) {
        val intent = Intent(this, WearService::class.java)
        intent.action = TURN_OFF
        startForegroundService(intent)
    }
}
