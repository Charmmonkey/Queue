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
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;
import com.stream.jerye.queue.MainActivity;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiMediaPlayer implements QueuePlayer,
        MediaPlayer.OnCompletionListener,
        ConnectionStateCallback,
        PlayerStateCallback,
        PlayerNotificationCallback {

    private static final String TAG = MultiMediaPlayer.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private String mCurrentTrack;
    private String mNextTrackUrl;
    private Player mSpotifyPlayer;
    private String mSpotifyAccessToken;
    private MusicQueueListener mMusicQueueListener;
    private Context mContext;
    private MediaObserver mMediaObserver = null;
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

    public MultiMediaPlayer(Context context, MusicQueueListener musicQueueListener, String spotifyAccessToken) {
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
            if (mSpotifyPlayer != null) {
                mSpotifyPlayer.pause();
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
        if (mSpotifyPlayer != null) {
            mSpotifyPlayer.pause();

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
            mSpotifyPlayer.resume();

        }
//        if (mMediaPlayer != null) {
//            mMediaPlayer.start();
//        }
    }

    @Override
    public void next() {
        if (mSpotifyPlayer != null && mNextTrackUrl != null) {
            mSpotifyPlayer.skipToNext();

        }
    }

    @Override
    public boolean isPlaying() {
        return mSpotifyPlayer != null && (spotifyPlayerIsPlaying && !spotifyPlayerIsPaused);
//                (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    @Override
    public boolean isPaused() {
        return mSpotifyPlayer != null && (!spotifyPlayerIsPlaying && spotifyPlayerIsPaused);
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
                mSpotifyPlayer = player;
                mSpotifyPlayer.addConnectionStateCallback(MultiMediaPlayer.this);
                mSpotifyPlayer.addPlayerNotificationCallback(MultiMediaPlayer.this);
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
        mSpotifyPlayer.play(mCurrentTrack);
        mSpotifyPlayer.queue(mNextTrackUrl);
        mMusicQueueListener.dequeue();
        mMediaObserver = new MediaObserver();

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
            mMediaObserver.startAgain();
            new Thread(mMediaObserver).start();
        } else if (eventType.equals(EventType.TRACK_START)) {

        } else if (eventType.equals(EventType.PAUSE)) {
            spotifyPlayerIsPaused = true;
            spotifyPlayerIsPlaying = false;
            mMediaObserver.stop();
        } else if (eventType.equals(EventType.END_OF_CONTEXT)) {
            mSpotifyPlayer.queue(mNextTrackUrl);
//            mMusicQueueListener.dequeue();
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
    }

    @Override
    public void onPlayerState(PlayerState playerState) {
    }

    private class MediaObserver implements Runnable {
        private AtomicBoolean stop = new AtomicBoolean(false);
        private int mPositionInMs;
        private int mDurationInMs;
        private PlayerStateCallback playerStateCallback = new PlayerStateCallback() {
            @Override
            public void onPlayerState(PlayerState playerState) {
                mPositionInMs = playerState.positionInMs;
                mDurationInMs = playerState.durationInMs;
            }
        };

        public void stop() {
            stop.set(true);
        }

        public void startAgain(){
            stop.set(false);
        }

        @Override
        public void run() {
            while (!stop.get()) {
                mSpotifyPlayer.getPlayerState(playerStateCallback);
                mMusicQueueListener.getSongProgress(mPositionInMs);
                mMusicQueueListener.getSongDuraction(mDurationInMs);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                }

            }
        }
    }


}
