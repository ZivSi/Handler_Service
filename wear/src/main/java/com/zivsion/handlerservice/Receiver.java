package com.zivsion.handlerservice;

import static com.zivsion.sharedlibrary.Constants.BOOT_ACTION;
import static com.zivsion.sharedlibrary.Constants.RECEIVED_MESSAGE_KEY;
import static com.zivsion.sharedlibrary.Constants.START;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "onReceive: " + intent.getAction());

        if (intent.getAction().equals(BOOT_ACTION)) {
            startService(context);

            Log.d("Receiver", "Starting service");
        } else {
            forwardMessage(context, intent);

            Log.d("Receiver", "Forwarding message: " + intent.getStringExtra("message"));
        }
    }

    private static void forwardMessage(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, WearService.class);
        serviceIntent.putExtra(RECEIVED_MESSAGE_KEY, intent.getStringExtra("message"));
        context.startService(serviceIntent);
    }

    private static void startService(Context context) {
        Intent serviceIntent = new Intent(context, WearService.class);
        serviceIntent.setAction(START);
        context.startForegroundService(serviceIntent);
    }
}
