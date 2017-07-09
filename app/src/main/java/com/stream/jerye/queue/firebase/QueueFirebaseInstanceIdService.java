package com.stream.jerye.queue.firebase;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by jerye on 7/1/2017.
 */

public class QueueFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Registration Token", refreshedToken);

        SharedPreferences prefs = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        prefs.edit().putString("registration token", refreshedToken).apply();

    }
}
