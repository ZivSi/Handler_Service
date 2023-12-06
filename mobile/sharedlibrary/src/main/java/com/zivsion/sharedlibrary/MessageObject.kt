package com.zivsion.sharedlibrary

import android.util.Log
import org.json.JSONObject

data class MessageObject(
    val from: String,
    val to: String,
    val receiverPackageName: String,
    val content: String,
    val action: String = "No Action",
    var sentTime: Long = 0
) {
    companion object {
        fun fromString(string: String): MessageObject {
            try {
                val jsonObject = JSONObject(string)

                return MessageObject(
                    jsonObject.getString("from"),
                    jsonObject.getString("to"),
                    jsonObject.getString("receiverPackageName"),
                    jsonObject.getString("content"),
                    jsonObject.getString("action"),
                    jsonObject.optLong("sentTime", 0)
                )
            } catch (e: Exception) {
                Log.e("MessageObject", "fromString: Couldn't parse $string")

                return MessageObject("Unknown", "Unknown", "Unknown", "Unknown", "Unknown", 0)
            }
        }
    }

    override fun toString(): String {
        val jsonObject = JSONObject()
        jsonObject.put("from", from)
        jsonObject.put("to", to)
        jsonObject.put("receiverPackageName", receiverPackageName)
        jsonObject.put("content", content)
        jsonObject.put("action", action)
        jsonObject.put("sentTime", sentTime)

        return jsonObject.toString()
    }

    fun print() {
        Log.d("MesssageProperties", "--------------------")
        Log.d("MesssageProperties", "Message properties:")
        Log.d("MesssageOrigin", "from: $from")
        Log.d("MesssageDest", "to: $to")
        Log.d("MesssageDest", "receiverPackageName: $receiverPackageName")
        Log.d(
            "MesssageContent",
            "content: ${content.substring(0, 25.coerceAtMost(content.length))}"
        )
        Log.d("MesssageAction", "action: $action")
        Log.d("MesssageTime", "sentTime: $sentTime")
        Log.d("MesssageProperties", "--------------------")
    }
}

/*
Example:
from: com.zivsion.appA
to: com.zivsion.appB
receiverPackageName: com.zivsion.appB.Receiver
content: "Hello World"
action: "No Action"
timestamp: 1234567890
 */