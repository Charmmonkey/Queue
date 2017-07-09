package com.stream.jerye.queue.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class QueueFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "FCM.java";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Notification Received");

        if (remoteMessage.getNotification() != null) {
            String msg = remoteMessage.getNotification().getBody();
            Log.d(TAG, msg);

            FirebaseEventBus.MessageDatabaseAccess mMessageDatabaseAccess = new FirebaseEventBus.MessageDatabaseAccess(this);
            mMessageDatabaseAccess.push(msg,"");
        }
    }


}
