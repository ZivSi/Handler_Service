package com.zivsion.handlerservice;


import static com.zivsion.sharedlibrary.Constants.BOOT_ACTION;
import static com.zivsion.sharedlibrary.Constants.FORWARD_TO_OTHER_SIDE;
import static com.zivsion.sharedlibrary.Constants.RECEIVED_MESSAGE_KEY;
import static com.zivsion.sharedlibrary.Constants.START;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "onReceive: " + intent.getAction());
        String content = intent.getStringExtra("message") + "";

        if (isReboot(intent)) {
            startService(context);

            Log.d("Receiver", "Starting service");
        } else {
            forwardMessage(context, intent);

            Log.d("Receiver", "Forwarding message: " + content);
        }
    }

    private boolean isReboot(Intent intent) {
        return Objects.equals(intent.getAction(), BOOT_ACTION);
    }

    private static void forwardMessage(Context context, Intent intent) {
        String content = intent.getStringExtra("message") + "";

        Intent serviceIntent = new Intent(context, HandlerService.class);
        serviceIntent.setAction(FORWARD_TO_OTHER_SIDE);
        serviceIntent.putExtra(RECEIVED_MESSAGE_KEY, content);
        context.startService(serviceIntent);
    }

    private static void startService(Context context) {
        Intent serviceIntent = new Intent(context, HandlerService.class);
        serviceIntent.setAction(START);
        context.startForegroundService(serviceIntent);
    }
}
