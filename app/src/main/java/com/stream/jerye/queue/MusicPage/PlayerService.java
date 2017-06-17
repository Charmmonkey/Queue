package com.stream.jerye.queue.MusicPage;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class PlayerService extends Service {

    private final IBinder mBinder = new PlayerBinder();
    private Player mPlayer;

    public static Intent getIntent(Context context) {
        return new Intent(context, PlayerService.class);
    }

    public class PlayerBinder extends Binder {
        public Player getService(Context context, MusicQueueListener musicQueueListener, String spotifyAccessToken) {
            mPlayer = new MultiMediaPlayer(context, musicQueueListener, spotifyAccessToken);
            return mPlayer;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mPlayer.release();
        super.onDestroy();
    }
}
