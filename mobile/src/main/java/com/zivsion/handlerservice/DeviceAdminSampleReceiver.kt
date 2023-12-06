package com.zivsion.handlerservice

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DeviceAdminSampleReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        Log.d("DeviceAdminSampleReceiver", "onEnabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Log.d("DeviceAdminSampleReceiver", "onDisabled")
    }
}
