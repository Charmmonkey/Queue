package com.stream.jerye.queue.MusicPage;

import android.content.Context;
import android.content.UriMatcher;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.stream.jerye.queue.MainActivity;

import java.io.IOException;

public class MultiMediaPlayer implements Player, MediaPlayer.OnCompletionListener, ConnectionStateCallback {

    private static final String TAG = MultiMediaPlayer.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private String mCurrentTrack;
    private com.spotify.sdk.android.player.Player mSpotifyPlayer;
    private String mSpotifyAccessToken;
    private MusicQueueListener mMusicQueueListener;
    private Context mContext;

    private static final int GENERAL_AUDIO_URL = 1;
    private static final int SPOTIFY_AUDIO_URL = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //https://p.scdn.co/mp3-preview/4839b070015ab7d6de9fec1756e1f3096d908fba
        sUriMatcher.addURI("https://", "*", GENERAL_AUDIO_URL);
        sUriMatcher.addURI("spotify:", "track:*", SPOTIFY_AUDIO_URL);
    }

    private com.spotify.sdk.android.player.Player.OperationCallback mOperationCallback = new com.spotify.sdk.android.player.Player.OperationCallback() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "Operation success");

        }

        @Override
        public void onError(Error error) {
            Log.d(TAG, "Operation error" + error);

        }
    };

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

    public MultiMediaPlayer(Context context, MusicQueueListener musicQueueListener, String spotifyAccessToken) {
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
        Log.d("MainActivity.java", "pressed play: " + url);

        mCurrentTrack = url;
        if (url.contains("spotify")) {
            if (mSpotifyPlayer != null) {
                mSpotifyPlayer.pause(null);
            }
            Log.d("MainActivity.java", "creating spotify player " + url);


            createSpotifyAudioPlayer(url);


        } else {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
            }

            try {
                Log.d("MainActivity.java", "Playing: " + url);
                createAndroidMediaPlayer(url);
                mCurrentTrack = url;
            } catch (IOException e) {
                Log.e(TAG, "Could not play: " + url, e);
            }
        }


    }

    @Override
    public void pause() {
        Log.d(TAG, "Pause");
        if (mSpotifyPlayer != null) {
            mSpotifyPlayer.pause(null);

        }
//        if (mMediaPlayer != null) {
//            mMediaPlayer.pause();
//        }
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
        if (mSpotifyPlayer != null) {
            mSpotifyPlayer.resume(null);

        }
//        if (mMediaPlayer != null) {
//            mMediaPlayer.start();
//        }
    }

    @Override
    public boolean isPlaying() {
        return (mSpotifyPlayer != null && mSpotifyPlayer.getPlaybackState().isPlaying);
//                (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    @Override
    public boolean isPaused() {
        return mSpotifyPlayer != null && 0 < mSpotifyPlayer.getPlaybackState().positionMs && !mSpotifyPlayer.getPlaybackState().isPlaying;
    }

    @Override
    @Nullable
    public String getCurrentTrack() {
        return mCurrentTrack;
    }

    @Override
    public void enqueue() {

    }

    private void createAndroidMediaPlayer(String url) throws IOException {
        //Android MediaPlayer
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDataSource(url);
        mMediaPlayer.setOnPreparedListener(new OnPreparedListener(url));
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.prepareAsync();

    }


    private void createSpotifyAudioPlayer(final String url) {
        //SpotifyMusicPlayer
        Config playerConfig = new Config(mContext, mSpotifyAccessToken, MainActivity.CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                mSpotifyPlayer = spotifyPlayer;
                mSpotifyPlayer.addConnectionStateCallback(MultiMediaPlayer.this);
                Log.d("MainActivity.java", "initialized");

            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    @Override
    public void onLoggedIn() {
        Log.d(TAG, "logged in");
        mSpotifyPlayer.playUri(mOperationCallback, mCurrentTrack, 0, 0);

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d(TAG, "logged failed" + error);

    }

    @Override
    public void onTemporaryError() {
        Log.d(TAG, "logged temp error");

    }

    @Override
    public void onConnectionMessage(String s) {
        Log.d(TAG, "logged message" + s);

    }
}
