package com.stream.jerye.queue;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.stream.jerye.queue.MessagePage.MessageFragment;
import com.stream.jerye.queue.MusicPage.MusicFragment;
import com.stream.jerye.queue.MusicPage.MusicQueueListener;
import com.stream.jerye.queue.MusicPage.PlayerService;
import com.stream.jerye.queue.MusicPage.QueuePlayer;
import com.stream.jerye.queue.MusicPage.SpotifyTrack;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MusicQueueListener, FirebaseEventBus.FirebaseListener {
    public static final String CLIENT_ID = "06a251bae8ae4881bb0022223b960c1d";
    private static final String REDIRECT_URI = "https://en.wikipedia.org/wiki/Whitelist";
    private static final int REQUEST_CODE = 42;
    private QueuePlayer mPlayer;
    private String TAG = "MainActivity.java";
    private String mToken;
    private FirebaseEventBus.MusicDatabaseAccess mMusicDatabaseAccess;
    private List<SpotifyTrack> mQueuedTracks;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Service Connected");
            mPlayer = ((PlayerService.PlayerBinder) service).getService(MainActivity.this, MainActivity.this, mToken);
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
    Button mPlayButton;
    @BindView(R.id.next_button)
    Button mNextButton;
    @BindView(R.id.previous_button)
    Button mPreviousButton;
    @BindView(R.id.music_seekbar)
    SeekBar mSeekBar;
    @BindView(R.id.music_current)
    TextView mCurrentMusicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        ButterKnife.bind(this);
        bindService(PlayerService.getIntent(this), mServiceConnection, Activity.BIND_AUTO_CREATE);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        mMusicDatabaseAccess = new FirebaseEventBus.MusicDatabaseAccess(this);
        mMusicDatabaseAccess.peek();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "play button clicked");

                if (mPlayer == null) {
                    Log.d("MainActivity.java", "mPlayer is null");
                    return;
                }
                if (mQueuedTracks != null) {
                    if (mPlayer.isPaused()) {
                        mPlayer.resume();
                        mPlayButton.setText("Pause");
                    } else if (mPlayer.isPlaying()) {
                        mPlayer.pause();
                        mPlayButton.setText("Play");
                    } else {
                        mPlayer.setNextTrack(mQueuedTracks.get(1).getTrack());
                        mPlayer.play(mQueuedTracks.get(0).getTrack());
                        mPlayButton.setText("Pause");
                    }
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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("MainActivity.java", "onActivityResult: " + requestCode);

        if (requestCode == REQUEST_CODE) {
            Log.d("MainActivity.java", "requestcode");

            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Log.d("MainActivity.java", "type: " + response.getType());

                mToken = response.getAccessToken();
                SharedPreferences prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                prefs.edit().putString("token", mToken).apply();

                mPager.setAdapter(new SimpleFragmentPageAdapter(getSupportFragmentManager()));
            }
        }
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
    public void peekedResult(List<SpotifyTrack> list) {
        mQueuedTracks = list;
    }

    @Override
    public void dequeue() {
//        Log.d("MainActivity.java", "QueueListener dequeue");
//
//
//        String current = mQueueMusicAdapter.peek();
//        Log.d("MainActivity.java", "current music: " + current);
//
//
//        mQueueMusicAdapter.dequeue(); //reroute to db eventually.
//
//        //
//        mCurrentMusicView.setText(current);
//
//        if (mQueueMusicAdapter.getItemCount() > 0)
//            mPlayer.setNextTrack(mQueueMusicAdapter.peekMore());
    }

    @Override
    public void getSongProgress(int positionInMs) {
        mSeekBar.setProgress(positionInMs);
    }

    @Override
    public void getSongDuration(int durationInMs) {
        mSeekBar.setMax(durationInMs);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);

    }
}
