package com.stream.jerye.queue;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.stream.jerye.queue.MessagePage.MessageFragment;
import com.stream.jerye.queue.MusicPage.MusicFragment;
import com.stream.jerye.queue.MusicPage.SimpleTrack;
import com.stream.jerye.queue.firebase.FirebaseEventBus;
import com.stream.jerye.queue.profile.SpotifyProfile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.UserPrivate;

public class RoomActivity extends AppCompatActivity implements
        MusicPlayerListener,
        FirebaseEventBus.FirebasePeekHandler,
        SpotifyProfile.SpotifyProfileCallback {
    private QueuePlayer mPlayer;
    private String TAG = "MainActivity.java";
    private static SharedPreferences prefs;
    private String mToken;
    private AnimatedVectorDrawable playToPause;
    private AnimatedVectorDrawable pauseToPlay;
    private FirebaseEventBus.MusicDatabaseAccess mMusicDatabaseAccess;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Service Connected");
            mPlayer = ((PlayerService.PlayerBinder) service).getService(RoomActivity.this, RoomActivity.this, mToken);
            Log.d(TAG, "peeking from activity result");
            mMusicDatabaseAccess.peek();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Service DisConnected");

            mPlayer = null;
        }
    };

    @BindView(R.id.view_pager)
    ViewPager mPager;
    @BindView(R.id.play_button)
    ImageView mPlayButton;
    @BindView(R.id.next_button)
    Button mNextButton;
    @BindView(R.id.previous_button)
    Button mPreviousButton;
    @BindView(R.id.music_seekbar)
    SeekBar mSeekBar;
    @BindView(R.id.music_current)
    TextView mCurrentMusicView;
    @BindView(R.id.music_duration)
    TextView mMusicDuration;
    @BindView(R.id.music_progress)
    TextView mMusicProgress;
    @BindView(R.id.profile_name)
    TextView mProfileName;
    @BindView(R.id.profile_picture)
    ImageView mProfilePicture;
    @BindView(R.id.profile_logout)
    Button mProfileLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_activity);
        ButterKnife.bind(this);
        prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        mToken = prefs.getString("token", "");

        mMusicDatabaseAccess = new FirebaseEventBus.MusicDatabaseAccess(this,this);

        playToPause = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_play_to_pause);
        pauseToPlay = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_pause_to_play);

        bindService(PlayerService.getIntent(this), mServiceConnection, Activity.BIND_AUTO_CREATE);

        mPager.setAdapter(new SimpleFragmentPageAdapter(getSupportFragmentManager()));

        SpotifyProfile spotifyProfile = new SpotifyProfile(this, mToken);
        spotifyProfile.getUserProfile();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "FirebaseInstanceId Token: " + FirebaseInstanceId.getInstance().getToken());
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "play button clicked");

                if (mPlayer == null) {
                    Log.d("MainActivity.java", "mPlayer is null");
                    return;
                }

                if (mPlayer.isPaused()) {
                    mPlayer.resume();
                    mPlayButton.setImageDrawable(playToPause);
                    playToPause.start();
                } else if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    mPlayButton.setImageDrawable(pauseToPlay);
                    pauseToPlay.start();
                } else {
                    mPlayer.play();
                    mPlayButton.setImageDrawable(playToPause);
                    playToPause.start();
                }


            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity.java", "Next button clicked");

                if (mPlayer == null) {
                    Log.d("MainActivity.java", "mPlayer is null");
                    return;
                }
                mPlayer.next();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.resume();
            }
        });

    }


    @Override
    public void createProfile(UserPrivate userPrivate) {
        String profileName = userPrivate.display_name;
        String profilePicture = userPrivate.images.get(0).url;
        String profileId = userPrivate.id;
        prefs.edit()
                .putString("profile name", profileName)
                .putString("profile picture url", profilePicture)
                .putString("profile id", profileId)
                .apply();

        mProfileName.setText(profileName);
        Picasso.with(this).load(profilePicture).into(mProfilePicture);
    }

    private class SimpleFragmentPageAdapter extends FragmentStatePagerAdapter {

        public SimpleFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MusicFragment.newInstance();
            } else {
                return MessageFragment.newInstance();
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }


    @Override
    public void peekedResult(List<SimpleTrack> list) {
        mPlayer.setNextTrack(list);
    }

    @Override
    public void getSongProgress(int positionInMs) {
        mSeekBar.setProgress(positionInMs);
        int totalInS = positionInMs / 1000;
        int minutes = totalInS / 60;
        int seconds = totalInS % 60;
        mMusicProgress.setText(minutes + ":" + (seconds < 10 ? "0" : "") + seconds);
    }

    @Override
    public void getSongDuration(int durationInMs) {
        Log.d(TAG, "setting max: " + durationInMs);
        mSeekBar.setMax(durationInMs);

        int totalInS = durationInMs / 1000;
        int minutes = totalInS / 60;
        int seconds = totalInS % 60;
        mMusicDuration.setText(minutes + ":" + (seconds < 10 ? "0" : "") + seconds);
    }

    @Override
    public void queueNextSong(SimpleTrack oldTrackToRemove) {
        Log.d(TAG, "queueNextSong called from player context end");
        mMusicDatabaseAccess.remove(oldTrackToRemove);
        mMusicDatabaseAccess.peek();
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);

    }


}
