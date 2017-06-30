package com.stream.jerye.queue;

import android.content.Context;
import android.content.UriMatcher;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;
import com.stream.jerye.queue.MusicPage.SimpleTrack;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiMediaPlayer implements QueuePlayer,
        MediaPlayer.OnCompletionListener,
        ConnectionStateCallback,
        PlayerStateCallback,
        PlayerNotificationCallback {

    private static final String TAG = MultiMediaPlayer.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private SimpleTrack mCurrentTrack;
    private SimpleTrack mNextTrack;
    private Player mSpotifyPlayer;
    private String mSpotifyAccessToken;
    private MusicPlayerListener mMusicPlayerListener;
    private Context mContext;
    private Handler mHandler = null;
    private MediaObserver mMediaObserver = null;
    private int mPositionInMs;
    private static boolean spotifyPlayerIsPlaying = false;
    private static boolean spotifyPlayerIsPaused = false;


    private static final int GENERAL_AUDIO_URL = 1;
    private static final int SPOTIFY_AUDIO_URL = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //https://p.scdn.co/mp3-preview/4839b070015ab7d6de9fec1756e1f3096d908fba
        sUriMatcher.addURI("https://", "*", GENERAL_AUDIO_URL);
        sUriMatcher.addURI("spotify:", "track:*", SPOTIFY_AUDIO_URL);
    }


    public MultiMediaPlayer(Context context, MusicPlayerListener musicPlayerListener, String spotifyAccessToken) {
        mContext = context;
        mMusicPlayerListener = musicPlayerListener;
        mSpotifyAccessToken = spotifyAccessToken;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        Log.d(TAG, "onCompletion");

        release();
    }

    @Override
    public void play() {
        if (mSpotifyPlayer != null) {
            mSpotifyPlayer.pause();
        }
        Log.d("MainActivity.java", "creating spotify player " + mCurrentTrack);

        createSpotifyAudioPlayer();

    }

    @Override
    public void pause() {
        Log.d(TAG, "Pause");
        if (mSpotifyPlayer != null) {
            mSpotifyPlayer.pause();

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
        if (mSpotifyPlayer != null) {
            mSpotifyPlayer.resume();

        }
    }

    @Override
    public void seekTo(int newPosition) {
        mSpotifyPlayer.seekToPosition(newPosition);
    }

    @Override
    public void next() {
        if (mSpotifyPlayer != null && mCurrentTrack != null) {
            playTrack(mCurrentTrack);
        }
    }

    @Override
    public boolean isPlaying() {
        return mSpotifyPlayer != null && (spotifyPlayerIsPlaying && !spotifyPlayerIsPaused);
    }

    @Override
    public boolean isPaused() {
        return mSpotifyPlayer != null && (!spotifyPlayerIsPlaying && spotifyPlayerIsPaused);
    }

    @Override
    public void setNextTrack(List<SimpleTrack> currentAndNextSong) {
        mCurrentTrack = currentAndNextSong.get(0);
        mNextTrack = currentAndNextSong.get(1);

    }


//    private void createAndroidMediaPlayer(String url) throws IOException {
//        //Android MediaPlayer
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.setDataSource(url);
//        mMediaPlayer.setOnPreparedListener(new OnPreparedListener(url));
//        mMediaPlayer.setOnCompletionListener(this);
//        mMediaPlayer.prepareAsync();
//
//    }


    private void createSpotifyAudioPlayer() {
        //SpotifyMusicPlayer
        Config playerConfig = new Config(mContext, mSpotifyAccessToken, RoomActivity.CLIENT_ID);

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
        playTrack(mCurrentTrack);
        mHandler = new Handler(Looper.getMainLooper());
        mMediaObserver = new MediaObserver();

    }

    @Override
    public void onLoggedOut() {
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d(TAG, throwable.toString());
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
            mHandler.postDelayed(mMediaObserver, 250);

        } else if (eventType.equals(EventType.AUDIO_FLUSH)) {
        } else if (eventType.equals(EventType.PAUSE)) {
            spotifyPlayerIsPaused = true;
            spotifyPlayerIsPlaying = false;
            mMediaObserver.stop();
        } else if (eventType.equals(EventType.END_OF_CONTEXT)) {
            playTrack(mNextTrack);
            mMusicPlayerListener.queueNextSong(mCurrentTrack);
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
    }

    @Override
    public void onPlayerState(PlayerState playerState) {
        mPositionInMs = playerState.positionInMs;
    }

    private class MediaObserver implements Runnable {
        private AtomicBoolean stop = new AtomicBoolean(false);

        public void stop() {
            stop.set(true);
        }

        public void startAgain() {
            stop.set(false);
        }

        @Override
        public void run() {
            if (!stop.get()) {
                mSpotifyPlayer.getPlayerState(MultiMediaPlayer.this);
                mMusicPlayerListener.getSongProgress(mPositionInMs);
                mHandler.postDelayed(mMediaObserver, 250);
            }
        }
    }

    private void playTrack(SimpleTrack track) {
        mSpotifyPlayer.play(track.getTrack());
        mMusicPlayerListener.getSongDuration((int) track.getDurationInMS());
        Log.d(TAG, "duration: " + track.getDurationInMS());
    }
}
