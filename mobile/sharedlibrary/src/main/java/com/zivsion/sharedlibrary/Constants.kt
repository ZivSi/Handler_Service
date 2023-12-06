package com.zivsion.sharedlibrary

class Constants {
    companion object {
        const val TURN_OFF = "com.zivsion.action.TURN_OFF"
        const val START = "com.zivsion.action.START"
        const val STOP = "com.zivsion.action.STOP"
        const val LOCK = "com.zivsion.action.LOCK"
        const val FORWARD_TO_OTHER_SIDE = "com.zivsion.action.FORWARD_TO_OTHER_SIDE"
        const val ACTIVITY_POST =
            "com.zivsion.action.ACTIVITY_POST" // To post the message to the activity

        const val ALIVE = "Alive" // To check if the watch is connected
        const val SALARY = "Salary"
        const val COUNTDOWN_TIME = 2000L

        const val PACKAGE_NAME = "com.zivsion.handlerservice"
        const val RECEIVED_MESSAGE_KEY =
            "message_key" // To pass the message to the activity as intent extra key
        const val PATH = "/ZivChannelMessage" // To send the message to the wear

        const val MESSAGE_PATH =
            "com.zivsion.app.MESSAGE_PATH" // To get the message received from other apps

        const val SERVICE_BROADCAST_FILTER = "com.zivsion.handlerservice.MESSAGE"

        const val BOOT_ACTION = "android.intent.action.BOOT_COMPLETED"
    }
}