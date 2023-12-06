package com.zivsion.handlerservice;

import static com.zivsion.sharedlibrary.Constants.ACTIVITY_POST;
import static com.zivsion.sharedlibrary.Constants.RECEIVED_MESSAGE_KEY;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;

public class WearableListenerService extends com.google.android.gms.wearable.WearableListenerService {
    public static final String TAG = "WearableListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String message = new String(messageEvent.getData());

        Log.e(TAG, "onMessageReceived: " + message);

        // Broadcast the message to the MainActivity
        Intent intent = new Intent(ACTIVITY_POST);
        intent.putExtra(RECEIVED_MESSAGE_KEY, message);
        sendBroadcast(intent);
    }
}