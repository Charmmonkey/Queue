package com.stream.jerye.queue;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class PlayerService extends Service {

    private final IBinder mBinder = new PlayerBinder();
    private QueuePlayer mQueuePlayer;

    public static Intent getIntent(Context context) {
        return new Intent(context, PlayerService.class);
    }

    public class PlayerBinder extends Binder {
        public QueuePlayer getService(Context context, MusicPlayerListener musicPlayerListener, String spotifyAccessToken) {
            mQueuePlayer = new MultiMediaPlayer(context, musicPlayerListener, spotifyAccessToken);
            return mQueuePlayer;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mQueuePlayer.release();
        super.onDestroy();
    }
}
