package com.stream.jerye.queue.MusicPage;

import android.content.Context;
import android.content.UriMatcher;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;
import com.stream.jerye.queue.MainActivity;

import java.io.IOException;

public class MultiMediaQueuePlayer implements QueuePlayer, MediaPlayer.OnCompletionListener, ConnectionStateCallback, PlayerNotificationCallback {

    private static final String TAG = MultiMediaQueuePlayer.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private String mCurrentTrack;
    private String mNextTrackUrl;
    private Player mSpotifyQueuePlayer;
    private String mSpotifyAccessToken;
    private MusicQueueListener mMusicQueueListener;
    private Context mContext;
    private static boolean spotifyPlayerIsPlaying = false;
    private static boolean spotifyPlayerIsPaused = false;
    private static boolean spotifyPlayerIsFresh = false;


    private static final int GENERAL_AUDIO_URL = 1;
    private static final int SPOTIFY_AUDIO_URL = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //https://p.scdn.co/mp3-preview/4839b070015ab7d6de9fec1756e1f3096d908fba
        sUriMatcher.addURI("https://", "*", GENERAL_AUDIO_URL);
        sUriMatcher.addURI("spotify:", "track:*", SPOTIFY_AUDIO_URL);
    }


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

    public MultiMediaQueuePlayer(Context context, MusicQueueListener musicQueueListener, String spotifyAccessToken) {
        mContext = context;
        mMusicQueueListener = musicQueueListener;
        mSpotifyAccessToken = spotifyAccessToken;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        Log.d(TAG, "onCompletion");
        mMusicQueueListener.dequeue();

        release();
    }

    @Override
    public void play(String url) {
        Log.d("MainActivity.java", "pressed play: " + url);

        mCurrentTrack = url;
        if (url.contains("spotify")) {
            if (mSpotifyQueuePlayer != null) {
                mSpotifyQueuePlayer.pause();
            }
            Log.d("MainActivity.java", "creating spotify player " + url);


            createSpotifyAudioPlayer();


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
        if (mSpotifyQueuePlayer != null) {
            mSpotifyQueuePlayer.pause();

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
        if (mSpotifyQueuePlayer != null) {
            mSpotifyQueuePlayer.resume();

        }
//        if (mMediaPlayer != null) {
//            mMediaPlayer.start();
//        }
    }

    @Override
    public void next() {
        if (mSpotifyQueuePlayer != null && mNextTrackUrl != null) {
            mSpotifyQueuePlayer.skipToNext();

        }
    }

    @Override
    public boolean isPlaying() {
        return mSpotifyQueuePlayer != null && (spotifyPlayerIsPlaying && !spotifyPlayerIsPaused);
//                (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    @Override
    public boolean isPaused() {
        return mSpotifyQueuePlayer != null && (!spotifyPlayerIsPlaying && spotifyPlayerIsPaused);
    }

    @Override
    @Nullable
    public String getCurrentTrack() {
        return mCurrentTrack;
    }

    @Override
    public void setNextTrack(String nextTrackUrl) {
        mNextTrackUrl = nextTrackUrl;
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


    private void createSpotifyAudioPlayer() {
        //SpotifyMusicPlayer
        Config playerConfig = new Config(mContext, mSpotifyAccessToken, MainActivity.CLIENT_ID);

        Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mSpotifyQueuePlayer = player;
                mSpotifyQueuePlayer.addConnectionStateCallback(MultiMediaQueuePlayer.this);
                mSpotifyQueuePlayer.addPlayerNotificationCallback(MultiMediaQueuePlayer.this);
                Log.d("MainActivity.java", "initialized");
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    @Override
    public void onLoggedIn() {
        Log.d(TAG, "logged in");
        mSpotifyQueuePlayer.play(mCurrentTrack);
        mMusicQueueListener.dequeue();
    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Throwable throwable) {

    }


    @Override
    public void onTemporaryError() {
        Log.d(TAG, "logged temp error");

    }

    @Override
    public void onConnectionMessage(String s) {
        Log.d(TAG, "logged message" + s);

    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

        Log.d(TAG, "event type: " + eventType);
        if (eventType.equals(EventType.PLAY)) {
            spotifyPlayerIsPlaying = true;
            spotifyPlayerIsPaused = false;
        } else if (eventType.equals(EventType.PAUSE)) {
            spotifyPlayerIsPaused = true;
            spotifyPlayerIsPlaying = false;
        } else if (eventType.equals(EventType.END_OF_CONTEXT)) {
            mSpotifyQueuePlayer.queue(mNextTrackUrl);
            mMusicQueueListener.dequeue();
        }


    }


    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }


}
