package com.stream.jerye.queue.MusicPage;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.stream.jerye.queue.MainActivity;

import java.io.IOException;

public class MultiMediaPlayer implements Player, MediaPlayer.OnCompletionListener,SpotifyPlayer.NotificationCallback {

    private static final String TAG = MultiMediaPlayer.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private String mCurrentTrack;
    private SpotifyPlayer mSpotifyPlayer;
    private String mSpotifyAccessToken;
    private MusicQueueListener mMusicQueueListener;
    private Context mContext;

    private class OnPreparedListener implements MediaPlayer.OnPreparedListener {

        private final String mUrl;

        public OnPreparedListener(String url) {
            mUrl = url;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            mCurrentTrack = mUrl;
        }
    }

    public MultiMediaPlayer(Context context, MusicQueueListener musicQueueListener, String spotifyAccessToken){
        mContext = context;
        mMusicQueueListener = musicQueueListener;
        mSpotifyAccessToken = spotifyAccessToken;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        mMusicQueueListener.dequeue();

        release();
    }

    @Override
    public void play(String url) {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }

        try {
            Log.d("MainActivity.java", "Playing: " + url);
            createMediaPlayer(url);
            mCurrentTrack = url;
        } catch (IOException e) {
            Log.e(TAG, "Could not play: " + url, e);
        }
    }

    @Override
    public void pause() {
        Log.d(TAG, "Pause");
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mCurrentTrack = null;
    }

    @Override
    public void resume() {
        Log.d(TAG, "Resume");
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    @Nullable
    public String getCurrentTrack() {
        return mCurrentTrack;
    }

    @Override
    public void enqueue() {

    }

    private void createMediaPlayer(String url) throws IOException {

        //Android MediaPlayer
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDataSource(url);
        mMediaPlayer.setOnPreparedListener(new OnPreparedListener(url));
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.prepareAsync();

        //SpotifyMusicPlayer
        Config playerConfig = new Config(mContext, mSpotifyAccessToken, MainActivity.CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                mSpotifyPlayer = spotifyPlayer;

            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        switch (playerEvent){

        }
    }

    @Override
    public void onPlaybackError(Error error) {

    }
}
